# PandaN

안그래도 복잡한데, <br>
일까지 복잡할 필요는 없잖아요. <br>
<br>
**머리 아플땐 [PandaN](https://pandan.link/).**<br>
<br>
<img width="800px" alt="a753bd0a774f9e7b" src="https://user-images.githubusercontent.com/70243735/131826995-82cc5a66-88f4-47aa-beb2-3d2d1ddda4f8.png">

<br>

## 1. PandaN 소개
> 가장 쉽고 편하게 시작할 수 있는 협업툴

지난 1~2년을 표현할 수 있는 키워드는 '언택트'입니다. <br>
코로나로 인해 늘어나는 비대면 협업 공간의 수요는 점점 늘어나나, 그에 상응하는 공급은 부족합니다.  <br>
이미 기존에 협업툴들이 존재하고 있지만 **난해하고, 배우기 어렵다는 공통점**들을 발견할 수 있었습니다.  <br>
<br>
이에 저희 팀 판단은 **가장 쉽고 편하게 시작할 수 있는 협업툴, PandaN**을 기획하게 되었습니다.

<br>

## 2. Team PandaN 소개

:heavy_check_mark: 개발 결과물에 대한 책임은 개인이 아닌, **팀 모두**에게 있습니다. <br>
<br>
:heavy_check_mark: 그 어떤 의사결정도 당연한 것이 없기 때문에 서로에게 **끊임없이 되묻습니다**. <br>
<br>
:heavy_check_mark: PR을 Merge하기 전에 **모든 팀원들의 approve**가 필요합니다. <br> 
<br>
:heavy_check_mark: **코드리뷰**를 통해 오타, 버그의 조기 발견하며, 더 좋은 해결방안을 찾기 위해 함께 고민합니다. <br> 
<br>
:heavy_check_mark: 개발 중 드는 의문에 대해서는 **이슈**로 남겨 함께 토론합니다. <br> 
<br>
:heavy_check_mark: 토론한 내용을 바탕으로 **팀 노션**에 문서화하여 기록으로 남깁니다. <br> 
<br>


|  [강승연](https://github.com/tmddusgood)  |  [김성경](https://github.com/Code-Angler)  |  [이태강](https://github.com/BlossomWhale)  |  [최민서](https://github.com/mangdo)  | 
| :----------: |  :--------:  |  :---------: |  :---------: | 
| <img src="https://user-images.githubusercontent.com/70243735/131817966-37cd30fa-41e3-4806-a5dc-30b639b55114.png" width="150px" alt="승연"/> | <img src="https://user-images.githubusercontent.com/70243735/131817957-e3c4e507-2013-4289-a97a-0f532dbf5b69.jpg" width="100px" alt="성경"/> | <img src="https://user-images.githubusercontent.com/70243735/131817961-fe1ad30c-4ba5-44e4-9f09-1de7ea8ae7ab.png" width="100px" alt="태강"/> | <img src="https://user-images.githubusercontent.com/70243735/131817954-a68398bc-c7c1-4b9d-9044-02e09d4b61db.png" width="130px" alt="민서"> |


<br>

## 3. 기술 스택
**`Back-end`**
- Java 8
- SpringBoot 2.5.2
- Spring Security
- Gradle 7.0.2
- JPA
- QueryDSL
- MySQL 8.0

**`Front-end`**
- React ([React Repository이동](https://github.com/Team-PandaN/Team-PandaN-Front))

**`DevOps`**
- Jenkins
- Docker
- Nginx
- AWS EC2 (Centos7)
- AWS RDS (MySQL 8.0)
- AWS S3

<br>

## 4. 아키텍처 설계
<img src="https://user-images.githubusercontent.com/39071543/131789746-8126a2b5-2709-40bb-b2be-7d3c9b953f44.png" width="800px">

<br>


## 5. 개발 포인트
> 자세한 내용은 링크를 참고해주세요.

* [Git Flow를 따르는 협업 프로세스](https://pandan.notion.site/945341227bd64432a973d4294b89db37)
* [만장일치 방식의 철저한 코드 리뷰](https://github.com/Team-PandaN/Team-PandaN-Back/pull/21)
* [팀 자체 기술 블로그를 운영하여 문서화](https://pandan.notion.site/ab1407eab5154d108f562b84f8cef731)
* [Mysql에서 실행계획 분석 후 쿼리 튜닝](https://www.notion.so/Mysql-0ca7bcd2a4b34333b3880693c7ed9e88)
* 동시성 제어를 통한 DB 무결성 확보
  * [동시 수정 제어 - Lock Manager](https://pandan.notion.site/930f626a77f642ab8f49ceffe035e3dc)
  * [동시 이동 제어 - 비즈니스 로직과 격리 수준 차별화](https://pandan.notion.site/58f5dd7e7ab340e8bec32c720a2ccc12)
* [Jenkins Pipeline을 사용하여 CI/CD 환경 구축](https://pandan.notion.site/Jenkins-Pipeline-624eedc79a8e4c4083a688d72f29094d)
* [Docker를 이용하여 CD 구현](https://pandan.notion.site/Github-with-5a0d1647d897422ab8407aabad58f54d)
* [Nginx의 Reversed-Proxy를 이용하여 로드밸런싱 및 Blue/Green 방식의 무중단 배포 적용](https://www.notion.so/Nginx-Blue-Green-fad34415851c42bd966a9a3d91b9f633)
* [JMeter를 이용하여 스트레스 테스트](https://pandan.notion.site/Apache-JMeter-7866603a868748ad97ca7eda1f4d8cd3)

<br>

## 6. 설계
* API 설계 - [노션 페이지](https://pandan.notion.site/98a6558c7da34f22b18a61e499ddb06d?v=732629e140434e29a74792f0c72a57d6)
* ERD - [위키](https://github.com/Team-PandaN/Team-PandaN-Back/wiki/PandaN-ERD)
* 유저 시나리오 - [Whimsical](https://whimsical.com/5xexH5xdKF2Ht6ruzf9FuH)
