pipeline {
    agent any

    stages {

        stage('Compile') {
            steps {
                echo "Compiling..."
                bat "sbt compile"
            }
        }

        stage('Test') {
            steps {
                echo "Testing..."
                bat "sbt test"
            }
        }

        stage('Package') {
            steps {
                echo "Packaging..."
                bat "sbt package"
            }
        }

    }
}
