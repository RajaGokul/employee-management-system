pipeline {

    agent {
        label 'build-agent'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {

        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        PATH = "/usr/lib/jvm/java-21-openjdk-amd64/bin:/usr/share/maven/bin:${env.PATH}"

        APP_NAME = 'employee-management-system'
        IMAGE_NAME = 'raja62533/employee-management-system'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Initialize') {
            steps {
                script {
                    currentBuild.displayName = "#${BUILD_NUMBER}"
                    currentBuild.description = "Version ${BUILD_NUMBER}"
                }

                echo "Application : ${APP_NAME}"
                echo "Image       : ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }

        stage('Verify Build Environment') {
            steps {
                sh '''
                    echo "========== BUILD ENVIRONMENT =========="

                    hostname
                    whoami
                    pwd

                    echo
                    java -version
                    echo
                    mvn -version
                    echo
                    git --version
                    echo
                    docker --version
                    echo
                    trivy --version
                '''
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                withSonarQubeEnv('SonarCloud') {

                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=RajaGokul_employee-management-system \
                        -Dsonar.organization=rajagokul
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build') {
            steps {

                sh '''
                    docker build \
                    -t ${IMAGE_NAME}:${IMAGE_TAG} \
                    -t ${IMAGE_NAME}:latest .
                '''
            }
        }

        stage('Trivy Scan') {
            steps {

                sh '''
                    trivy image \
                    --exit-code 0 \
                    --severity HIGH,CRITICAL \
                    --no-progress \
                    ${IMAGE_NAME}:${IMAGE_TAG}
                '''
            }
        }

        stage('Docker Login') {
            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {

                    sh '''
                        echo "$DOCKER_PASS" | docker login \
                        -u "$DOCKER_USER" \
                        --password-stdin
                    '''
                }
            }
        }

        stage('Docker Push') {
            steps {

                sh '''
                    docker push ${IMAGE_NAME}:${IMAGE_TAG}
                    docker push ${IMAGE_NAME}:latest
                '''
            }
        }

    }

    post {

        success {
            echo "======================================"
            echo "BUILD SUCCESSFUL"
            echo "Image: ${IMAGE_NAME}:${IMAGE_TAG}"
            echo "======================================"
        }

        failure {
            echo "======================================"
            echo "BUILD FAILED"
            echo "======================================"
        }

        always {
            cleanWs()
        }
    }
}
