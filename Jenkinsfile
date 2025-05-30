pipeline {
    agent any

    environment {
        PROJECT_DIR = "/home/ubuntu/zaro-main"
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Git Pull') {
            steps {
                withCredentials([
                    usernamePassword(credentialsId: 'github-token', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN'),
                    string(credentialsId: 'discord-webhook', variable: 'WEBHOOK_URL')
                ]) {
                    dir("${PROJECT_DIR}") {
                        sh '''
                            echo "📥 Git pull 중..."
                            if git pull https://$GIT_USER:$GIT_TOKEN@github.com/code-is-evenly-cooked/even-zaro-back.git main; then
                                curl -H "Content-Type: application/json" -X POST \
                                  -d '{"content": "✅ [Auto] Git pull 성공!"}' $WEBHOOK_URL
                            else
                                curl -H "Content-Type: application/json" -X POST \
                                  -d '{"content": "❌ [Auto] Git pull 실패!"}' $WEBHOOK_URL
                                exit 1
                            fi
                        '''
                    }
                }
            }
        }

        stage('Docker Deploy') {
            steps {
                withCredentials([string(credentialsId: 'discord-webhook', variable: 'WEBHOOK_URL')]) {
                    dir("${PROJECT_DIR}") {
                        sh '''
                            curl -H "Content-Type: application/json" -X POST \
                              -d '{"content": "🐳 [Auto] Docker 배포 시작..."}' $WEBHOOK_URL

                            docker-compose -f docker-compose.prod.yml down --rmi all

                            if docker-compose -f docker-compose.prod.yml up -d --build; then
                                curl -H "Content-Type: application/json" -X POST \
                                  -d '{"content": "🚀 [Auto] 배포 완료!"}' $WEBHOOK_URL
                            else
                                curl -H "Content-Type: application/json" -X POST \
                                  -d '{"content": "❌ [Auto] Docker 실패"}' $WEBHOOK_URL
                                exit 1
                            fi
                        '''
                    }
                }
            }
        }
    }
}