pipeline {
    agent any

    environment {
        REGISTRY   = "localhost:5000"
        NAMESPACE  = "fecha-hora-k8s"
        TAG        = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn -B clean package -DskipTests'
                    sh "docker build -t ${REGISTRY}/fecha-hora-k8s-backend:${TAG} ."
                    sh "docker push ${REGISTRY}/fecha-hora-k8s-backend:${TAG}"
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh "docker build -t ${REGISTRY}/fecha-hora-k8s-frontend:${TAG} ."
                    sh "docker push ${REGISTRY}/fecha-hora-k8s-frontend:${TAG}"
                }
            }
        }

        stage('Deploy to k3s') {
            steps {
                sh """
                    kubectl apply -f k8s/
                    kubectl -n ${NAMESPACE} set image deployment/fecha-hora-k8s-backend fecha-hora-k8s-backend=${REGISTRY}/fecha-hora-k8s-backend:${TAG}
                    kubectl -n ${NAMESPACE} set image deployment/fecha-hora-k8s-frontend fecha-hora-k8s-frontend=${REGISTRY}/fecha-hora-k8s-frontend:${TAG}
                    kubectl -n ${NAMESPACE} rollout status deployment/fecha-hora-k8s-backend --timeout=120s
                    kubectl -n ${NAMESPACE} rollout status deployment/fecha-hora-k8s-frontend --timeout=120s
                """
            }
        }

        stage('Smoke Test') {
            steps {
                sh '''
                  sleep 5
                  curl -sf http://localhost:30080/api/datetime || exit 1
                '''
            }
        }
    }

    post {
        failure {
            echo "Pipeline falló — revisa logs del stage correspondiente"
        }
    }
}