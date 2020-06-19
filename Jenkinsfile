pipeline {
    agent any

    stages {

        stage('Compile') {
            steps {
                echo "Compiling..."
                bat "/usr/local/bin/sbt compile"
            }
        }

        stage('Test') {
            steps {
                echo "Testing..."
                bat "/usr/local/bin/sbt test"
            }
        }

        stage('Package') {
            steps {
                echo "Packaging..."
                bat "/usr/local/bin/sbt package"
            }
        }

    }
}
