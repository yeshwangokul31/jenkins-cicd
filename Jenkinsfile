
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

                STATUS=$(curl -sS \
                  -o /tmp/sonar-response.txt \
                  -w "%{http_code}" \
                  -H "Authorization: Bearer $SONAR_TOKEN" \
                  https://api.sonarcloud.io/analysis/engine)

                echo "Sonar API HTTP Status: $STATUS"
                cat /tmp/sonar-response.txt
            '''
        }
    }
}
