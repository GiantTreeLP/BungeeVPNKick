pipeline {
  agent any
  tools {
        maven 'Maven3'
        jdk 'Java10'
  }
  options {
    buildDiscarder logRotator(numToKeepStr: '10')
  }
  stages {
    stage('Clean') {
      steps {
        sh 'mvn clean'
      }
    }
    stage('Compile') {
      steps {
        sh 'mvn compile'
      }
    }
    stage('Test') {
      steps {
        sh 'mvn test'
      }
    }
    stage('Package') {
      steps {
        sh 'mvn package -Dmaven.test.skip=true'
      }
    }
  }
  post {
    always {
      archiveArtifacts artifacts: 'target/*.jar', excludes: 'target/original*', fingerprint: true
      junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
      discordSend description: 'New plugin build\n@here', footer: 'Update', link: BUILD_URL, successful: currentBuild.resultIsBetterOrEqualTo('SUCCESS'), title: JOB_NAME, webhookURL: 'https://discordapp.com/api/webhooks/427588035274735628/o0a4dxYZLCfNqBUgGVSlU-qFVBwY-pLO6u9Wbj_fbXpwdPsdk2vC8awkwJ4clM2uczTf'
    }
  }
}
