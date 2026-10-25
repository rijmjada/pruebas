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
        // Descomenta si vas a usar un registro como Docker Hub, AWS ECR, etc.:
        // REGISTRY          = 'docker.io/tu-usuario'
        // REGISTRY_CREDS    = 'dockerhub-credentials'
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

        stage('Push to Registry') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            steps {
                echo '=== Paso 5: Subida al Registro Docker (Opcional) ==='
                echo 'Para activar la subida automática a Docker Hub/ECR, configura tus credenciales en Jenkins y descomenta el bloque.'
                /*
                withCredentials([usernamePassword(credentialsId: env.REGISTRY_CREDS, passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                    script {
                        def pushCommands = """
                            echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
                            docker tag ${env.DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG} ${env.REGISTRY}/${env.DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG}
                            docker push ${env.REGISTRY}/${env.DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG}
                        """
                        if (isUnix()) {
                            sh pushCommands
                        } else {
                            bat pushCommands
                        }
                    }
                }
                */
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
