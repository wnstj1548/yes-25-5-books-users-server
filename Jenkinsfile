pipeline {
    agent any

    environment {
        SSH_PRIVATE_KEY = credentials('SSH_PRIVATE_KEY')
        SSH_USER = credentials('SSH_USER')
        SSH_HOST = credentials('SSH_HOST')
        REMOTE_DIR = credentials('REMOTE_DIR')
        YES25_5_MYSQL_PASSWORD = credentials('YES25_5_MYSQL_PASSWORD')
        EUREKA_SERVER_HOSTNAME = credentials('EUREKA_SERVER_HOSTNAME')
        EUREKA_SERVER_PORT = credentials('EUREKA_SERVER_PORT')
        DOORAY_WEBHOOK_URL = credentials('DOORAY_WEBHOOK_URL')
    }

    stages {
        stage('Build and Test with Maven') {
            steps {
                script {
                    sh """
                    if [ ! -f Dockerfile-8060 ]; then
                        echo "Dockerfile-8060 not found!"
                        exit 1
                    fi
                    """

                    sh "docker build -f Dockerfile-8060 --no-cache -t books-users-app-8060 ."

                    sh """
                    docker run --rm books-users-app-8060 mvn clean verify sonar:sonar \
                      -Dsonar.projectKey=yes-25-5-books-users \
                      -Dsonar.projectName='yes-25-5-books-users' \
                      -Dsonar.host.url=${SONAR_HOST_URL} \
                      -Dsonar.token=${SONAR_TOKEN}
                    """

                    sh "docker run --rm -v \$(pwd):/app -w /app books-users-app-8060 mvn package"

                    sh """
                    if [ ! -f target/*.jar ]; then
                        echo "JAR file not found!"
                        exit 1
                    fi
                    """
                }
            }
        }

        stage('Deploy JARs on Remote Server') {
            steps {
                script {
                    writeFile file: "${env.HOME}/.ssh/id_rsa", text: env.SSH_PRIVATE_KEY
                    sh """
                    chmod 600 ${env.HOME}/.ssh/id_rsa
                    ssh-keyscan ${env.SSH_HOST} >> ${env.HOME}/.ssh/known_hosts
                    """

                    sshagent(credentials: ['SSH_PRIVATE_KEY']) {
                        sh "ssh -i ${env.HOME}/.ssh/id_rsa -o StrictHostKeyChecking=no ${env.SSH_USER}@${env.SSH_HOST} 'rm -rf ${env.REMOTE_DIR}/*'"
                    }

                    sshagent(credentials: ['SSH_PRIVATE_KEY']) {
                        sh """
                        scp -i ${env.HOME}/.ssh/id_rsa -o StrictHostKeyChecking=no Dockerfile-8060 ${env.SSH_USER}@${env.SSH_HOST}:${env.REMOTE_DIR}/Dockerfile-8060
                        scp -i ${env.HOME}/.ssh/id_rsa -o StrictHostKeyChecking=no pom.xml ${env.SSH_USER}@${env.SSH_HOST}:${env.REMOTE_DIR}/pom.xml
                        scp -i ${env.HOME}/.ssh/id_rsa -o StrictHostKeyChecking=no -r src ${env.SSH_USER}@${env.SSH_HOST}:${env.REMOTE_DIR}/src
                        scp -i ${env.HOME}/.ssh/id_rsa -o StrictHostKeyChecking=no -r src/main/resources/application-prod.yml ${env.SSH_USER}@${env.SSH_HOST}:${env.REMOTE_DIR}/src/main/resources/application-prod.yml
                        """
                    }

                    sshagent(credentials: ['SSH_PRIVATE_KEY']) {
                        sh """
                        ssh -i ${env.HOME}/.ssh/id_rsa -o StrictHostKeyChecking=no ${env.SSH_USER}@${env.SSH_HOST} << EOF
                            cd ${env.REMOTE_DIR}
                            docker build -t books-users-app-8060 -f Dockerfile-8060 .
                            docker stop books-users-app-8060 || true
                            docker rm books-users-app-8060 || true
                            docker run -d -p 8060:8060 --name books-users-app-8060 \\
                              -e EUREKA_SERVER_HOSTNAME=${env.EUREKA_SERVER_HOSTNAME} \\
                              -e EUREKA_SERVER_PORT=${env.EUREKA_SERVER_PORT} \\
                              -e YES25_5_MYSQL_PASSWORD=${env.YES25_5_MYSQL_PASSWORD} \\
                              books-users-app-8060
                        EOF
                        """
                    }

                    sshagent(credentials: ['SSH_PRIVATE_KEY']) {
                        sh """
                        until ssh -i ${env.HOME}/.ssh/id_rsa -o StrictHostKeyChecking=no ${env.SSH_USER}@${env.SSH_HOST} "curl -s http://${env.SSH_HOST}:8060/actuator/health | grep -q '\"status\":\"UP\"'"; do
                            echo "Waiting for 8060 to be up..."
                            sleep 10
                        done
                        """
                    }

                    sshagent(credentials: ['SSH_PRIVATE_KEY']) {
                        sh """
                        scp -i ${env.HOME}/.ssh/id_rsa -o StrictHostKeyChecking=no Dockerfile-8061 ${env.SSH_USER}@${env.SSH_HOST}:${env.REMOTE_DIR}/Dockerfile-8061
                        scp -i ${env.HOME}/.ssh/id_rsa -o StrictHostKeyChecking=no pom.xml ${env.SSH_USER}@${env.SSH_HOST}:${env.REMOTE_DIR}/pom.xml
                        scp -i ${env.HOME}/.ssh/id_rsa -o StrictHostKeyChecking=no -r src ${env.SSH_USER}@${env.SSH_HOST}:${env.REMOTE_DIR}/src
                        scp -i ${env.HOME}/.ssh/id_rsa -o StrictHostKeyChecking=no -r src/main/resources/application-prod.yml ${env.SSH_USER}@${env.SSH_HOST}:${env.REMOTE_DIR}/src/main/resources/application-prod.yml
                        """
                    }

                    // Deploy JAR on Port 8061
                    sshagent(credentials: ['SSH_PRIVATE_KEY']) {
                        sh """
                        ssh -i ${env.HOME}/.ssh/id_rsa -o StrictHostKeyChecking=no ${env.SSH_USER}@${env.SSH_HOST} << EOF
                            cd ${env.REMOTE_DIR}
                            docker build -t books-users-app-8061 -f Dockerfile-8061 .
                            docker stop books-users-app-8061 || true
                            docker rm books-users-app-8061 || true
                            docker run -d -p 8061:8061 --name books-users-app-8061 \\
                              -e EUREKA_SERVER_HOSTNAME=${env.EUREKA_SERVER_HOSTNAME} \\
                              -e EUREKA_SERVER_PORT=${env.EUREKA_SERVER_PORT} \\
                              -e YES25_5_MYSQL_PASSWORD=${env.YES25_5_MYSQL_PASSWORD} \\
                              books-users-app-8061
                        EOF
                        """
                    }

                    // Wait for 8061 to be up
                    sshagent(credentials: ['SSH_PRIVATE_KEY']) {
                        sh """
                        until ssh -i ${env.HOME}/.ssh/id_rsa -o StrictHostKeyChecking=no ${env.SSH_USER}@${env.SSH_HOST} "curl -s http://${env.SSH_HOST}:8061/actuator/health | grep -q '\"status\":\"UP\"'"; do
                            echo "Waiting for 8061 to be up..."
                            sleep 10
                        done
                        """
                    }
                }
            }
        }

        post {
            success {
                script {
                    sendToDooray('green', '도서 회원 서버의 배포가 성공적으로 완료되었습니다!')
                }
            }

            failure {
                script {
                    sendToDooray('red', '도서 회원 서버의 배포가 실패했습니다...')
                }
            }
        }
    }
}

def sendToDooray(color, message) {
    script {
        def payload = [
            botName: '도서 회원 서버 Bot',
            botIconImage: 'https://www.tistory.com/favicon.ico',
            text: message,
            attachments: [
                [
                    title: 'Pull Request URL',
                    titleLink: env.PR_URL,
                    color: color,
