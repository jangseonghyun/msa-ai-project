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

    stage('Copy Frontend Dist To Host') {
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
            'ai-service',
            'config-server',
            'discovery-server'
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

    stage('Copy Backend Jars To Host') {
      steps {
        sh '''
          mkdir -p /home/tmd2052/02_msa-ai-project/backend/auth-service/build/libs
          mkdir -p /home/tmd2052/02_msa-ai-project/backend/doc-service/build/libs
          mkdir -p /home/tmd2052/02_msa-ai-project/backend/log-service/build/libs
          mkdir -p /home/tmd2052/02_msa-ai-project/backend/chat-service/build/libs
          mkdir -p /home/tmd2052/02_msa-ai-project/backend/gateway-service/build/libs
          mkdir -p /home/tmd2052/02_msa-ai-project/backend/ai-service/build/libs
          mkdir -p /home/tmd2052/02_msa-ai-project/backend/config-server/build/libs
          mkdir -p /home/tmd2052/02_msa-ai-project/backend/discovery-server/build/libs

          if [ -d "$WORKSPACE/backend/auth-service/build/libs" ]; then
            rm -f /home/tmd2052/02_msa-ai-project/backend/auth-service/build/libs/*.jar
            cp -r $WORKSPACE/backend/auth-service/build/libs/*.jar /home/tmd2052/02_msa-ai-project/backend/auth-service/build/libs/
          fi

          if [ -d "$WORKSPACE/backend/doc-service/build/libs" ]; then
            rm -f /home/tmd2052/02_msa-ai-project/backend/doc-service/build/libs/*.jar
            cp -r $WORKSPACE/backend/doc-service/build/libs/*.jar /home/tmd2052/02_msa-ai-project/backend/doc-service/build/libs/
          fi

          if [ -d "$WORKSPACE/backend/log-service/build/libs" ]; then
            rm -f /home/tmd2052/02_msa-ai-project/backend/log-service/build/libs/*.jar
            cp -r $WORKSPACE/backend/log-service/build/libs/*.jar /home/tmd2052/02_msa-ai-project/backend/log-service/build/libs/
          fi

          if [ -d "$WORKSPACE/backend/chat-service/build/libs" ]; then
            rm -f /home/tmd2052/02_msa-ai-project/backend/chat-service/build/libs/*.jar
            cp -r $WORKSPACE/backend/chat-service/build/libs/*.jar /home/tmd2052/02_msa-ai-project/backend/chat-service/build/libs/
          fi

          if [ -d "$WORKSPACE/backend/gateway-service/build/libs" ]; then
            rm -f /home/tmd2052/02_msa-ai-project/backend/gateway-service/build/libs/*.jar
            cp -r $WORKSPACE/backend/gateway-service/build/libs/*.jar /home/tmd2052/02_msa-ai-project/backend/gateway-service/build/libs/
          fi

          if [ -d "$WORKSPACE/backend/ai-service/build/libs" ]; then
            rm -f /home/tmd2052/02_msa-ai-project/backend/ai-service/build/libs/*.jar
            cp -r $WORKSPACE/backend/ai-service/build/libs/*.jar /home/tmd2052/02_msa-ai-project/backend/ai-service/build/libs/
          fi

          if [ -d "$WORKSPACE/backend/config-server/build/libs" ]; then
            rm -f /home/tmd2052/02_msa-ai-project/backend/config-server/build/libs/*.jar
            cp -r $WORKSPACE/backend/config-server/build/libs/*.jar /home/tmd2052/02_msa-ai-project/backend/config-server/build/libs/
          fi

          if [ -d "$WORKSPACE/backend/discovery-server/build/libs" ]; then
            rm -f /home/tmd2052/02_msa-ai-project/backend/discovery-server/build/libs/*.jar
            cp -r $WORKSPACE/backend/discovery-server/build/libs/*.jar /home/tmd2052/02_msa-ai-project/backend/discovery-server/build/libs/
          fi
        '''
      }
    }

    stage('Deploy') {
      steps {
        sh '''
          git config --global --add safe.directory /home/tmd2052/02_msa-ai-project
          cd /home/tmd2052/02_msa-ai-project
          git pull
          docker-compose up -d --build
        '''
      }
    }
  }
}