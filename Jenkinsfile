```groovy
pipeline {
    agent any

    environment {
        AWS_REGION      = 'ap-south-1'
        AWS_ACCOUNT_ID  = '102186470246'
        ECR_REPO        = 'jenkins-maven-demo'
        ECR_REGISTRY    = '102186470246.dkr.ecr.ap-south-1.amazonaws.com'
        IMAGE_TAG       = "${BUILD_NUMBER}"

        SONAR_ORG       = 'yeshwangokul31'
        SONAR_PROJECT   = 'yeshwangokul31_jenkins-cicd'
        SONAR_URL       = 'https://sonarcloud.io'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh '''
                    export JAVA_HOME=/opt/java17
                    export PATH=$JAVA_HOME/bin:$PATH

                    echo "Checking Java version"
                    java -version

                    echo "Checking Maven version"
                    mvn -version

                    echo "Building application and running tests"
                    mvn clean verify
                '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([
                    string(
                        credentialsId: 'sonarcloud-token',
                        variable: 'SONAR_TOKEN'
                    )
                ]) {
                    sh '''
                        set -e

                        export JAVA_HOME=/opt/java17
                        export PATH=$JAVA_HOME/bin:$PATH

                        echo "Checking SonarQube Cloud connectivity"
                        curl -sS -o /dev/null \
                          -w "HTTP Status: %{http_code}\\n" \
                          https://sonarcloud.io/api/server/version

                        echo "Starting SonarQube analysis"

                        mvn -B \
                          org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751:sonar \
                          -Dsonar.organization=${SONAR_ORG} \
                          -Dsonar.projectKey=${SONAR_PROJECT} \
                          -Dsonar.host.url=${SONAR_URL} \
                          -Dsonar.token="$SONAR_TOKEN" \
                          -Dsonar.java.binaries=target/classes \
                          -Dsonar.verbose=true \
                          -Dsonar.qualitygate.wait=true
                    '''
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    echo "Building Docker image"

                    docker build \
                      -t ${ECR_REPO}:${IMAGE_TAG} .

                    docker tag \
                      ${ECR_REPO}:${IMAGE_TAG} \
                      ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}
                '''
            }
        }

        stage('ECR Login') {
            steps {
                sh '''
                    export AWS_PAGER=""

                    echo "Logging into AWS ECR"

                    aws ecr get-login-password \
                      --region ${AWS_REGION} | \
                    docker login \
                      --username AWS \
                      --password-stdin ${ECR_REGISTRY}
                '''
            }
        }

        stage('Push to ECR') {
            steps {
                sh '''
                    echo "Pushing image to ECR"

                    docker push \
                      ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}
                '''
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Build, tests, SonarQube analysis and ECR push completed!'
        }

        failure {
            echo 'FAILURE: Check the Jenkins Console Output for the exact error.'
        }

        always {
            echo "Pipeline execution completed. Build number: ${BUILD_NUMBER}"
        }
    }
}
```