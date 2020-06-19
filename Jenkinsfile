pipeline {
    agent any

    stages {

        stage('Compile') {
            steps {
                echo "Compiling..."
                bat "compile"
            }
        }

        stage('Test') {
            steps {
                echo "Testing..."
                bat "test"
            }
        }

        stage('Package') {
            steps {
                echo "Packaging..."
                bat "package"
            }
        }

    }
}
