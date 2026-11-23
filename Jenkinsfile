
pipeline {
    agent any

    environment {
        SONAR_URL = 'https://sonarcloud.io'
    }

    stages {

        stage('Sonar Server Connectivity') {
            steps {
                sh '''
                    echo "===== SONAR SERVER CONNECTIVITY ====="

                    curl -sS -o /dev/null \
                        -w "HTTP Status: %{http_code}\\n" \
                        ${SONAR_URL}/api/server/version
                '''
            }
        }

        stage('Sonar Token Verification') {
            steps {
                withCredentials([
                    string(
                        credentialsId: 'sonarcloud-token',
                        variable: 'SONAR_TOKEN'
                    )
                ]) {
                    sh '''
                        set +x

                        echo "===== SONAR TOKEN VERIFICATION ====="

                        STATUS=$(curl -sS \
                            -o /tmp/sonar-response.txt \
                            -w "%{http_code}" \
                            -H "Authorization: Bearer $SONAR_TOKEN" \
                            https://api.sonarcloud.io/analysis/engine)

                        echo "Sonar API HTTP Status: $STATUS"

                        echo "===== API RESPONSE ====="
                        cat /tmp/sonar-response.txt
                        echo

                        rm -f /tmp/sonar-response.txt
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'SonarQube connectivity and token verification stages completed.'
        }

        failure {
            echo 'SonarQube verification failed. Check the console output.'
        }
    }
}
