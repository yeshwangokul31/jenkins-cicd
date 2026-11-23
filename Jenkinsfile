
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

                    java -version
                    mvn -version

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
                        export JAVA_HOME=/opt/java17
                        export PATH=$JAVA_HOME/bin:$PATH

                        mvn -B \
                          org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751:sonar \
                          -Dsonar.organization=${SONAR_ORG} \
                          -Dsonar.projectKey=${SONAR_PROJECT} \
                          -Dsonar.host.url=${SONAR_URL} \
                          -Dsonar.token="$SONAR_TOKEN" \
                          -Dsonar.qualitygate.wait=true
                    '''
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
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
                    docker push \
                      ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}
                '''
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Build, tests, SonarQube and ECR push completed!'
        }

        failure {
            echo 'FAILURE: Check Jenkins Console Output.'
        }

        always {
            echo "Pipeline execution completed. Build number: ${BUILD_NUMBER}"
        }
    }
}
