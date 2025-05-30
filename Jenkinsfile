pipeline {
  agent any

  triggers {
    githubPush()
  }

  stages {
    stage('Deploy') {
      steps {
        withCredentials([string(credentialsId: 'discord-webhook', variable: 'WEBHOOK_URL')]) {
          dir('/home/ubuntu/zaro-main') {
            sh '''
              echo "📥 Git pull 중..."
              if git pull origin main; then
                  curl -H "Content-Type: application/json" -X POST \
                    -d '{"content": "✅ [Auto] Git pull 성공!"}' $WEBHOOK_URL
              else
                  curl -H "Content-Type: application/json" -X POST \
                    -d '{"content": "❌ [Auto] Git pull 실패!"}' $WEBHOOK_URL
                  exit 1
              fi

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