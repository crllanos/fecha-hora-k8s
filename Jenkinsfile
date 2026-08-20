pipeline {
    agent any

    tools {
        maven 'Maven 3.9'
    }

    environment {
        REGISTRY  = "localhost:5000"
        NAMESPACE = "fecha-hora-k8s"
        TAG       = "${env.BUILD_NUMBER}"
        SONAR_URL = "http://192.168.122.168:9000"
    }

    stages {

        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn -B clean package -DskipTests'
                }
            }
        }

        stage('Tests + Cobertura (JaCoCo)') {
            steps {
                dir('backend') {
                    sh 'mvn -B verify'
                }
            }
            post {
                always {
                    junit(
                        testResults: 'backend/target/surefire-reports/*.xml',
                        allowEmptyResults: true
                    )
                }
            }
        }

        stage('Análisis SonarQube') {
            steps {
                withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                    dir('backend') {
                        sh """
                          mvn -B sonar:sonar \
                            -Dsonar.projectKey=fecha-hora-k8s-backend \
                            -Dsonar.host.url=${SONAR_URL} \
                            -Dsonar.token=${SONAR_TOKEN} \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        """
                    }
                }
            }
        }

// @TODO: OWASP Dependency Check — falla con error 403 en NVD API
// Posibles causas: key no activada, rate limiting en CI, o delay insuficiente
// Descomentar y revisar cuando se resuelva el acceso a NVD
//
// stage('OWASP Dependency Check') {
//     steps {
//         withCredentials([string(credentialsId: 'nvd-api-key', variable: 'NVD_KEY')]) {
//             dir('backend') {
//                 sh """
//                   mvn -B dependency-check:check \
//                     -Dnvd.api.key=\${NVD_KEY}
//                 """
//             }
//         }
//     }
//     post {
//         always {
//             publishHTML(target: [
//                 allowMissing         : true,
//                 alwaysLinkToLastBuild: true,
//                 keepAll              : true,
//                 reportDir            : 'backend/target',
//                 reportFiles          : 'dependency-check-report.html',
//                 reportName           : 'OWASP Dependency Check'
//             ])
//         }
//     }
// }

        stage('Docker Build & Push Imágenes') {
            steps {
                dir('backend') {
                    sh "docker build -t ${REGISTRY}/fecha-hora-k8s-backend:${TAG} ."
                    sh "docker push ${REGISTRY}/fecha-hora-k8s-backend:${TAG}"
                }
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
                  kubectl -n ${NAMESPACE} set image deployment/fecha-hora-k8s-backend \
                    fecha-hora-k8s-backend=${REGISTRY}/fecha-hora-k8s-backend:${TAG}
                  kubectl -n ${NAMESPACE} set image deployment/fecha-hora-k8s-frontend \
                    fecha-hora-k8s-frontend=${REGISTRY}/fecha-hora-k8s-frontend:${TAG}
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
            echo "Pipeline falló — revisa el stage correspondiente"
        }
        success {
            echo "Pipeline completo. Resultados en SonarQube: http://localhost:9000"
        }
    }
}