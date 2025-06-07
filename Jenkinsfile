pipeline {
  options {
    disableConcurrentBuilds()
  }

  agent any

  environment {
    PROJECT_DIR = "/home/ubuntu/zaro-main"
    A_IP = "${params.A_IP}"
    B_IP = "${params.B_IP}"
  }

  triggers {
    githubPush()
  }

  stages {

    stage('프록시 B로 전환') {
      steps {
        withCredentials([
          sshUserPrivateKey(credentialsId: 'server-key', keyFileVariable: 'PEM_FILE'),
          string(credentialsId: 'discord-webhook', variable: 'WEBHOOK_URL')
        ]) {
          script {
            def isB = sh(
              script: """
                ssh -i "$PEM_FILE" -o StrictHostKeyChecking=no ubuntu@${A_IP} 'grep -q "proxy_pass http://${B_IP}:8080;" /etc/nginx/sites-enabled/default && echo true || echo false'
              """,
              returnStdout: true
            ).trim()

            if (isB == "true") {
              echo "이미 B 프록시입니다. 전환 생략."
            } else {
              try {
                sh """
                  echo "프록시를 B 서버로 전환 중..."
                  ssh -i "$PEM_FILE" -o StrictHostKeyChecking=no ubuntu@${A_IP} 'sudo bash /home/ubuntu/swap_proxy.sh'
                """
              } catch (err) {
                def msg = err.getMessage().replaceAll('"', '\\"').take(200)
                sh """
                  curl -H "Content-Type: application/json" -X POST \
                    -d '{"content": "🅱️❌ B 프록시 전환 실패\\n${msg}"}' $WEBHOOK_URL
                """
                error("프록시 전환 실패")
              }
            }
          }
        }
      }
    }

    stage('Git Pull') {
      steps {
        dir("${PROJECT_DIR}") {
          withCredentials([
            usernamePassword(credentialsId: 'github-token', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN'),
            string(credentialsId: 'discord-webhook', variable: 'WEBHOOK_URL')
          ]) {
            sh '''
              echo "📥 Git pull 중..."
              ERR_MSG=$(git pull https://$GIT_USER:$GIT_TOKEN@github.com/code-is-evenly-cooked/even-zaro-back.git main 2>&1) || {
                SHORT_MSG=$(echo "$ERR_MSG" | head -n 5 | tr '\n' ' ' | cut -c 1-200)
                curl -H "Content-Type: application/json" -X POST \
                  -d "{\"content\": \"📥❌ Git pull 실패\\n$SHORT_MSG\"}" $WEBHOOK_URL
                exit 1
              }
            '''
          }
        }
      }
    }

    stage('Build Image & A 서버 컨테이너 실행') {
      steps {
        dir("${PROJECT_DIR}") {
          withCredentials([string(credentialsId: 'discord-webhook', variable: 'WEBHOOK_URL')]) {
            script {
              try {
                sh 'echo "🔨 Docker 이미지 빌드 중..."'
                sh 'docker-compose -f docker-compose.prod.yml stop app'
                sh 'docker-compose -f docker-compose.prod.yml rm -f app'
                sh 'docker rmi even-final'
                sh 'docker-compose -f docker-compose.prod.yml up -d --build app'
                sh 'docker save even-final > app.tar'
              } catch (err) {
                def msg = err.getMessage().replaceAll('"', '\\"').take(200)
                sh """
                  curl -H "Content-Type: application/json" -X POST \
                    -d '{"content": "🐳❌ A 서버 빌드 실패\\n${msg}"}' $WEBHOOK_URL
                """
                error("A 서버 빌드 실패")
              }
            }
          }
        }
      }
    }

    stage('프록시 A로 복원') {
      steps {
        withCredentials([
          sshUserPrivateKey(credentialsId: 'server-key', keyFileVariable: 'PEM_FILE'),
          string(credentialsId: 'discord-webhook', variable: 'WEBHOOK_URL')
        ]) {
          script {
            def result = sh(
              script: """
                echo "📍 프록시를 A 서버로 복원 중..."
                ssh -i "$PEM_FILE" -o StrictHostKeyChecking=no ubuntu@${A_IP} 'sudo bash /home/ubuntu/swap_proxy.sh' 2> err.log
              """,
              returnStatus: true
            )
            if (result != 0) {
              def msg = sh(script: "head -n 5 err.log | tr '\\n' ' ' | cut -c 1-200", returnStdout: true).trim()
              sh """
                curl -H "Content-Type: application/json" -X POST \
                  -d "{\\"content\\": \\"🅰️❌ 프록시 A 복원 실패\\\\n${msg}\\"}" $WEBHOOK_URL
              """
              error "프록시 복원 실패"
            }
          }
        }
      }
    }

    stage('B 서버에 전송 및 실행') {
      steps {
        withCredentials([
          sshUserPrivateKey(credentialsId: 'server-key', keyFileVariable: 'PEM_FILE'),
          string(credentialsId: 'discord-webhook', variable: 'WEBHOOK_URL')
        ]) {
          script {
            try {
              echo "📦 B 서버에 파일 전송 중..."
              sh """
                scp -i "$PEM_FILE" -o StrictHostKeyChecking=no "$PROJECT_DIR"/app.tar ubuntu@${B_IP}:/home/ubuntu/
                scp -i "$PEM_FILE" -o StrictHostKeyChecking=no "$PROJECT_DIR"/.env ubuntu@${B_IP}:/home/ubuntu/
              """
            } catch (err) {
              def msg = err.getMessage().replaceAll('"', '\\"').take(200)
              sh """
                curl -H "Content-Type: application/json" -X POST \
                  -d '{"content": "📦❌ B 서버 파일 전송 실패\\n${msg}"}' $WEBHOOK_URL
              """
              error("B 서버 파일 전송 실패")
            }

            try {
              echo "🚀 B 서버에서 앱 실행 중..."
              sh """
                ssh -i "$PEM_FILE" -o StrictHostKeyChecking=no ubuntu@${B_IP} '
                  docker load -i app.tar && \
                  docker stop even_final_app || true && \
                  docker rm even_final_app || true && \
                  docker run -d --name even_final_app --env-file .env -p 8080:8080 even-final
                '
              """
            } catch (err) {
              def msg = err.getMessage().replaceAll('"', '\\"').take(200)
              sh """
                curl -H "Content-Type: application/json" -X POST \
                  -d '{"content": "🚀❌ B 서버 앱 실행 실패\\n${msg}"}' $WEBHOOK_URL
              """
              error("B 서버 앱 실행 실패")
            }
          }
        }
      }
    }
  }
}