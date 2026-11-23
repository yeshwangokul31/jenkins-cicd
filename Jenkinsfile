
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
                echo '===== CHECKOUT SOURCE CODE ====='
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh '''
                    set -e

                    export JAVA_HOME=/opt/java17
                    export PATH=$JAVA_HOME/bin:$PATH

                    echo "===== JAVA VERSION ====="
                    java -version

                    echo "===== MAVEN VERSION ====="
                    mvn -version

                    echo "===== BUILD AND TEST ====="
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

                        export JAVA_HOME=/opt/java/openjdk
                        export PATH=$JAVA_HOME/bin:$PATH

                        echo "===== SONAR JAVA VERSION ====="
                        java -version

                        echo "===== SONARCLOUD CONNECTIVITY ====="

                        curl -sS -o /dev/null \
                            -w "HTTP Status: %{http_code}\\n" \
                            https://sonarcloud.io/api/server/version

                        echo "===== SONARQUBE ANALYSIS STARTED ====="

                        mvn -B \
                            org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751:sonar \
                            -Dsonar.organization=${SONAR_ORG} \
                            -Dsonar.projectKey=${SONAR_PROJECT} \
                            -Dsonar.host.url=${SONAR_URL} \
                            -Dsonar.token="$SONAR_TOKEN" \
                            -Dsonar.scanner.skipJreProvisioning=true \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.verbose=true \
                            -Dsonar.qualitygate.wait=true

                        echo "===== SONARQUBE ANALYSIS COMPLETED ====="
                    '''
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    set -e

                    echo "===== DOCKER IMAGE BUILD ====="

                    docker build \
                        -t ${ECR_REPO}:${IMAGE_TAG} .

                    docker tag \
                        ${ECR_REPO}:${IMAGE_TAG} \
                        ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}

                    echo "===== DOCKER IMAGE CREATED ====="

                    docker images ${ECR_REPO}
                '''
            }
        }

        stage('ECR Login') {
            steps {
                sh '''
                    set -e

                    export AWS_PAGER=""

                    echo "===== AWS ECR LOGIN ====="

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
                    set -e

                    echo "===== PUSHING IMAGE TO ECR ====="

                    docker push \
                        ${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}

                    echo "===== ECR IMAGE PUSH SUCCESSFUL ====="

                    echo "IMAGE URI:"
                    echo "${ECR_REGISTRY}/${ECR_REPO}:${IMAGE_TAG}"
                '''
            }
        }

        stage('Deploy to EKS') {
            steps {
                sh '''
                    set -e

                    echo "===== CONFIGURING EKS ====="

                    aws eks update-kubeconfig \
                        --region ${AWS_REGION} \
                        --name jenkins-eks-demo

                    echo "===== DEPLOYING APPLICATION ====="

                    sed "s|jenkins-maven-demo:1|jenkins-maven-demo:${IMAGE_TAG}|g" \
                        k8s/deployment.yaml | kubectl apply -f -

                    kubectl apply -f k8s/service.yaml

                    echo "===== WAITING FOR ROLLOUT ====="

                    kubectl rollout status deployment/jenkins-maven-demo \
                        --timeout=180s

                    echo "===== DEPLOYMENT COMPLETED ====="

                    kubectl get deployments
                    kubectl get pods -o wide
                    kubectl get services
                '''
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Build, Test, SonarQube Analysis and ECR Push completed!'
        }

        failure {
            echo 'FAILURE: Check Jenkins Console Output for details.'
        }

        always {
            echo "Pipeline execution completed. Build number: ${BUILD_NUMBER}"
        }
    }
}
