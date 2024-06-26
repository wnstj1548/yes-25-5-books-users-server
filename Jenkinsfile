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
        stage('Checkout Code') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/develop']],
                          doGenerateSubmoduleConfigurations: false,
                          extensions: [[$class: 'CleanCheckout']],
                          submoduleCfg: [],
                          userRemoteConfigs: [[
                              url: 'https://github.com/nhnacademy-be6-yes-25-5/yes-25-5-books-users-server.git',
                              credentialsId: 'deploy'
                          ]]
                ])
            }
        }

        stage('Verify Dockerfile Exists') {
            steps {
                script {
                    if (!fileExists('Dockerfile')) {
                        error('Dockerfile not found!')
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build --no-cache -t books-users-app .'

            }
        }

        stage('Run Maven Tests in Docker') {
            steps {
                sh '''
                docker run --rm books-users-app mvn clean verify sonar:sonar \
                    -Dspring.profiles.active=ci \
                    -Dsonar.projectKey=yes-25-5-books-users \
                    -Dsonar.projectName="yes-25-5-books-users" \
                    -Dsonar.host.url=${SONAR_HOST_URL} \
                    -Dsonar.token=${SONAR_TOKEN}
                '''
            }
        }

        stage('Build Maven Project in Docker') {
            steps {
                sh '''
                docker run --rm -v "$(pwd)":/app -w /app books-users-app mvn package \
                    -Dspring.profiles.active=ci
                '''
            }
        }

        stage('Check if JAR File Exists') {
            steps {
                script {
                    if (!fileExists('target/*.jar')) {
                        error('JAR file not found!')
                    }
                }
            }
        }

        stage('Publish Unit Test Results') {
            steps {
                junit '**/target/surefire-reports/**/*.xml'
            }
        }

        stage('Upload JAR to Remote Server') {
            steps {
                sshagent(credentials: ['SSH_PRIVATE_KEY']) {
                    sh '''
                    scp -o StrictHostKeyChecking=no target/*.jar ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR}
                    '''
                }
            }
        }

        stage('Deploy JAR on Remote Server') {
            steps {
                sshagent(credentials: ['SSH_PRIVATE_KEY']) {
                    sh '''
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

    post {
        success {
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

        failure {
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