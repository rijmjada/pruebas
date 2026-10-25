pipeline {
    agent any

    options {
        // Guarda un historial razonable de builds para no saturar el servidor
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Evita ejecuciones concurrentes del mismo pipeline
        disableConcurrentBuilds()
        // Tiempo límite para abortar si un paso se queda trabado
        timeout(time: 20, unit: 'MINUTES')
        // Muestra timestamps en la consola de Jenkins
        timestamps()
    }

    parameters {
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: true,
            description: 'Omitir pruebas unitarias si no hay una base de datos MySQL activa en el entorno de Jenkins'
        )
        string(
            name: 'IMAGE_NAME',
            defaultValue: 'demo-app',
            description: 'Nombre de la imagen Docker'
        )
    }

    environment {
        DOCKER_IMAGE_NAME = "${params.IMAGE_NAME}"
        DOCKER_IMAGE_TAG  = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                echo '=== Paso 1: Clonando el repositorio ==='
                checkout scm
            }
        }

        stage('Test & Verify') {
            when {
                expression { return !params.SKIP_TESTS }
            }
            steps {
                echo '=== Paso 2: Ejecutando pruebas unitarias con Maven ==='
                script {
                    if (isUnix()) {
                        sh 'chmod +x mvnw'
                        sh './mvnw clean test'
                    } else {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
            post {
                always {
                    // Publica los reportes de prueba JUnit en Jenkins si existen
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "=== Paso 3: Construyendo imagen Docker [${env.DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG}] ==="
                script {
                    def buildCommand = "docker build -t ${env.DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG} -t ${env.DOCKER_IMAGE_NAME}:latest ."
                    if (isUnix()) {
                        sh buildCommand
                    } else {
                        bat buildCommand
                    }
                }
            }
        }

        stage('Verify Image') {
            steps {
                echo '=== Paso 4: Verificando que la imagen existe localmente ==='
                script {
                    def inspectCommand = "docker image inspect ${env.DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG}"
                    if (isUnix()) {
                        sh inspectCommand
                    } else {
                        bat inspectCommand
                    }
                }
            }
        }

        stage('Push to Docker Hub') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            steps {
                echo '=== Paso 5: Autenticando y subiendo imagen a Docker Hub ==='
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    script {
                        def fullImageName = "${DOCKER_USER}/${env.DOCKER_IMAGE_NAME}"
                        def tagBuildCmd   = "docker tag ${env.DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG} ${fullImageName}:${env.DOCKER_IMAGE_TAG}"
                        def tagLatestCmd  = "docker tag ${env.DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG} ${fullImageName}:latest"
                        def pushBuildCmd  = "docker push ${fullImageName}:${env.DOCKER_IMAGE_TAG}"
                        def pushLatestCmd = "docker push ${fullImageName}:latest"
                        def logoutCmd     = "docker logout"

                        if (isUnix()) {
                            // Comillas simples para que Groovy no exponga la variable en logs
                            sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
                            sh tagBuildCmd
                            sh tagLatestCmd
                            sh pushBuildCmd
                            sh pushLatestCmd
                            sh logoutCmd
                        } else {
                            bat 'echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin'
                            bat tagBuildCmd
                            bat tagLatestCmd
                            bat pushBuildCmd
                            bat pushLatestCmd
                            bat logoutCmd
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo '=== Limpiando el Workspace ==='
            cleanWs(deleteDirs: true, notFailBuild: true)
        }
        success {
            echo "🎉 Build #${env.BUILD_NUMBER} completado con éxito! Imagen lista: ${env.DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG}"
        }
        failure {
            echo "❌ El pipeline falló en el Build #${env.BUILD_NUMBER}. Revisa los logs de la consola."
        }
    }
}
