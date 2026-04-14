pipeline {
  agent any

  stages {

    stage('Frontend Build') {
      steps {
        dir('frontend') {
          script {
            docker.image('node:24').inside {
              sh 'npm install'
              sh 'npm run build'
            }
          }
        }
      }
    }

    stage('Copy Frontend to Host') {
        steps {
            sh '''
            rm -rf /home/tmd2052/02_msa-ai-project/frontend/dist
            mkdir -p /home/tmd2052/02_msa-ai-project/frontend
            cp -r $WORKSPACE/frontend/dist /home/tmd2052/02_msa-ai-project/frontend
            '''
        }
    }

    stage('Backend Build') {
      steps {
        script {
          def services = [
            'auth-service',
            'doc-service',
            'log-service',
            'chat-service',
            'gateway-service',
            'ai-service'
          ]

          for (svc in services) {
            def path = "backend/${svc}"

            if (fileExists("${path}/gradlew")) {
              echo ">>> Building ${svc}"

              dir(path) {
                docker.image('gradle:8.5-jdk17').inside {
                  sh 'chmod +x gradlew'
                  sh './gradlew build -x test'
                }
              }

            } else {
              echo ">>> Skipping ${svc}"
            }
          }
        }
      }
    }

    stage('Deploy') {
      steps {
        sh '''
          cd /home/tmd2052/02_msa-ai-project &&
          git pull &&
          docker-compose up -d --build
        '''
      }
    }

  }
}