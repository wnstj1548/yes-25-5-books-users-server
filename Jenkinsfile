pipeline {
    agent any

    environment {
        SSH_PRIVATE_KEY = credentials('SSH_PRIVATE_KEY')
        REMOTE_USER = credentials('SSH_USER')
        REMOTE_HOST = credentials('SSH_HOST')
        REMOTE_DIR = credentials('REMOTE_DIR')
        YES25_5_MYSQL_PASSWORD = credentials('YES25_5_MYSQL_PASSWORD')
        EUREKA_SERVER_HOSTNAME = credentials('EUREKA_SERVER_HOSTNAME')
        EUREKA_SERVER_PORT = credentials('EUREKA_SERVER_PORT')
        SONAR_HOST_URL = credentials('SONAR_HOST_URL')
        SONAR_TOKEN = credentials('SONAR_TOKEN')
        DOORAY_WEBHOOK_URL = credentials('DOORAY_WEBHOOK_URL')
    }

    stages {
        stage('Build Docker Image') {
            steps {
                checkout scm

                script {
                    if (!fileExists('Dockerfile')) {
                        error "Dockerfile not found!"
                    }
                }

                sh 'docker build --no-cache -t books-users-app .'
            }
        }

        stage('Run Maven tests in Docker') {
            steps {
                script {
                    sh 'docker run --rm books-users-app mvn clean verify sonar:sonar ' +
                       "-Dspring.profile.active=ci " +
                       "-Dsonar.projectKey=yes-25-5-books-users " +
                       "-Dsonar.projectName='yes-25-5-books-users' " +
                       "-Dsonar.host.url=${SONAR_HOST_URL} " +
                       "-Dsonar.token=${SONAR_TOKEN}"
                }
            }
        }

        stage('Build Maven project in Docker') {
            steps {
                script {
                    sh 'docker run --rm -v "$(pwd)":/app -w /app books-users-app mvn package ' +
                       "-Dspring.profiles.active=ci"
                }
            }
        }

        stage('Publish Unit Test Results') {
            steps {
                script {
                    junit '**/target/surefire-reports/**/*.xml'
                }
            }
        }

        stage('Upload JAR to Remote Server') {
            steps {
                script {
                    sshagent(['SSH_PRIVATE_KEY']) {
                        sh "scp -o StrictHostKeyChecking=no target/*.jar ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR}"
                    }
                }
            }
        }

        stage('Send Dooray Webhook on Success') {
            when {
                success()
            }
            steps {
                script {
                    def payload = [
                        botName: '도서 회원 서버 Bot',
                        botIconImage: 'https://www.tistory.com/favicon.ico',
                        text: '도서 회원 서버의 Pull Request가 성공적으로 올라갔어요!',
                        attachments: [
                            [
                                title: 'Pull Request URL',
                                titleLink: env.PR_URL,
                                color: 'green',
                                text: "PR 제목: ${env.PR_TITLE}, PR 작성자: ${env.PR_ACTOR}"
                            ]
                        ]
                    ]

                    httpRequest contentType: 'APPLICATION_JSON',
                                url: "${DOORAY_WEBHOOK_URL}",
                                requestBody: groovy.json.JsonOutput.toJson(payload)
                }
            }
        }

        stage('Send Dooray Webhook on Failure') {
            when {
                failure()
            }
            steps {
                script {
                    def payload = [
                        botName: '도서 회원 서버 Bot',
                        botIconImage: 'https://www.tistory.com/favicon.ico',
                        text: '도서 회원 서버의 Pull Request가 실패했어요...',
                        attachments: [
                            [
                                title: 'Pull Request URL',
                                titleLink: env.PR_URL,
                                color: 'red',
                                text: "PR 제목: ${env.PR_TITLE}, PR 작성자: ${env.PR_ACTOR}"
                            ]
                        ]
                    ]

                    httpRequest contentType: 'APPLICATION_JSON',
                                url: "${DOORAY_WEBHOOK_URL}",
                                requestBody: groovy.json.JsonOutput.toJson(payload)
                }
            }
        }

        stage('Deploy JAR on Remote Server') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    sshagent(['SSH_PRIVATE_KEY']) {
                        sh '''
                            scp -o StrictHostKeyChecking=no Dockerfile ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR}/Dockerfile
                            scp -o StrictHostKeyChecking=no pom.xml ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR}/pom.xml
                            scp -o StrictHostKeyChecking=no -r src ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR}/src
                            scp -o StrictHostKeyChecking=no src/main/resources/application-prod.yml ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR}/src/main/resources/application-prod.yml

                            ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "ls -la ${REMOTE_DIR}/Dockerfile"
                            ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "ls -la ${REMOTE_DIR}/pom.xml"
                            ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "ls -la ${REMOTE_DIR}/src"
                            ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "ls -la ${REMOTE_DIR}/src/main/resources/application-prod.yml"

                            ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} << EOF
                                cd ${REMOTE_DIR}
                                if [ ! -f Dockerfile ]; then
                                  echo "Dockerfile not found!"
                                  exit 1
                                fi
                                docker build -t books-users-app .
                                docker stop books-users-app || true
                                docker rm books-users-app || true
                                docker run -d -p 8060:8060 --name books-users-app \
                                  -e YES25_5_MYSQL_PASSWORD=${YES25_5_MYSQL_PASSWORD} \
                                  -e EUREKA_SERVER_HOSTNAME=${EUREKA_SERVER_HOSTNAME} \
                                  -e EUREKA_SERVER_PORT=${EUREKA_SERVER_PORT} \
                                  books-users-app
                            EOF
                        '''
                    }
                }
            }
        }

        stage('Send Deployment Status to Dooray') {
            when {
                branch 'main'
                success()
            }
            steps {
                script {
                    def payload = [
                        botName: '도서 회원 서버 Bot',
                        botIconImage: 'https://www.tistory.com/favicon.ico',
                        text: '도서 회원 서버의 배포가 성공적으로 완료되었습니다!',
                        attachments: [
                            [
                                title: 'Pull Request URL',
                                titleLink: env.PR_URL,
                                color: 'green',
                                text: "PR 제목: ${env.PR_TITLE}, PR 작성자: ${env.PR_ACTOR}"
                            ]
                        ]
                    ]

                    httpRequest contentType: 'APPLICATION_JSON',
                                url: "${DOORAY_WEBHOOK_URL}",
                                requestBody: groovy.json.JsonOutput.toJson(payload)
                }
            }
        }

        stage('Send Deployment Failure to Dooray') {
            when {
                branch 'main'
                failure()
            }
            steps {
                script {
                    def payload = [
                        botName: '도서 회원 서버 Bot',
                        botIconImage: 'https://www.tistory.com/favicon.ico',
                        text: '도서 회원 서버의 배포가 실패했습니다...',
                        attachments: [
                            [
                                title: 'Pull Request URL',
                                titleLink: env.PR_URL,
                                color: 'red',
                                text: "PR 제목: ${env.PR_TITLE}, PR 작성자: ${env.PR_ACTOR}"
                            ]
                        ]
                    ]

                    httpRequest contentType: 'APPLICATION_JSON',
                                url: "${DOORAY_WEBHOOK_URL}",
                                requestBody: groovy.json.JsonOutput.toJson(payload)
                }
            }
        }
    }
}