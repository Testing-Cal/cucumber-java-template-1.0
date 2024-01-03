import groovy.json.JsonSlurper
import java.security.*

def parseJson(jsonString) {
    def lazyMap = new JsonSlurper().parseText(jsonString)
    def m = [:]
    m.putAll(lazyMap)
    return m
}
def parseJsonArray(jsonString){
    def datas = readJSON text: jsonString
    return datas
}

def parseJsonString(jsonString, key){
    def datas = readJSON text: jsonString
    String Values = writeJSON returnText: true, json: datas[key]
    return Values
}

def parseYaml(jsonString) {
    def datas = readYaml text: jsonString
    String yml = writeYaml returnText: true, data: datas['kubernetes']
    return yml

}

def createYamlFile(data,filename) {
    writeFile file: filename, text: data
}

def returnSecret(path,secretValues){
    def secretValueFinal= []
    for(secret in secretValues) {
        def secretValue = [:]
        //secretValue['envVar'] = secret.envVariable
        //secretValue['vaultKey'] = secret.vaultKey
        secretValue.put('envVar',secret.envVariable)
        secretValue.put('vaultKey',secret.vaultKey)
        secretValueFinal.add(secretValue)
    }
    def secrets = [:]
    secrets["path"] = path
    secrets['engineVersion']=  2
    secrets['secretValues'] = secretValueFinal

    return secrets
}

// String str = ''
// loop to create a string as -e $STR -e $PTDF

def dockerVaultArguments(secretValues){
    def data = []
    for(secret in secretValues) {
        data.add('$'+secret.envVariable+' > .'+secret.envVariable)
    }
    return data
}

def dockerVaultArgumentsFile(secretValues){
    def data = []
    for(secret in secretValues) {
        data.add(secret.envVariable)
    }
    return data
}

def pushToCollector(){
  print("Inside pushToCollector...........")
    def job_name = "$env.JOB_NAME"
    def job_base_name = "$env.JOB_BASE_NAME"
    String generalProperties = parseJsonString(env.JENKINS_METADATA,'general')
    generalPresent = parseJsonArray(generalProperties)
    if(generalPresent.tenant != '' &&
    generalPresent.lazsaDomainUri != ''){
      echo "Job folder - $job_name"
      echo "Pipeline Name - $job_base_name"
      echo "Build Number - $currentBuild.number"
      sh """curl -k -X POST '${generalPresent.lazsaDomainUri}/collector/orchestrator/devops/details' -H 'X-TenantID: ${generalPresent.tenant}' -H 'Content-Type: application/json' -d '{\"jobName\" : \"${job_base_name}\", \"projectPath\" : \"${job_name}\", \"agentId\" : \"${generalPresent.agentId}\", \"devopsConfigId\" : \"${generalPresent.devopsSettingId}\", \"agentApiKey\" : \"${generalPresent.agentApiKey}\", \"buildNumber\" : \"${currentBuild.number}\" }' """
    }
}


