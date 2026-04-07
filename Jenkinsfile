pipeline {
  agent any

  stages {

    stage('Frontend Build') {
      agent {
        docker {
          image 'node:24'
        }
      }
      steps {
        dir('frontend/react') {
          sh 'npm install'
          sh 'npm run build'
        }
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
                  sh './gradlew build'
                }
              }

            } else {
              echo ">>> Skipping ${svc} (no gradlew)"
            }
          }
        }
      }
    }

  }
}