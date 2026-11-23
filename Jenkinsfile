
pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-south-1'
        AWS_ACCOUNT_ID = '102186470246'
        ECR_REPO = 'jenkins-maven-demo'
        IMAGE_TAG = "${BUILD_NUMBER}"
        ECR_REGISTRY = '102186470246.dkr.ecr.ap-south-1.amazonaws.com'
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
                    mvn clean verify
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    docker build -t ${ECR_REPO}:${IMAGE_TAG} .
                    docker tag ${ECR_REPO}:${IMAGE_TAG} \
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
            echo 'Application image successfully pushed to ECR!'
        }
        failure {
            echo 'Pipeline failed. Check Jenkins console output.'
        }
    }
}