def agentLabel = "${env.JENKINS_AGENT == null ? "":env.JENKINS_AGENT}"
pipeline {
  agent { label agentLabel }
  environment {
    DEFAULT_STAGE_SEQ = "'CodeCheckout','Deploy','UnitTests','GenerateHTMLreport','Destroy'"
    CUSTOM_STAGE_SEQ = "${DYNAMIC_JENKINS_STAGE_SEQUENCE}"
    PROJECT_TEMPLATE_ACTIVE = "${DYNAMIC_JENKINS_STAGE_NEEDED}"
    LIST = "${env.PROJECT_TEMPLATE_ACTIVE == 'true' ? env.CUSTOM_STAGE_SEQ : env.DEFAULT_STAGE_SEQ}"
    BRANCHES = "${env.GIT_BRANCH}"
    COMMIT = "${env.GIT_COMMIT}"
    RELEASE_NAME = "cucumber"
    SERVICE_PORT = "${APP_PORT}"
    DOCKERHOST = "${DOCKERHOST_IP}"
    REGISTRY_URL = "${DOCKER_REPO_URL}"
    ACTION = "${ACTION}"
    DEPLOYMENT_TYPE = "${DEPLOYMENT_TYPE == ""? "EC2":DEPLOYMENT_TYPE}"
    KUBE_SECRET = "${KUBE_SECRET}"
    CHROME_BIN = "/usr/bin/google-chrome"
    ARTIFACTORY = "${ARTIFACTORY == ""? "ECR":ARTIFACTORY}"
    ARTIFACTORY_CREDENTIALS = "${ARTIFACTORY_CREDENTIAL_ID}"
    NGINX_IP = "${NGINX_IP}"
    JENKINS_METADATA = "${JENKINS_METADATA}"
  }
  stages {
     stage('Running Stages') {
           agent { label agentLabel }
           steps {
             script {
               def listValue = "$env.LIST"
               def list = listValue.split(',')
               print(list)
               echo "projectTemplateActive - $env.PROJECT_TEMPLATE_ACTIVE"
               if (env.CUSTOM_STAGE_SEQ != null) {
                 echo "customStagesSequence - $env.CUSTOM_STAGE_SEQ"
               }
               echo "defaultStagesSequence - $env.DEFAULT_STAGE_SEQ"
               for (int i = 0; i < list.size(); i++) {
                 print(list[i])
                 if (list[i] == "'CodeCheckout'") {
                   print(list[i])
                   stage('Initialisation') {
                     // stage details here
                     def job_name = "$env.JOB_NAME"
                     print(job_name)
                     def values = job_name.split('/')
                     namespace_prefix = values[0].replaceAll("[^a-zA-Z0-9\\-\\_]+","").toLowerCase().take(50)
                     namespace = "$namespace_prefix-$env.foldername".toLowerCase()
                     service = values[2].replaceAll("[^a-zA-Z0-9\\-\\_]+","").toLowerCase().take(50)
                     print("kube namespace: $namespace")
                     print("service name: $service")
                     env.namespace_name=namespace
                     env.service=service
                     if (env.ARTIFACTORY == "ACR"){
                      def url_string = "$REGISTRY_URL"
                      url = url_string.split('/')
                      env.ACR_LOGIN_URL = url[0]
                      echo "Reg Login url: $ACR_LOGIN_URL"
                   }
                    if (env.DEPLOYMENT_TYPE == 'KUBERNETES' || env.DEPLOYMENT_TYPE == 'OPENSHIFT') {
                        String kubeProperties = parseJsonString(env.JENKINS_METADATA,'kubernetes')
                        kubeVars = parseJsonArray(kubeProperties)
                    }
                  if (env.DEPLOYMENT_TYPE == 'KUBERNETES' || env.DEPLOYMENT_TYPE == 'OPENSHIFT'){
                   if (kubeVars.namespace != null && kubeVars.namespace != '') {
                       namespace = kubeVars.namespace
                   }else{
                       echo "namespace not received"
                  }
               }
               print("kube namespace: $namespace")
               env.namespace_name = namespace


                    
                   }
                 }
                 else if ("${list[i]}" == "'Deploy'" && env.ACTION == 'DEPLOY') {
                  stage('Deploy') {
                   script {
                      TEMP_STAGE_NAME = "$STAGE_NAME"
                      if (env.DEPLOYMENT_TYPE == 'EC2') {
                          sh 'ssh -o "StrictHostKeyChecking=no" ciuser@$DOCKERHOST "docker pull public.ecr.aws/lazsa/zalenium:latest"'
                          sh 'ssh -o "StrictHostKeyChecking=no" ciuser@$DOCKERHOST "docker pull elgalu/selenium:latest"'
                          sh 'ssh -o "StrictHostKeyChecking=no" ciuser@$DOCKERHOST "docker stop ${JOB_BASE_NAME} || true && docker rm ${JOB_BASE_NAME} || true"'
                          sh 'ssh -o "StrictHostKeyChecking=no" ciuser@$DOCKERHOST "docker run -d --restart always --name ${JOB_BASE_NAME} -p $SERVICE_PORT:4444 -e PULL_SELENIUM_IMAGE=true -v /var/run/docker.sock:/var/run/docker.sock  -v /tmp/videos-cucu:/home/seluser/videos --privileged  public.ecr.aws/lazsa/zalenium:latest start --timeZone "UTC""'
                          env.REMOTE_DRIVER_HOST = "http://$DOCKERHOST:$SERVICE_PORT"
                      }
                      if (env.DEPLOYMENT_TYPE == 'KUBERNETES') {
                          if (env.ARTIFACTORY == 'JFROG') {
                              withCredentials([file(credentialsId: "$KUBE_SECRET", variable: 'KUBECONFIG'), usernamePassword(credentialsId: "$ARTIFACTORY_CREDENTIALS", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                              sh '''
                              kubectl create ns "$namespace_name" || true
                              kubectl -n "$namespace_name" create secret docker-registry regcred --docker-server="$REGISTRY_URL" --docker-username="\"$USERNAME\"" --docker-password="\"$PASSWORD\"" || true
                              '''
                              }
                          }
                          if (env.ARTIFACTORY == 'ACR') {
                              withCredentials([file(credentialsId: "$KUBE_SECRET", variable: 'KUBECONFIG'), usernamePassword(credentialsId: "$ARTIFACTORY_CREDENTIALS", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                              sh '''
                              kubectl create ns "$namespace_name" || true
                              kubectl -n "$namespace_name" create secret docker-registry regcred --docker-server="$ACR_LOGIN_URL" --docker-username="\"$USERNAME\"" --docker-password="\"$PASSWORD\"" || true
                              '''
                              }
                          }
                          withCredentials([file(credentialsId: "$KUBE_SECRET", variable: 'KUBECONFIG')]) {
                          sh '''
                          kubectl create ns "$namespace_name" || true
                          helm upgrade --install $RELEASE_NAME -n "$namespace_name" zalenium --atomic --timeout 300s
                          sleep 10
                          '''
                            script {
                             env.temp_service_name = "$RELEASE_NAME-zalenium".take(63)
                             def url = sh (returnStdout: true, script: '''kubectl get svc -n "$namespace_name" | grep "$temp_service_name" | awk '{print $4}' ''').trim()
                              if (url != "<pending>") {
                                env.REMOTE_DRIVER_HOST = "http://$url"
                                print("##\$@\$ http://$url/dashboard ##\$@\$")
                              }
                            }
                          }
                      }
                   }
                   
                  }
                 }
                 else if ("${list[i]}" == "'UnitTests'"  && env.ACTION == 'DEPLOY') {
                   stage('Unit Tests') {
                     script{
                       TEMP_STAGE_NAME = "$STAGE_NAME"
                       sh '''
                           sleep 60
                           mvn clean install -DREMOTE_DRIVER_HOST="$REMOTE_DRIVER_HOST"
                       '''
                     }
                     
                   }
                 }
                  else if ("${list[i]}" == "'GenerateHTMLreport'"  && env.ACTION == 'DEPLOY') {
                 
                    stage('GenerateHTMLreport') {
                            cucumber reportTitle: 'Report',
                                    fileIncludePattern: '**/*.json',
                                    trendsLimit: 10,
                                    classifications: [
                                        [
                                            'key': 'Browser',
                                            'value': 'Chrome'
                                        ]
                                    ]
                        }
                 }
                 else if ("${list[i]}" == "'Destroy'" && env.ACTION == 'DESTROY') {
                  stage('Destroy') {
                    TEMP_STAGE_NAME = "$STAGE_NAME"
                    if (env.DEPLOYMENT_TYPE == 'EC2') {
                      sh 'ssh -o "StrictHostKeyChecking=no" ciuser@$DOCKERHOST "docker stop ${JOB_BASE_NAME} || true && docker rm ${JOB_BASE_NAME} || true"'
                    }
                    if (env.DEPLOYMENT_TYPE == 'KUBERNETES') {
                      withCredentials([file(credentialsId: "$KUBE_SECRET", variable: 'KUBECONFIG')]) {
                      sh '''
                      helm uninstall $RELEASE_NAME -n "$namespace_name"
                      '''
                      }
                    }
                    
                  }
                 }
               }
             }
           }
     }
  }
  post { 
        failure {
                  pushToCollector()
                }
                success {
                  pushToCollector()
                }
                aborted {
                            pushToCollector()
                        }
  }

}
