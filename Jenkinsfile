#!groovy

pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build'

                //moving this here so that the graph reflects test failures
                junit allowEmptyResults: true,
                        testResults: '**/build/test-results/testReleaseUnitTest/TEST-*.xml'

                //this will get magically ignored if the build failed
                script {currentBuild.result = 'SUCCESS'}
            }
        }

        stage('Metrics') {
            steps {
                echo 'Gathering additional metrics...'
                androidLint canComputeNew: false, defaultEncoding: '', healthy: '',
                        pattern: '**/build/reports/lint-results*.xml', unHealthy: ''
                step([$class: 'JacocoPublisher', exclusionPattern: '**/R.class, **/R$*.class, ' +
                        '**/*Test*.class, **/BuildConfig.*, **/Manifest*.*, ' +
                        '**/*$ViewInjector*.*, **/*$ViewBinder*.*, **/*Assert*.class, ' +
                        '**/*_ViewBinding*.class, **/*_MembersInjector*.class'])
            }
        }
    }
}
