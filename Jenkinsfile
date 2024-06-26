// pipeline {
//     agent any
//
//     environment {
//         SSH_PRIVATE_KEY = credentials('SSH_PRIVATE_KEY')
//         REMOTE_USER = credentials('SSH_USER')
//         REMOTE_HOST = credentials('SSH_HOST')
//         REMOTE_DIR = credentials('REMOTE_DIR')
//         YES25_5_MYSQL_PASSWORD = credentials('YES25_5_MYSQL_PASSWORD')
//         EUREKA_SERVER_HOSTNAME = credentials('EUREKA_SERVER_HOSTNAME')
//         EUREKA_SERVER_PORT = credentials('EUREKA_SERVER_PORT')
//         DOORAY_WEBHOOK_URL = credentials('DOORAY_WEBHOOK_URL')
//     }
//
//     stages {
//         stage('Checkout') {
//             steps {
//                 script {
//                     // Checkout code from SCM (Git repository)
//                     checkout scm
//                 }
//             }
//         }
//
//         stage('Set up SSH') {
//             steps {
//                 script {
//                     // Set up SSH on the agent machine
//                     writeFile file: "${env.HOME}/.ssh/id_rsa", text: env.SSH_PRIVATE_KEY
//                     sh """
//                     chmod 600 ${env.HOME}/.ssh/id_rsa
//                     ssh-keyscan ${env.REMOTE_HOST} >> ${env.HOME}/.ssh/known_hosts
//                     """
//                 }
//             }
//         }
//
//         stage('Build and Test with Maven') {
//             steps {
//                 script {
//                     // Verify Dockerfile existence
//                     sh """
//                     if [ ! -f Dockerfile-8060 ]; then
//                         echo "Dockerfile not found!"
//                         exit 1
//                     fi
//                     """
//
//                     // Build Docker image
//                     sh "docker build -f Dockerfile-8060 --no-cache -t books-users-app ."
//
//                     // Run Maven tests in Docker
//                     sh """
//                     docker run --rm books-users-app mvn clean verify sonar:sonar \
//                       -Dsonar.projectKey=yes25-5-books-user \
//                       -Dsonar.projectName='yes25-5-books-user' \
//                       -Dsonar.host.url=${env.SONAR_HOST_URL} \
//                       -Dsonar.token=${env.SONAR_TOKEN}
//                     """
//
//                     // Build Maven project in Docker
//                     sh "docker run --rm -v \$(pwd):/app -w /app books-users-app mvn package"
//
//                     // Check if JAR file exists
//                     sh """
//                     if [ ! -f target/*.jar ]; then
//                         echo "JAR file not found!"
//                         exit 1
//                     fi
//                     """
//                 }
//             }
//         }
//
//         stage('Deploy JAR on Remote Server') {
//             steps {
//                 script {
//                     sshagent(credentials: ['SSH_PRIVATE_KEY']) {
//                         sh """
//                         scp -i ~/.ssh/id_rsa -o StrictHostKeyChecking=no target/*.jar ${env.REMOTE_USER}@${env.REMOTE_HOST}:${env.REMOTE_DIR}
//                         """
//                     }
//
//                     sshagent(credentials: ['SSH_PRIVATE_KEY']) {
//                         sh """
//                         ssh -i ~/.ssh/id_rsa -o StrictHostKeyChecking=no ${env.REMOTE_USER}@${env.REMOTE_HOST} << EOF
//                             cd ${env.REMOTE_DIR}
//                             if [ ! -f Dockerfile ]; then
//                                 echo "Dockerfile not found!"
//                                 exit 1
//                             fi
//                             docker build -t books-users-app .
//                             docker stop books-users-app || true
//                             docker rm books-users-app || true
//                             docker run -d -p 8060:8060 --name books-users-app \\
//                               -e YES25_5_MYSQL_PASSWORD=${env.YES25_5_MYSQL_PASSWORD} \\
//                               -e EUREKA_SERVER_HOSTNAME=${env.EUREKA_SERVER_HOSTNAME} \\
//                               -e EUREKA_SERVER_PORT=${env.EUREKA_SERVER_PORT} \\
//                               books-users-app
//                         EOF
//                         """
//                     }
//                 }
//             }
//         }
//     }
//
//     post {
//         success {
//             script {
//                 sendToDooray('green', '도서 회원 서버의 배포가 성공적으로 완료되었습니다!')
//             }
//         }
//
//         failure {
//             script {
//                 sendToDooray('red', '도서 회원 서버의 배포가 실패했습니다...')
//             }
//         }
//     }
// }
//
// def sendToDooray(color, message) {
//     script {
//         def payload = [
//             botName: '도서 회원 서버 Bot',
//             botIconImage: 'https://www.tistory.com/favicon.ico',
//             text: message,
//             attachments: [
//                 [
//                     title: 'Pull Request URL',
//                     titleLink: env.PR_URL,
//                     color: color,
//                     text: "PR 제목: ${env.PR_TITLE}, PR 작성자: ${env.PR_ACTOR}"
//                 ]
//             ]
//         ]
//
//         def response = httpRequest(
//             contentType: 'APPLICATION_JSON',
//             url: env.DOORAY_WEBHOOK_URL,
//             requestBody: toJson(payload)
//         )
//
//         if (response.status != 200) {
//             error "Failed to send webhook: ${response.status} - ${response.content}"
//         }
//     }
// }