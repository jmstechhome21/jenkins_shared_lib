def call(Map pipelineParams){
def projectName = pipelineParams.ecrRepoName

pipeline {
 agent any
  environment {
    registry = "ybmsr/${projectName}"
    registryCredential = 'dockerhub_credentials'
    dockerImage = ''
  }
  stages {
     stage('get scm') {
      steps {
	       git credentialsId: 'github_credentials', url: 'https://github.com/jmstechhome21/${projectName}'
       }
    }
	   stage('mavenbuild'){
	   steps{
	    sh 'mvn package'
	   }
	   }
    stage('Building image') {
      steps{
        script {
          dockerImage = docker.build registry + ":$BUILD_NUMBER"
        }
      }
    }
    stage('push image') {
      steps{
        script {
          docker.withRegistry( '', registryCredential ) {
            dockerImage.push()
          }
        }
      }
    }
    stage('Remove old docker image') {
      steps{
        sh "docker rmi $registry:$BUILD_NUMBER"
      }
    }
  }
}
}
