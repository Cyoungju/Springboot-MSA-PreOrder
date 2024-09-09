# 🛍 예약 구매 서비스
**대규모의 트래픽을 처리해 선착순 구매 기능을 지원**하는 E-commerce 서비스  
일반적인 상품 구매 기능과, 한정된 수량의 상품을 특정 시간에 오픈하여 선착순으로 예약 구매할 수 있는 기능을 제공  
- **개발 기간** : 2024.08.07 ~ 2024.09.06
- **프로젝트 블로그** [(바로가기)](https://jjuya.tistory.com/category/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8/Springboot-MSA-%20PreOrder)
- **API 명세서** [(바로가기)](https://documenter.getpostman.com/view/30578335/2sA3s3GqpS)  
- **테스트 시나리오** [(바로가기)](https://documenter.getpostman.com/view/30578335/2sA3s3GqpS)  


## 목차
- [🛠️ 프로젝트 아키텍처](#프로젝트-아키텍처)
- [🚀 사용 기술](#사용기술)
- [⚖️ 기술적 의사 결정](#기술적-의사-걀정)
  - Kafka
  - Redis
  - MSA
- [💻 구현 기능](#구현기능)
- [📈 트러블슈팅](#트러블슈팅)
  - 실시간 재고 캐싱 처리
  - kafka 메시징 처리
  - 동시성 문제 해결
  - 사용자 보안 강화
  - 마이크로소프트 간 장애 전파 방지
- [📌 버전](#버전)  

 
## 🛠️ 프로젝트 아키텍처
### ERD 
![ERD](./img/erd.png)  

### 서비스 아키텍처
![Architecture](./img/architecture.png)  
대규모 트래픽을 처리 하면서도 각 서비스 간의 독립성을 유지하고 성능을 최적화 하기 위해 아카텍쳐 설계
<details>
<summary>
자세히보기
</summary>  

_**Spring Cloud Gateway**_
- 클라이언트 요청을 중앙에서 관리하고, 로드 밸런싱과 인증/인가 처리  
- 마이크로 서비스 간의 API게이트웨이 역할을 위해 사용

_**Netflix OSS (Eureka)**_
- 서비스 디스커버리 및 로드밸런싱을 위한 툴  
- 각 서비스가 서로의 위치를 자동을 찾아 통신할수 있게 도와주기 위해 도입

_**USER-SERVICE**_  
- 유저관리, 마이페이지 관련 담당
- 유저관리 - 회원가입, 로그인, 로그아웃 
- 마이페이지 - wishList 조회, wishList상품 추가 및 삭제, 주문 내역 확인, 배송지 등록

_**PRODUCT-SERVICE**_ 
- 상품 재고, 상태 관리등 상품 관련 담당
- 상품 관리 - 판매불가 상품을 제외한 상품 리스트 조회, 상품 상세 페이지 조회
- 특정 상품 주문 시간 설정 - 선착순 예약 판매 기능

_**ORDER-SERVICE**_
- 상품의 주문 배송 관련 담당
- wishList 상품주문, 재고복구, 선착순 예약판매 구매, 상세 주문 내역 확인 
- 주문상태 변경 자동화 처리

_**PAYMENT-SERVICE**_
- 결제 프로세스, 결제 정보 관련 담당
 
</details>

## 🚀 사용기술
### IDE  
<img src="http://img.shields.io/badge/intellijidea-000000?style=for-the-badge&logo=Python&logoColor=white">  

### 언어 / 프레임워크   
<img src="http://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="http://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=SPRINGBOOT&logoColor=white">   <img src="http://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">


### 인증/인가    
<img src="http://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white"> <img src="http://img.shields.io/badge/JSON Web Token-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"> <img src="http://img.shields.io/badge/api Gateway -6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="">  


### 데이터베이스    
<img src="http://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="http://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white">

### 메시징  
<img src="http://img.shields.io/badge/kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white">

### DevOps  
<img src="http://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="http://img.shields.io/badge/GitHub-F05032?style=for-the-badge&logo=github&logoColor=white">   

### 테스트툴
<img src="http://img.shields.io/badge/Jmeter-D22128?style=for-the-badge&logo=apachejmeter&logoColor=white"> <img src="http://img.shields.io/badge/POSTMAN-FF6C37?style=for-the-badge&logo=postman&logoColor=white">  

### 기타  
<img src="http://img.shields.io/badge/google smtp-4285F4?style=for-the-badge&logo=google&logoColor=white">  


 
## ⚖️ 기술적 의사 결정
### Kafka

### Redis

### 마이크로소프트 아키텍처(MSA)

 

## 💻 구현기능
### CRUD 기능 구현
- 상품, 회원 정보, wishList, order, payment  

### 상품 주문, 결제 기능 제공
- 일반적인 상품 구매 와 함께 한정된 수량의 상품을 특정 시간에 오픈하여 선착순으로 예약 구매 지원

### 배송상태, 수량 변경 자동화
- Spring Batch, 스케줄러를 이용한 자동화 작업

### 회원가입시 개인정보 암호화 , 이메일인증 처리
- 개인정보 양방향 AES 암호화 
- Google SMTP 이메일 인증 처리

### 사용자 인증 인가 작업
- Spring Security, API Gateway를 통한 사용자 인증 인가 작업

### JWT 토큰 발급
- JWT 토큰 발급을 통한 무상태 인증 시스템 구현  
  


## 📈트러블슈팅

### 실시간 재고 캐싱 처리  
[자세히보기](https://jjuya.tistory.com/207)  
**[Before]**

- DB 직접 조회로 인한 성능 저하 발생
- 인메모리 DB Redis를 사용하여 실시간 재고 캐싱 처리
- Cache-Aside / Write Through 캐시 전략 사용
- 데이터베이스 정합성은 높였으나, 성능 개선 효과는 미미함  

**[After]**
- 쓰기 전략 변경 Through → Behind
- 성능 개선과 재고의 궁극적 일관성 유지

`TPS : 62.5/sec -> 94.9/sec (약 51.84% 성능 개선)`  


### kafka 메시징 처리  
[자세히보기]()  
**[Before]**

- Feign client를 이용해 마이크로 서비스 간 동기 방식 통신
- 서비스 응답시간 지연, 트래픽 증가 시 시스템 성능 저하
- @async 어노테이션 비동기 통신
- 대규모 트래픽이 발생할 경우, 애플리케이션의 쓰레드 풀 설정에 의존하게 되며, 시스템이 부담을 느끼기 시작하면 성능이 저하

**[After]**

- Kafka 기반의 비동기 메시지 전달 방식으로 전환
- 서비스 간 통신 지연 해소, 시스템 성능 향상

`TPS : 58.8/sec -> 81.1/sec (약 37.9%성능 개선)`  


### 동시성 문제 해결 
[자세히보기]()  
**[Before]**

- 대규모 트래픽 처리 시 여러 스레드가 동시에 동일한 자원에 접근 - 동시성 문제 발생
- 디비락 비관적 락 사용 - 성능저하 데드락 믄제 발생

**[After]**

- 분산 환경에서의 동시성제어를 위해 레디스 분산락 사용
- 레디스 TTL 설정 데드락방지
- 레디스 분산락을 사용해 예약 주문 수량 빠른 조회 및 데이터 동시성문제 해결

### 사용자 보안 강화  
**[Before]**

- AccessToken만 사용 시 토큰 탈취로 인한 보안 취약점 발생

**[After]**

- JWT 기반 인증 시스템에서 Refresh 토큰을 활용하여 AccessToken이 만료된 경우 재발급을 처리
- 이를 통해 사용자 로그인 세션의 지속성을 유지하면서도, 보안성을 강화

### 마이크로소프트 간 장애 전파 방지 
[자세히보기](https://jjuya.tistory.com/205)

  **[Before]**

  - 마이크로 서비스 간 Feign client 통신 시 하나의 서비스 장애가 다른 서비스로 전파되어 전체 시스템에 영향을 미침
  - 서비스 간 결합도로 인한 장애 발생 시 복구가 어려움

  **[After]**

  - Circuit Breaker와 Retry 메커니즘을 도입, 장애가 발생하면 그 서비스로의 요청을 차단
  - 일시적인 장애 또는 네트워크 지연이 발생할 경우에도 자동으로 재시도를 진행

## 📌 버전
### v11.15.7
2024.09.05
- [추가] kafka를 이용해 비동기 이벤트 처리로 전환
- [수정] 결제 로직 수정
- [수정] 코드 리팩토링

### v11.14.7
2024.09.03
- [수정] 결제 API 수정
- [수정] 동시성 문제 해결을 위한 DB재고 비관적 락 설정
- [추가] 테스트 시나리오 작성

### v11.13.6
2024.08.31
- [추가] 결제 테이블 추가
- [수정] 결제 로직및 재고 복구 로직 수정

### v10.13.6
2024.08.30
- [오류] Redis 분산락을 활용한 동시성 문제 해결
- [수정] Spring Batch를 이용해 주문 시간 제한및 수량 복구 업데이트
- [수정] 주문 로직 수정
- [추가] 선차순 구매를 위핸 상품구매 오픈시간 API작성

### v10.12.5
2024.08.29
- [수정] 코드 리팩토링
- [수정] Redis로 재고 캐싱 처리

### v10.11.5
2024.08.25
- [추가] 결제 api 작성

### v9.11.5
2024.08.23
- [수정] 주문로직 수정
- [추가] Circuit Breaker/ Retry 추가

### v9.10.5
2024.08.22
- [추가] resilience4j 추가

### v8.9.5
2024.08.21
- [추가] refreshToken redis에 추가 TTL
- [수정] accessToken 블랙리스트 설정

### v8.8.5
2024.08.21
- [추가] 배송 로직 추가
- [추가] order에 배송지 추가

### v8.7.5
2024.08.20
- [추가] FeignClient 서비스 통신

### v7.7.5
2024.08.19
- [추가] API gateway를 통한 사용자 인증 처리- JWT 토큰 검증

### v7.6.5
2024.08.18
- [추가] spring cloud gateway 구현
- [추가] API gateway를 Eureka와 연동

### v6.6.5
2024.08.16
- [추가] Netflix Eureka를 사용해서 Client-Side Discovery 구현

### v5.6.5
2024.08.16
- [변경] 멀티모듈 코드 리팩토링
- [변경] MSA(Microservices Architecture) 구조로 전환

### v4.5.5
2024.08.15
- [추가] 주문 상태 일괄 처리 및 자동화를 위한 Spring Batch를 추가

### v4.4.3
2024.08.14
- [추가] Order, OrderItem 테이블 작성
- [공통] 생성 시간 및 수정 시간 컬럼을 전체 테이블에 추가

### v3.4.3
2024.08.13
- [추가] 위시리스트 기능을 추가, 상품 추가/수량 변경 및 상품 삭제 API 작성
- [추가] AccessToken과 RefreshToken 발급 기능을 추가
- [추가] 로그아웃 API작성

### v2.4.3
2024.08.12
- [추가] 페이징 처리기능 추가
- [수정] 전체 상품 리스트에서 AVAILABLE 상태인 상품만 조회
- [수정] 배송지 기본 배송지 상태 컬럼 추가

### v2.3.3
2024.08.11
- [추가] 상품 테이블추가
- [추가] 전체 상품 리스트 및 상세 페이지 API 작성
- [수정] JWT 만료 시간 오류를 수정
- [추가] 배송지 테이블 추가

### v1.1.2
2024.08.10
- [추가] Spring Security를 사용한 회원가입 및 로그인 API 작성
- [추가] JWT 토큰 발급 기능을 추가
- [추가] 회원가입 시 이메일 인증을 위한 Google SMTP를 추가
- [수정] 개인정보, 이메일, 비밀번호를 암호화하여 저장
- [공통] API 유틸리티 클래스를 작성

### v0.0.1
2024.08.08
- [패치]: Docker 환경설정 추가
