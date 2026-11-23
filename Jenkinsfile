
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Java Version') {
            steps {
                sh '''
                    export JAVA_HOME=/opt/java17
                    export PATH=$JAVA_HOME/bin:$PATH
                    java -version
                    mvn -version
                '''
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
    }
}
