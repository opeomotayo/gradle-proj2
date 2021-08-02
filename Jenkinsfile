pipeline {
	agent any
	stages {
	
        stage('Build') {
        	steps {
                sh './gradlew -b build.gradle clean build'
            }
        }
         
    stage('Parallel Stages') {
            parallel {
                stage('Archival') {
        	        steps {
                        archiveArtifacts 'build/libs/*.?ar'
			        }
                }
                      stage('SonarQube analysis') {
                    steps {
                        withSonarQubeEnv('sonar') {
                        sh './gradlew  sonarqube'
              }
            }
        }
                          stage('Test Reports') {
        	        steps {
                        junit 'build/test-results/test/*.xml'
			        }
                }
                }
                }
           
                
        
       
        stage('Artifact Uploader') {
            steps {
                nexusArtifactUploader artifacts: [[artifactId: 'myTeam', file: 'build/libs/myTeam.war', type: 'war']], credentialsId: 'nexusAdminCreds', groupId: 'com.nisum.mytime', nexusUrl: '10.3.45.130:8081/nexus', nexusVersion: 'nexus2', protocol: 'http', repository: 'releases', version: '1.0.${BUILD_NUMBER}'
            }
        }
        stage('Deploy to PreProd') {
            steps {
                sh '''
                    scp build/libs/myTeam.war tomcat@10.3.45.131:/opt/tomcat/webapps/myTeam.war;
                    ssh tomcat@10.3.45.131 /opt/tomcat/bin/shutdown.sh;
                    ssh tomcat@10.3.45.131 /opt/tomcat/bin/startup.sh
                '''
                //sshPublisher(publishers: [sshPublisherDesc(configName: 'preprod', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: '/opt/tomcat/bin/shutdown.sh;sleep 5;/opt/tomcat/bin/startup.sh', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '/opt/tomcat/webapps/', remoteDirectorySDF: false, removePrefix: '', sourceFiles: 'build/libs/*.war')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
            }
        }
       
    }
         
    post {
   	    always {
   		    notify('started')
   	    }
   	    failure {
   		    notify('err')
   	    }
   	    success {
   		    notify('success')
   	    }
    }
}
 
def notify(status){
	emailext (
	to: "sneelam@nisum.com,rtirumalapuram@nisum.com",
	subject: "${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
	body: """<p>${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    	<p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME}  [${env.BUILD_NUMBER}]</a></p>""",
	)
}
