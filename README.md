# 🛍 선착순 구매
Spring boot 기반의 **대규모의 트래픽을 처리해 선착순 구매 기능을 지원**하는 E-commerce 서비스 입니다.  
**Redis와 Kafka** 를 활용해 **동시성 처리** 와 빠른 주문 처리를 구현하였으며, **캐싱을 통한 실시간 재고 관리** 기능을 제공합니다.
또한 **MSA(Microservices Architecture)** 를 도입하여 각 서비스의 확장과 유지보수성을 향상시켰습니다.

일반적인 상품 구매 기능과, 한정된 수량의 상품을 특정 시간에 오픈하여 선착순으로 구매할 수 있는 기능을 제공합니다.  


- 개발 기간 : 2024.08.07 ~ 2024.09.04
- [API 명세서](https://documenter.getpostman.com/view/30578335/2sA3s3GqpS)  

## Project Archictecture
![Architecture](./img/architecture.png)  

## ERD
![ERD](./img/erd.png)  

## Tech Stack
- IDE : IntelliJ IDEA Community
- Java : JDK 17
- Spring Boot / Spring Security ： 3.3.0
- JWT : 0.12.3
- Gradle : 7.3.2
- MySQL :  9.0.1
- Redis : 7.4.0
- Kafka : 2.8.1
- Docker : 27.0.3
- Discoverey service : Netflix Eureka
- API Gateway : Spring Cloud Gateway
- SMTP: Spring Boot Mail Starter

## 구현기능
- **OpenFeign과 Resilience4j를 활용한 마이크로서비스 간 통신**
  - 신뢰성 있는 통신 및 장애 복원력 강화
- **KafKa를 사용한 비동기 이벤트 처리**
  - 비동기적인 메시지 브로커 역할을 하는 Kafka를 통해 마이크로서비스 간의 느슨한 결합을 유지하며 이벤트 기반 아키텍처를 구현.
  - 비즈니스 이벤트 처리 시 Kafka의 높은 처리량을 활용하여 성능 최적화.
- **Redisson 분산락을 통한 동시성 문제 해결**
  - Redis 기반의 Redisson Lock을 사용하여 재고나 주문 처리 시 발생할 수 있는 동시성 문제를 방지.
- **Redis 기반 실시간 캐싱 및 재고 관리**
  - Cache-Aside / Write-Behind 캐시 전략을 통해 대규모 트래픽에서도 빠르고 안정적인 재고 조회 및 관리
- **Api Gateway를 통한 라우팅 및 인가 기능 구현**
  - 클라이언트의 요청을 적절한 마이크로서비스로 라우팅하고, 부하를 분산시켜 시스템 효율성을 높임.
  - Gateway 레벨에서 JWT 토큰 검증을 통해 비인가 사용자의 접근을 차단.
- **Netflix Eureka를 활용한 서비스 등록 및 디스커버리**
  - Eureka를 통해 마이크로서비스 간의 동적 등록 및 검색이 가능하도록 설정.
- **[JWT 토큰 발급을 통한 무상태 인증 시스템 구현](https://jjuya.tistory.com/197)**
  - Spring Security와 JWT(Json Web Token)를 사용하여 사용자 인증을 처리하며, 무상태(stateless) 방식으로 세션 없이 인증을 유지
- **spring batch와 스케쥴러를 이용한 배송 상태 및 재고 복구 관리**
  - 정기적으로 배송 상태 업데이트 및 재고 복구 작업을 자동화
- **이메일 인증을 통한 회원가입**
  - Google SMTP를 활용하여 회원가입시 이메일 인증 진행
- **[AES 기반 양방향 암호화를 통한 보안 강화](https://jjuya.tistory.com/198)**
  - 중요한 사용자 정보나 민감한 데이터를 안전하게 보호할 수 있도록 개인정보 양방향 암호화 처리.


## 트러블슈팅
- **동시성 문제 해결**
  - Redis의 Redisson Lock을 사용하여 분산 환경에서도 안전하게 동시성 제어 구현
- **보안 강화를위한 Refresh 토큰 추가 발급**
  - JWT 기반 인증 시스템에서 Refresh 토큰을 활용하여 액세스 토큰이 만료된 경우 재발급을 처리. 이를 통해 사용자 로그인 세션의 지속성을 유지하면서도, 보안성을 강화.
- **Circuit Breaker/ Retry 를 사용하여 마이크로 서비스 간 장애 전파방지**
  - 시적인 장애 또는 네트워크 지연이 발생할 경우에도 자동으로 재시도를 진행
  - 특정 마이크로서비스에서 지속적인 장애가 발생하면 그 서비스로의 요청을 차단. 이를 통해 장애가 다른 서비스로 전파되는 것을 방지


## 성능개선
- **[Redis 캐싱을 통한 성능 개선](https://jjuya.tistory.com/207)**
  - 실시간 재고 관리 및 데이터 조회에서 Redis 캐싱을 적용하여 성능을 향상.
  - TPS : 62.5/sec -> 94.9/sec (약  51.84%성능 개선)

- **kafka 비동기 처리로 성능개선**
  - 기존의 동기 방식으로 OpenFeign을 통해 마이크로서비스 간 통신하던 부분을 Kafka 비동기 이벤트 처리로 전환하여 성능을 최적화.
  - TPS : 58.8/sec -> 81.1/sec(약 37.9%성능 개선)

- **상품페이지 페이징처리**


## 버전
