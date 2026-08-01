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

                echo "===================================="
                echo "Application : ${APP_NAME}"
                echo "Version     : ${IMAGE_TAG}"
                echo "Docker Image: ${IMAGE_NAME}:${IMAGE_TAG}"
                echo "===================================="
            }
        }

        stage('Verify Build Environment') {
            steps {
                sh '''
                    echo "===== Build Environment ====="
                    hostname
                    whoami
                    pwd
                    java -version
                    mvn -version
                    git --version
                    docker --version
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

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                withSonarQubeEnv('SonarCloud') {
                    sh '''
                    mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.2.0.4988:sonar \
                    -Dsonar.projectKey=RajaGokul_employee-management-system \
                    -Dsonar.organization=rajagokul
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    script {
                        def qualityGate = waitForQualityGate()

                        if (qualityGate.status == 'OK') {
                            echo "✅ Quality Gate PASSED"
                        } else {
                            echo "⚠️ Quality Gate Status : ${qualityGate.status}"
                            echo "Proceeding with pipeline for DevOps learning project."
                        }
                    }
                }
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

	stage('Deploy to Application Server') {
    steps {
        withCredentials([
            string(credentialsId: 'app-server-ip', variable: 'APP_SERVER')
        ]) {
            sshagent(credentials: ['build_agent_ssh']) {
                sh '''
                ssh -o StrictHostKeyChecking=no jenkins@$APP_SERVER <<EOF

                echo "===== Deploying Application ====="

                docker pull ${IMAGE_NAME}:${IMAGE_TAG}

                docker stop employee-management-system || true
                docker rm employee-management-system || true

                docker run -d \
                    --name employee-management-system \
                    --restart unless-stopped \
		    --add-host=host.docker.internal:host-gateway \
                    -p 8080:8080 \
                    ${IMAGE_NAME}:${IMAGE_TAG}

                docker ps

                EOF
                '''
            }
        }
    }
}
    }

    post {

        success {
            echo ""
            echo "===================================="
            echo "BUILD SUCCESSFUL"
            echo "Application : ${APP_NAME}"
            echo "Version     : ${IMAGE_TAG}"
            echo "Image       : ${IMAGE_NAME}:${IMAGE_TAG}"
            echo "===================================="
        }

        failure {
            echo ""
            echo "===================================="
            echo "BUILD FAILED"
            echo "Application : ${APP_NAME}"
            echo "Version     : ${IMAGE_TAG}"
            echo "===================================="
        }

        always {
            cleanWs()
        }
    }
}

