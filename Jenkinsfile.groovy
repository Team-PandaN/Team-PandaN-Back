node {
    gitPull("release")

    build()

    junitTest()

    dockerhubPush()

    // 현재 운영되고 있는 서버가 set1인지 set2인지 확인
    current_set = checkCurrentSet();

    // set1 또는 set2 서버에 배포
    deploy(current_set)

    // 서버가 제대로 실행될 때까지 기다리기. 30초는 짧음
    sleep 50

    // set1 또는 set2 서버에 배포확인 체크
    checkDeploy(current_set);

    // nginx가 가리키는 서버 set를 변경
    nginx()

    // 최종 배포 성공 알림
    slackSend (channel: '#zero-downtime-jenkins', color: '#26B581', message: "DEPLOY SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
}

// github으로 부터 source code를 가져옵니다.
def gitPull(String branch) {
    stage('Git pull') {
        echo 'git pull ' + branch
        git branch: branch, url: "${GIT_URL}"
    }
}

// build를 진행합니다.
def build() {
    stage('Build') {
        sh "chmod 544 ./gradlew"
        try{
            sh "./gradlew clean build"
        } catch (error){
            junitTest()
            slackSend (channel: '#zero-downtime-jenkins', color: '#FF0000', message: "BUILD FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
            throw error;
        }
        sh "docker build -t ${DOCKER_HUB_URL}:${BUILD_NUMBER} ."
    }
}

// set1 또는 set2 서버에 배포합니다.
def deploy(String current_set){
    stage('deploy') {
        if (current_set == "set1"){
            // set1이 지금 운영하고있는 서버라면 set2에 배포한다
            deploySet2()
        }else if(current_set == "set2"){
            // set2가 지금 운영하고있는 서버라면 set1에 배포한다
            deploySet1()
        }
    }
}

// worker instance set1에 SSH 접속하여 병렬수행으로 배포를 진행합니다.
def deploySet1() {
    parallel 'deploy-set1-1' : {
        sshPublisher(publishers:
                [sshPublisherDesc(
                        configName: 'worker-1',
                        transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand:
                                '''
                    docker container stop $(docker ps -q -f "status=running")
                    nohup docker run --rm -p 80:8080 -v "/home/centos/application-prod.yml:/application-set1.yml:z" -e "SPRING_PROFILES_ACTIVE=set1" ${DOCKER_HUB_URL}:${BUILD_NUMBER} > nohup.out 2>&1 &
                    ''',
                                execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')],
                        usePromotionTimestamp: false,
                        useWorkspaceInPromotion: false,
                        verbose: false)
                ]
        )
    }, 'deploy-set1-2' : {
        sshPublisher(publishers:
                [sshPublisherDesc(
                        configName: 'worker-3',
                        transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand:
                                '''
                    docker container stop $(docker ps -q -f "status=running")
                    nohup docker run --rm -p 80:8080 -v "/home/centos/application-prod.yml:/application-set1.yml:z" -e "SPRING_PROFILES_ACTIVE=set1" ${DOCKER_HUB_URL}:${BUILD_NUMBER} > nohup.out 2>&1 &
                    ''',
                                execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')],
                        usePromotionTimestamp: false,
                        useWorkspaceInPromotion: false,
                        verbose: false)
                ]
        )
    }
}

// worker instance set2에 SSH 접속하여 병렬수행으로 배포를 진행합니다.
def deploySet2() {
    parallel 'deploy-set2-1' : {
        sshPublisher(publishers:
                [sshPublisherDesc(
                        configName: 'worker-2',
                        transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand:
                                '''
                    docker container stop $(docker ps -q -f "status=running")
                    nohup docker run --rm -p 80:8080 -v "/home/centos/application-prod.yml:/application-set2.yml:z" -e "SPRING_PROFILES_ACTIVE=set2" ${DOCKER_HUB_URL}:${BUILD_NUMBER} > nohup.out 2>&1 &
                    ''',
                                execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')],
                        usePromotionTimestamp: false,
                        useWorkspaceInPromotion: false,
                        verbose: false)
                ]
        )
    }, 'deploy-set2-2' : {
        sshPublisher(publishers:
                [sshPublisherDesc(
                        configName: 'worker-4',
                        transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand:
                                '''
                    docker container stop $(docker ps -q -f "status=running")
                    nohup docker run --rm -p 80:8080 -v "/home/centos/application-prod.yml:/application-set2.yml:z" -e "SPRING_PROFILES_ACTIVE=set2" ${DOCKER_HUB_URL}:${BUILD_NUMBER} > nohup.out 2>&1 &
                    ''',
                                execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')],
                        usePromotionTimestamp: false,
                        useWorkspaceInPromotion: false,
                        verbose: false)
                ])
    }
}

// dockerhub로 만든이미지를 push 합니다.
def dockerhubPush(){
    withCredentials(
            [[$class: 'UsernamePasswordMultiBinding',
              credentialsId: 'docker-hub',
              usernameVariable: 'DOCKER_USER_ID',
              passwordVariable: 'DOCKER_USER_PASSWORD']]
    ){
        stage('push'){
            sh "docker login -u ${DOCKER_USER_ID} -p ${DOCKER_USER_PASSWORD} docker.io"
            sh "docker push ${DOCKER_HUB_URL}:${BUILD_NUMBER}"
        }
    }
}

// junit test 결과
def junitTest(){
    stage('Junit Test'){
        junit testResults: '**/test-results/test/*.xml'
    }
}

// 현재 운영되고 있는 서버가 set1인지 set2인지 확인합니다
def checkCurrentSet(){
    current_set = """${sh( returnStdout: true, script: 'curl -s ${NGINX_URL}' )}"""
    if (current_set != "set1" && current_set != "set2"){
        echo "NGINX가 set1도 set2도 아닌 이상한 곳을 가리키고 있었다"
        // set1을 켜주기 위해, nginx 서버가 이전에 가리켰던 곳을 set2라고 임의로 설정한다.
        current_set = "set2"
    }
    return current_set
}

// set1 혹은 set2 서버가 배포를 제대로 했는지 확인합니다.
def checkDeploy(String current_set){
    stage('Check Deploy'){
        echo "check set : "+current_set

        if (current_set == "set1"){
            // set1이 지금 운영하고있는 서버라면 set2에 배포했으니, 이를 체크
            checkDeploySet2()
            echo "server set2 deploy success"

        }else if(current_set == "set2"){
            // set2가 지금 운영하고있는 서버라면 set1에 배포했으니, 이를 체크
            checkDeploySet1()
            echo "server set1 deploy success"

        }
    }
}

// set1에 있는 서버들이 제대로 배포되었는지 병렬 수행 확인합니다.
def checkDeploySet1(){
    parallel 'check-deploy-set1-1' : {
        checkDeployOneServer("${WORKER_1_URL}")
    }, 'check-deploy-set1-2' : {
        checkDeployOneServer("${WORKER_3_URL}")
    }
}

// set2에 있는 서버들이 제대로 배포되었는지 확인합니다.
def checkDeploySet2(){
    parallel 'check-deploy-set2-1' : {
        checkDeployOneServer("${WORKER_2_URL}")
    }, 'check-deploy-set2-2' : {
        checkDeployOneServer("${WORKER_4_URL}")
    }
}

// 해당 서버가 제대로 배포되었는지 확인합니다.
def checkDeployOneServer(String worker_url){

    env.WORKER_URL = worker_url
    try{
        echo "check worker_url : "+"${WORKER_URL}"
        check_deploy = """${sh( returnStdout: true, script: 'curl -s ${WORKER_URL}' )}"""
        if (!(check_deploy == "set1" || check_deploy == "set2")){
            throw Exception
        }
    }catch(error){
        slackSend (channel: '#zero-downtime-jenkins', color: '#FF0000', message: "DEPLOY FAIL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        throw Exception
    }
}

// nginx가 가리키는 서버 set를 변경합니다.
def nginx(){
    stage("nginx switch"){
        sshPublisher(publishers:
                [sshPublisherDesc(configName: 'nginx', transfers:
                        [sshTransfer(cleanRemote: false, excludes: '',
                                execCommand: 'sh switch.sh',
                                execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')
                        ],
                        usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)
                ]
        )
    }
}