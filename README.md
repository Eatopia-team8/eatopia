<img width="1600" height="896" alt="Image" src="https://github.com/user-attachments/assets/3ddabf48-3037-4f0d-9f45-dc000aa2c89f" />

# 🍽️ Eatopia — 식품 전문 쇼핑 플랫폼

## 📚 목차

- [📌 프로젝트 개요](#프로젝트-개요)
- [⭐ 핵심 기능](#핵심-기능)
- [🛠 기술스택](#기술-스택)
- [⚙️ 시스템 설계](#system-design)
- [💡 기술적 의사결정](#기술적-의사결정)
- [⚒️ 트러블 슈팅 및 성능 개선](#troubleshooting)
- [👥 팀원 소개](#팀원-소개)

---

## 📌프로젝트 개요

Eatopia는 **신선식품 중심의 식품 전문 쇼핑 플랫폼**으로,
기존 대형 플랫폼 대비 **낮은 수수료(5%)** 를 제공해

- **판매자 → 더 높은 수익**
- **소비자 → 합리적인 가격**

을 실현하는 것을 목표로 개발되었습니다.

‘마켓컬리’와 유사한 구조를 기반으로 하지만, 더 단순한 시스템과 낮은 수수료 정책을 통해
**개인 판매자·소규모 식품 브랜드도 쉽게 입점할 수 있는 플랫폼**을 지향합니다.

---

## ⭐핵심 기능

| 기능    | 설명                                                      |
|-------|---------------------------------------------------------|
| 인증/인가 | 로그인, 로그아웃, 회원가입, 회원삭제                                   |
| 주문    | 주문 CRUD, 정산 요청, 배송 상태 관리, 환불 요청                         |
| 결제    | 결제 생성, 결제 취소                                            |
| 장바구니  | 상품 담기, 수량 변경, 선택 상품 삭제, 주문 예정 금액 조회                     |
| 쿠폰    | 쿠폰 생성, 발급(다운로드), 사용 및 롤백                                |
| 상품    | 카테고리/상품 CRUD, 이미지 업로드(S3 presigned URL), Redis 캐시 기반 조회 |
| 리뷰    | 주문서당 1건 제한, 수정·삭제, 신고 중복 방지, 정렬·페이징                     |

---

## 🛠기술 스택

### Language

![Java](https://img.shields.io/badge/Java_17-007396?style=flat&logo=java&logoColor=white)  ![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white)

### Database

![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)

### Collaboration

![Notion](https://img.shields.io/badge/Notion-000000?style=flat&logo=notion&logoColor=white)  ![Slack](https://img.shields.io/badge/Slack-4A154B?style=flat&logo=slack&logoColor=white)  ![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white)

### Monitoring

![Grafana](https://img.shields.io/badge/Grafana-F46800?style=flat&logo=grafana&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=flat&logo=prometheus&logoColor=white)

### API

![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=black)

### IDE

![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=flat&logo=intellij-idea&logoColor=white)

### External API

![PortOne](https://img.shields.io/badge/PortOne-4B8BF4?style=flat)  ![Iamport](https://img.shields.io/badge/Iamport-008CEE?style=flat)

### Infra / CI·CD

![AWS EC2](https://img.shields.io/badge/AWS_EC2-FF9900?style=flat&logo=amazon-ec2&logoColor=white)  ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)  ![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat&logo=github-actions&logoColor=white)  ![S3](https://img.shields.io/badge/AWS_S3-569A31?style=flat&logo=amazon-s3&logoColor=white)

### Test

![k6](https://img.shields.io/badge/k6-7D64FF?style=flat&logo=k6&logoColor=white)  ![Postman](https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=postman&logoColor=white)  ![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=flat&logo=junit5&logoColor=white)  ![Swagger Test](https://img.shields.io/badge/Swagger_Test-85EA2D?style=flat&logo=swagger&logoColor=black)

### Security

![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white)  ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=spring-security&logoColor=white)

### Frameworks & Libraries

![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=springboot&logoColor=white)  ![AOP](https://img.shields.io/badge/AOP-6DB33F?style=flat)  ![Interceptor](https://img.shields.io/badge/Interceptor-6DB33F?style=flat)  ![WebClient](https://img.shields.io/badge/WebClient-6DB33F?style=flat)  ![RestTemplate](https://img.shields.io/badge/RestTemplate-6DB33F?style=flat)  ![Lombok](https://img.shields.io/badge/Lombok-8CC84B?style=flat&logo=lombok&logoColor=white)  ![Spring Batch](https://img.shields.io/badge/Spring_Batch-6DB33F?style=flat)  ![QueryDSL](https://img.shields.io/badge/QueryDSL-0769AD?style=flat)  ![Scheduler](https://img.shields.io/badge/Scheduler-6B7280?style=flat)

---

<a id="system-design"></a>

## ⚙️시스템 설계

### ERD

<img width="4073" height="2622" alt="Image" src="https://github.com/user-attachments/assets/5a3f4f1d-dcef-4e8b-8fae-87128d226532" />

### API 문서

| 구분            | 설명                 | 메서드    | 엔드포인트                                                           |
|---------------|--------------------|--------|-----------------------------------------------------------------|
| AUTH          | 회원가입               | POST   | /api/eatopia/v1/auth/signup                                     |
|               | 로그인                | POST   | /api/eatopia/v1/auth/login                                      |
|               | 로그아웃               | POST   | /api/eatopia/v1/logout                                          |
|               | 회원 탈퇴              | DELETE | /api/eatopia/v1/auth/withdraw                                   |
| USER          | 비밀번호 재설정           | POST   | /api/eatopia/v1/users/password-reset                            |
|               | 비밀번호 변경 이메일 발송     | POST   | /api/eatopia/v1/users/newpassword-foremail                      |
|               | 프로필 정보 수정          | PATCH  | /api/eatopia/v1/users/update-profile                            |
|               | 비밀번호 변경            | PATCH  | /api/eatopia/v1/users/change-password                           |
|               | 현재 로그인된 사용자 정보     | GET    | /api/eatopia/v1/users/userinfo                                  |
|               | 상세정보 조회            | GET    | /api/eatopia/v1/users/user-detail/{userId}                      |
|               | 유저 검색 기능           | GET    | /api/eatopia/v1/users/search                                    |
|               | 유저 목록 조회(관리자용)     | GET    | /api/eatopia/v1/users/admin-use-userList                        |
| CATEGORY      | 카테고리 등록(관리자)       | POST   | /api/eatopia/v1/categories                                      |
|               | 카테고리 삭제(관리자)       | DELETE | /api/eatopia/v1/categories/{categoryId}                         |
|               | 카테고리 수정(관리자)       | PATCH  | /api/eatopia/v1/categories/{categoryId}                         |
|               | 카테고리 조회(공통)        | GET    | /api/eatopia/v1/categories                                      |
|               | 카테고리 조회(관리자 페이징)   | GET    | /api/eatopia/v1/admin/categories                                |
| PRODUCT       | 상품 등록(판매자)         | POST   | /api/eatopia/v1/products                                        |
|               | 상품 수정(판매자)         | PATCH  | /api/eatopia/v1/products/{productId}                            |
|               | 상품 삭제(판매자/관리자)     | DELETE | /api/eatopia/v1/products/{productId}                            |
|               | 상품 단건 조회           | GET    | /api/eatopia/v1/products/{productId}                            |
|               | 상품 페이징 조회          | GET    | /api/eatopia/v1/products                                        |
|               | 상품 조회 + 전체캐시       | GET    | /api/eatopia/v2/products                                        |
|               | 상품 조회 + 부분캐시       | GET    | /api/eatopia/v3/products                                        |
| CART          | 장바구니 상품 추가         | POST   | /api/eatopia/v1/carts/items                                     |
|               | 장바구니 조회            | GET    | /api/eatopia/v1/carts                                           |
|               | 장바구니 수량 변경         | PATCH  | /api/eatopia/v1/carts/items/{productId}                         |
|               | 장바구니 상품 선택 상태 변경   | PATCH  | /api/eatopia/v1/carts/items/select                              |
|               | 장바구니 상품 삭제         | DELETE | /api/eatopia/v1/carts/items                                     |
| ORDER         | 주문 생성              | POST   | /api/eatopia/v1/orders                                          |
|               | 주문 상세 조회           | GET    | /api/eatopia/v1/orders/{orderId}                                |
|               | 주문 목록 조회           | GET    | /api/eatopia/v1/orders                                          |
|               | 주문 취소              | PATCH  | /api/eatopia/v1/orders/{orderId}/cancel                         |
| PAYMENT       | 결제 정보 생성           | POST   | /api/eatopia/v1/payments                                        |
|               | 결제 수단 변경           | PATCH  | /api/eatopia/v1/payments/{paymentId}/method                     |
|               | 결제 검증              | POST   | /api/eatopia/v1/payments/verify                                 |
| COUPON        | 쿠폰 생성              | POST   | /api/eatopia/v1/coupons                                         |
|               | 쿠폰 다운로드            | POST   | /api/eatopia/v1/coupons/{couponId}/download                     |
|               | 쿠폰 상세 조회           | GET    | /api/eatopia/v1/coupons/{couponId}                              |
|               | 쿠폰 삭제              | DELETE | /api/eatopia/v1/coupons/{couponId}                              |
|               | 내가 생성한 쿠폰 목록 조회    | GET    | /api/eatopia/v1/coupons/created                                 |
|               | 발급받은 쿠폰 목록 조회      | GET    | /api/eatopia/v1/coupons/issued                                  |
|               | 다운로드 가능 쿠폰 조회      | GET    | /api/eatopia/v1/coupons/downloadable                            |
|               | 전체 쿠폰 목록 조회        | GET    | /api/eatopia/v1/all-coupons                                     |
| REVIEW        | 리뷰 생성              | POST   | /api/eatopia/v1/orders/{orderDetailId}/review                   |
|               | 상품 리뷰 조회           | GET    | /api/eatopia/v1/products/{productId}/reviews                    |
|               | 내 상품 리뷰 조회(판매자)    | GET    | /api/eatopia/v1/seller/reviews                                  |
|               | 리뷰 조회(관리자)         | GET    | /api/eatopia/v1/admin/reviews                                   |
|               | 리뷰 수정              | PATCH  | /api/eatopia/v1/reviews/{reviewId}                              |
|               | 리뷰 삭제              | DELETE | /api/eatopia/v1/reviews/{reviewId}                              |
|               | 리뷰 신고              | POST   | /api/eatopia/v1/reviews/{reviewId}/report                       |
|               | 리뷰 신고 내역 조회(관리자)   | GET    | /api/eatopia/v1/admin/reviews/{reviewId}/reports                |
|               | 리뷰 숨김 처리(관리자)      | PATCH  | /api/eatopia/v1/reviews/{reviewId}/hide                         |
| ADDRESS       | 배송지 생성             | POST   | /api/eatopia/v1/addresses/create-address                        |
|               | 배송지 수정             | PATCH  | /api/eatopia/v1/addresses/update-address/{addressId}            |
|               | 기본 배송지 설정          | PATCH  | /api/eatopia/v1/addresses/set-address/{addressId}/default       |
|               | 배송지 조회             | GET    | /api/eatopia/v1/address/check-address                           |
|               | 배송지 상세 조회          | GET    | /api/eatopia/v1/addresses/check-detailAddress/{addressId}       |
|               | 배송지 삭제             | DELETE | /api/eatopia/v1/addresses/delete-address/{addressId}            |
| REFUND        | 환불 요청 생성           | POST   | /api/eatopia/v2/refunds                                         |
|               | 환불 조회              | GET    | /api/eatopia/v2/refunds                                         |
|               | 환불 요청 승인           | POST   | /api/eatopia/v2/refunds/{refundId}/success                      |
|               | 환불 요청 거절           | POST   | /api/eatopia/v2/refunds/{refundId}/canceled                     |
| STATISTIC     | 판매자별 매출 조회         | GET    | /api/eatopia/v1/statistic/seller                                |
|               | 전체 매출 요약 조회        | GET    | /api/eatopia/v1/statistic/summary                               |
|               | 판매자별 매출 조회(redis)  | GET    | /api/eatopia/v2/statistic/seller                                |
|               | 전체 매출 요약 조회(redis) | GET    | /api/eatopia/v2/statistic/summary                               |
| S3            | 단건 presigned URL   | GET    | /api/eatopia/v1/s3/presigned-url                                |
|               | 다중 presigned URL   | POST   | /api/eatopia/v1/s3/presigned-urls                               |
| PRODUCT-IMAGE | 이미지 순서 변경          | PATCH  | /api/eatopia/v1/products/{productId}/images/{imageId}/order     |
|               | 대표 이미지 변경          | PATCH  | /api/eatopia/v1/products/{productId}/images/{imageId}/thumbnail |
|               | 이미지 개별 삭제          | DELETE | /api/eatopia/v1/products/{productId}/images/{imageId}           |
|               | 이미지 개별 추가          | POST   | /api/eatopia/v1/products/{productId}/images                     |
| DELIVERY      | 배달 상태 변경           | PATCH  | /api/eatopia/v1/deliveries/{orderId}/status                     |
| SETTLEMENT    | 판매자 정산 요청(비동기)     | POST   | /api/eatopia/v1/settlements/seller/{sellerId}                   |
|               | 정산 내역 목록 조회        | GET    | /api/eatopia/v1/settlements                                     |
|               | 정산 내역 상세 조회        | GET    | /api/eatopia/v1/settlements/{sellerId}                          |
| SEARCH        | 인기 검색어 조회          | GET    | /api/eatopia/v1/search/keywords/popular                         |

### 와이어프레임

🔗[**와이어프레임 보러가기**](https://miro.com/app/live-embed/uXjVJ67ZM4g=/?embedMode=view_only_without_ui&moveToViewport=-1684,-697,21203,19611&embedId=302688991808)

---

## 💡기술적 의사결정

<details>
<summary>부하 테스트 K6 사용</summary>
<div markdown="1">

### 부하 테스트 도입 배경

서비스의 트래픽이 증가하거나 특정 기능에 많은 동시 요청이 몰릴 경우, 애플리케이션은 예기치 못한 지연, 오류, 데이터 불일치 문제를 겪을 수 있다. 이러한 상황을 사전에 탐지하지 못하면 운영 환경에서 장애로 이어지기 때문에, **실제 트래픽과 유사한 부하를 인위적으로 생성하여 시스템의 안정성과 한계 지점을 검증하는 과정이 필수적**이다.

특히 주요 기능의 **동시성 처리 능력**, **응답 시간 변화**, **리소스 사용량 증가에 대한 시스템 내성** 등을 확인해 장애를 예방하는 것이 목적이다.

---

### 도구 검토 및 대안 비교

부하 테스트 도입을 위해 JMeter, Locust 등 다양한 도구를 검토했다. 각 도구가 제공하는 기능은 충분했지만, **우리 서비스의 트래픽 특성과 테스트 목적에 가장 적합한 도구**를 선정하는 것이 핵심 기준이었다.

#### JMeter

- **장점**: 플러그인 다양, GUI 기반 초기 진입 장벽 낮음
- **단점**:
    - 실행 구조가 무거워 고부하 테스트에서 리소스 사용량 급증
    - XML 기반 설정으로 시나리오 명확성·유지보수성 떨어짐
    - 운영 지표와 직접 비교하기 어려운 결과 포맷
    - 대규모 동시성 테스트 시 스케일링 비용 증가

#### Locust

- **장점**: Python 기반 확장성, Web UI 모니터링
- **단점**:
    - 복잡한 시나리오일수록 오버헤드 증가
    - 고부하 상황에서 병렬 처리 비용 커짐
    - 기본 성능 지표가 제한적이라 추가 구성 필요

---

### k6를 선택한 이유

### 1. 실제 웹 트래픽 구조와 유사한 동시성 모델

k6는 이벤트루프 기반 구조를 사용해 **적은 리소스로 높은 동시성을 재현**할 수 있다.

웹 서비스의 비동기 I/O 중심 트래픽 패턴을 자연스럽게 반영해 테스트 결과의 현실성이 높았다.

### 2. 시나리오 구성력이 뛰어남

- VU(가상 유저), Stage, Threshold 등을 코드로 명확하게 선언
- 복잡한 요청 흐름도 JS 한 파일 안에서 구조적으로 표현 가능
- JMeter처럼 설정이 여러 파일로 분산되지 않음

### 3. 운영 지표와 동일한 형태의 성능 데이터 제공

우리 프로젝트는 **유저 서비스 특성상 응답 속도, 오류율, 처리량** 같은 지표가 서비스 품질에 직접적으로 영향을 미친다.

k6는 이러한 핵심 지표를 **운영 환경에서 사용하는 형식과 동일하게 측정** 할 수 있어, 테스트 결과를 실제 사용자 경험과 가까운 기준으로 해석할 수 있었다.

특히 다음 지표들을 기본적으로 제공해 분석 효율성이 높았다.

- p95 / p99 응답 시간
- RPS
- 오류율(error rate) / 실패율(fail rate)
- 처리량(iteration per second)
- 데이터 송수신량

### 4. 재현성 높은 테스트 구조

테스트 실행이 환경 자원(CPU/메모리)에 의존적이지 않아

같은 조건에서 **결과 편차가 적고 재현성이 높다.**

### 5. 코드 기반 테스트의 장점

- Git 버전 관리가 쉬움
- PR 리뷰 가능
- 시나리오 브랜치 분리 가능
- 팀 내 공유·확장 용이

</div>
</details>

<details>
<summary>CI/CD</summary>
<div markdown="1">

### 도입 배경

프로젝트 코드의 배포 및 수정 작업은 지속적으로 발생할 수 있으며, 수동으로 명령어를 반복 실행하는 방식은 오류 발생 가능성이 높고 효율적이지 못하다.  
따라서 이러한 반복 작업을 자동화하고 안정성을 확보하기 위해 **CI/CD**를 도입하였다.

---

### 사용 도구: **GitHub Actions**

선택 이유:

1. 기존 GitHub 저장소와의 연동이 용이함.
2. Docker Build 파일을 활용하여 Docker Hub로 이미지 푸시 가능, 이를 통해 AWS EC2 및 ECR에 손쉽게 배포 가능.
3. AWS 클라우드 환경에서 실제 서비스 배포 및 실행 경험을 쌓을 수 있음.

</div>
</details>

<details>
<summary>Redis</summary>
<div markdown="1">

### 도입 배경

마켓형 플랫폼 특성상 데이터가 급격히 증가할 수 있으며, 조회 성능 저하가 발생할 수 있다.  
반복 조회되는 데이터를 효율적으로 캐싱하고 응답 속도를 개선하기 위해 **Redis**를 도입하였다.  
또한 팀원들이 모두 Redis 경험이 있어, 학습 부담 없이 안정적 운영과 빠른 개발이 가능하다는 장점도 있다.
---

### 선택 이유

1. 외부 서버 기반으로 동작하여 데이터 공유와 수평 확장에 유리하다.
2. 다양한 자료구조 제공과 Lock 처리 기능으로 복잡한 동시성 제어에 적합하다.
3. DB 부하를 줄이고 빠른 응답 속도를 제공할 수 있는 캐싱 솔루션으로 최적이다.

</div>
</details>

___

<a id="troubleshooting"></a>

## ⚒️트러블 슈팅 및 성능 개선

<details>
<summary>로그인 시 JPA '쓰기 지연'으로 인한 Duplicate Key Exception 해결기</summary>
<div markdown="1">

### 1. 문제 상황 정의

문제는 Refresh Token 발급 및 비밀번호 재발급용 인증 토큰을 생성하는 로직에서 발생했습니다. 테스트 중 사용자가 짧은 시간 안에 로그인을 2회 연속 시도하거나, 비밀번호 재발급 버튼을 2번 연속 클릭하는 경우 `Unique` 제약 조건을 위배했다는 500 에러가 발생했습니다.

분명 로직상으로는 기존 토큰을 `delete()` 한 뒤 새로운 토큰을 `save()` 하도록 설계했음에도 불구하고, DB는 중복 키 오류를 반환했습니다.

### 2. 원인 분석: JPA의 '쓰기 지연(Write-Behind)'

원인 분석을 위해 JPA의 동작 방식과 실제 실행되는 쿼리를 확인했습니다. 문제는 **JPA의 '쓰기 지연(Write-Behind)'**이라는 성능 최적화 기능 때문에 발생했습니다.

`AuthLoginRequest`를 처리하는 로직의 일부입니다.

```java
// Refresh Token 생성 및 저장 로직
authRepository.findByUserId(user.getId()).

ifPresent(token ->{
        authRepository.

delete(token); // 1. 삭제 호출
});

String refreshTokenValue = UUID.randomUUID().toString();
RefreshToken refreshToken = RefreshToken.create(user.getId(), refreshTokenValue);
authRepository.

save(refreshToken); // 2. 저장 호출
```

JPA의 트랜잭션 내 동작 순서는 다음과 같았습니다.

1. `authRepository.delete(token)`가 호출되어도, JPA는 `DELETE` 쿼리를 즉시 DB에 실행하지 않습니다. 대신, 1차 캐시에만 변경 사항을 반영하고 `DELETE` 쿼리는 'SQL 쓰기 지연 저장소'에 보관합니다.
2. 이후 `authRepository.save(refreshToken)`가 호출되면, `INSERT` 쿼리 역시 'SQL 쓰기 지연 저장소'에 쌓입니다.
3. **문제:** 트랜잭션이 커밋되는 시점에, JPA는 최적화를 위해 **`INSERT` 쿼리를 `DELETE` 쿼리보다 먼저 실행**시켰습니다.
4. 결과적으로 `DELETE`가 되기 전에 `INSERT`가 시도되면서 `Unique` 제약 조건을 위배하는 `Duplicate Key Exception`이 발생한 것이었습니다.

### 3. 1차 해결: `flush()`를 이용한 명시적 쿼리 실행

이 문제를 해결하기 위해서는 `DELETE` 쿼리가 `INSERT` 쿼리보다 항상 먼저 실행되도록 순서를 보장해야 했습니다. 이를 위해 `repository.flush()`를 명시적으로 호출하는 방식을 사용했습니다.

```java
// Refresh Token 생성 및 저장 로직
authRepository.findByUserId(user.getId()).

ifPresent(token ->{
        authRepository.

delete(token);
    authRepository.

flush(); // <-- 1. DELETE 쿼리를 즉시 강제 실행
});

String refreshTokenValue = UUID.randomUUID().toString();
RefreshToken refreshToken = RefreshToken.create(user.getId(), refreshTokenValue);
authRepository.

save(refreshToken); // <-- 2. 이후 save 실행
```

기존 토큰을 `delete()` 한 직후 `flush()`를 호출하여, 쓰기 지연 저장소에 있던 `DELETE` 쿼리를 DB에 강제로 즉시 실행시켰습니다. 그 후 `save()`가 호출되도록 하여, **'삭제'가 '저장'보다 항상 먼저 실행**되도록 순서를 명확히 보장했습니다.

이 방식을 적용한 결과, 동일한 연속 요청 테스트에서 `Duplicate Key Exception`은 더 이상 발생하지 않았습니다.

### 4. 회고 및 근본적인 개선 방안

이번 경험을 통해 JPA의 `flush()`와 트랜잭션 생명 주기에 대해 명확히 이해할 수 있었습니다.

하지만 동시에 `flush()` 방식의 **명확한 한계점**도 발견했습니다. 이 해결책은 **단일 스레드(혹은 단일 서버 인스턴스) 환경**에서의 '쿼리 실행 순서' 문제만 해결할 뿐입니다.

만약 0.001초 단위의 극히 짧은 시간에 **두 개 이상의 스레드나 서버가 동시에** 동일한 사용자의 데이터에 접근하려 한다면(Race Condition), `flush()`와 관계없이 여전히 중복 키 문제가 발생할 수 있습니다.

따라서 이 문제를 **원천적으로 해결**하기 위해서는 근본적인 동시성 제어가 필요합니다.
다음 고도화 단계에서는 **`@Lock(PESSIMISTIC_WRITE)`** (비관적 락)을 관련 리포지토리 메서드에 적용하는 것을 고려하고 있습니다. 조회 시점부터 해당 레코드에 락을 걸어, 하나의 트랜잭션이 완료될 때까지 다른 접근을 대기시켜 데이터 정합성을 100% 확보하는 방식으로 개선할 계획입니다.


</div>
</details>
<details>
<summary>쿠폰 발급 시 발생하는 동시성 문제</summary>
<div markdown="1">

문제 상황

- 대규모로 쿠폰을 발급할 때 여러 사용자가 동시에 요청을 보내어 같은 쿠폰이 중복으로 발급되거나, 재고가 음수가 되는 등 일관성이 깨지는 문제가 발생

원인 분석

- 데이터베이스에서 락을 사용하지 않아 동시에 실행되는 쓰레드가 동일한 재고 값을 읽고 각각 성공적으로 커밋

해결 방안

- 동일 쿠폰 행을 `SELECT … FOR UPDATE`로 트랜잭션 동안 잠금하여, 다른 트랜잭션이 해당 쿠폰을 읽고(쓰기 전제) 변경하려고 하면 대기시키거나 타임아웃으로 실패시켜 재고에 대한 제어를 하나의 원자적 작업으로 보장
- 발급 완료 기록에 유니크 제약 조건(사용자ID+쿠폰ID)을 부여해 애플리케이션 오류가 있더라도 DB가 중복 삽입을 차단하도록 함

</div>
</details>
<details>
<summary>Redis ZSET 기반 통계 API 최적화</summary>
<div markdown="1">

`상황`

v1 통계 API는 요청 시마다 집계 쿼리(GROUP BY, SUM)를 실행했습니다. 이로 인해 I/O 부하가 급증하여 API 응답 속도가 불안정해지는 현상이 발생했습니다.

---

`해결`

DB 쿼리 방식에서 캐시 조회 방식으로 변경했습니다.

DB 부하가 없는 매일 새벽 5시에 일별/월별 통계를 집계 하여 Redis ZSET에 저장했습니다.

또한 ZRANGEBYSCORE를 사용하여 startDate와 endDate를 Score 범위로 데이터를 조회하도록 수정했습니다.

---

`결과`

- k6 부하 테스트 결과, p(95) 응답 속도가 3.26초에서 13.81ms로 99% 단축되었습니다.
- 실시간 DB 부하를 제거하여 시스템 전반의 안정성을 확보했습니다.

</div>
</details>
<details>
<summary>장바구니 PESSIMISTIC_WRITE를 사용해 동시성 제어</summary>
<div markdown="1">

**문제 관측**

- 동일 사용자가 여러 기기에서 동시에 상품 추가/삭제 시 충돌 가능성
- 테스트 방법
    1. k6로 동일 사용자 동시 장바구니 요청 시뮬레이션
    2. DB 상태 모니터링

**문제 발생**

- 예상 시나리오**:** 동일 상품 1행 유지, 수량만 증가
- 실제 결과**:** 동시 요청 시 동일 상품 여러 row 생성

```bash
Caused by: org.hibernate.NonUniqueResultException: Query did not return a unique result: 10 results were returned
```

- 문제 요약
    - 동일 상품이 여러 row로 중복 등록
    - 수량 누적 실패 (정합성 깨짐)

**해결 접근 ① : 낙관적 락 + DB 제약**

- **기술 선택 이유**
    - `@UniqueConstraint`로 사용자-상품 중복 방지
    - 접근 빈도가 낮을 것으로 예상되어 **낙관적 락**으로 충돌 감지 시도
- **테스트 결과**
    - 일부 요청에서 **Deadlock** 및 **수량 불일치** 발생
    - 동시에 INSERT 시도하며 row-level lock 충돌
- **Deadlock 발생 원인 분석**
    - InnoDB는 존재하지 않는 row에 INSERT 시도 시에도 **gap lock / insert intention lock** 을 사용
    - 두 요청이 동시에 같은 조건으로 INSERT를 시도하면 각 트랜잭션이 확보한 잠금이 서로의 잠금을 기다리며 교착 상태가 발생
    - 낙관적 락은 “이미 존재하는 row 수정 충돌 감지”에 초점이 있어, **동시 INSERT 경쟁 자체를 직렬화하지 못함**
- **결론**
    - 낙관적 락만으로는 **동시 INSERT + 수량 누적 문제**를 안정적으로 처리하기 어려움
    - INSERT 구간에서 발생하는 잠금 경쟁을 해결할 별도의 제어(비관적 락/직렬화 로직)가 필요함

**해결 접근 ② : 비관적 락(Pessimistic Lock) 적용**

- **기술 선택 이유**
    - 동시 요청 시에도 **정확한 수량 누적**과 **단일 row 보장**이 중요
    - 장바구니는 “쓰기 경쟁(Write Contention)”이 빈번하므로

      → **락을 먼저 점유하여 안전하게 처리하는 비관적 락**이 더 적합

    - 실시간 데이터 정합성이 중요한 장바구니 도메인 특성상

      → 충돌 감지보다 **충돌 예방** 중심의 설계 필요

- **적용 방식**
    - `findByCartIdAndProductIdForUpdate()` → `FOR UPDATE` 적용
    - 락 처리 로직을 별도 서비스로 분리 (`@Transactional(REQUIRES_NEW)`)
    - 상위 서비스는 재시도 루프 (`MAX_RETRY`, delay) 관리

**트러블슈팅 과정**

1. **트랜잭션 분리 문제**
    - 같은 트랜잭션 내에서 `FOR UPDATE`가 실행되어 예외 발생
    - **해결:** 락 전용 메서드에 `propagation = REQUIRES_NEW` 적용해 **독립 트랜잭션 분리**
2. **자기 호출 트랜잭션 미적용**
    - 같은 빈 내부 호출 시 트랜잭션 프록시 미적용
    - **해결:** 락 처리 로직을 **별도 서비스로 분리**
3. **Deadlock 재시도 처리**
    - **동시에 동일 장바구니 row를 수정하려는 요청이 겹칠 때**,

      비관적 락 획득 순서가 충돌하며 **InnoDB의 잠금 순환 대기(lock wait cycle)** 가 발생할 수 있음

      → 이는 Deadlock으로 감지되어 트랜잭션이 롤백됨

    - **해결:** 상위 서비스에 **재시도 루프 + 지연(backoff) 전략** 적용

      → 일시적으로 락 순서 충돌이 발생하더라도 자동으로 다시 시도하여 **정상 흐름 회복**

4. **결과**
    - **동시 10건 요청도 1개 row 유지 + 수량 누적 정확**
    - Deadlock 자동 복구, 데이터 정합성 확보

**개선 방향**

장바구니 트래픽이 높아진 환경일 때 적용해볼 수 있는 방안:

- Redis 기반 사전 제어(중복 요청 방지)로 DB 트랜잭션 경합 자체 감소
- MySQL `ON DUPLICATE KEY UPDATE`를 활용해 **단일 Upsert 쿼리로 수량 증가** 처리
- 장바구니 변경을 메시지 큐에 넣고, 처리 과정은 순차화하여 DB 락 부하 완화

</div>
</details>
<details>
<summary>상품 조회 Redis Cache와 Index 적용한 성능 개선 보고서</summary>
<div markdown="1">

Eatopia는 신선식품 중심의 식품 전문 쇼핑 플랫폼으로

대량 데이터 환경에서의 상품 목록 조회 성능 최적화를 목표로 성능 개선을 진행했습니다

이 문서는 부하 테스트(1차~6차) 결과를 바탕으로

캐시 전략 개선 → DB 인덱스 적용에 이르는 성능 튜닝 과정을 정리한 보고서입니다

### 🧩 공통 환경

| 항목         | 값                                                 |
|------------|---------------------------------------------------|
| **테스트 도구** | k6 + Grafana + InfluxDB                           |
| **서버**     | Spring Boot (Java 17)                             |
| **API 대상** | `/api/eatopia/v1~v3/products`                     |
| **테스트 방식** | 다중 시나리오 랜덤 실행 (페이지, 키워드, 카테고리 등)                  |
| **테스트 시간** | 약 35분                                             |
| **캐시 전략**  | V1 – 캐시 없음V2 – 고정 키 기반 캐시V3 – 선택적 캐시 (카테고리 ID 기반) |
| **시나리오 수** | 6가지 (랜덤 선택)                                       |

- 시나리오

    ```javascript
    const testScenarios = [
        {name: 'basic_list', params: {page: 0, size: 10}},
        {name: 'keyword_search', params: {keyword: '삼성', page: 0, size: 10}},
        {name: 'paging', params: {page: 5, size: 10}},
        {name: 'large_page_size', params: {page: 0, size: 20}},
        {name: 'high_page_number', params: {page: 50, size: 10}},
        {
            name: 'various_keywords',
            params: {
                keyword: ['프리미엄', '베이직', '스마트', '에코'][Math.floor(Math.random() * 4)],
                page: 0,
                size: 10,
            },
        },
    ];
    ```

### 1️⃣ 1~2차 테스트 (기초 안정성 검증)

- **데이터 규모:** 약 **1만 건**
- **테스트 환경:** **200~400 VU**

초기 테스트에서는 전체적으로 안정적인 응답 속도를 보였습니다.

평균 응답 시간은 약 **10~12ms 수준**으로,

대부분의 요청이 빠르게 처리되었습니다.

| 버전                         | 평균 응답(ms)        | p(90)            | p(95)            | 분석                        |
|----------------------------|------------------|------------------|------------------|---------------------------|
| **V1 (no cache)**          | 13.9 → **16.15** | 28.4 → **30.37** | 32.8 → **34.78** | 요청 수가 2배로 늘어나면서 약간의 지연 발생 |
| **V2 (cache all)**         | 3.5 → **3.80**   | 10.1 → **10.42** | 15.9 → **16.94** | 거의 동일, 캐시 효과 유지 👍        |
| **V3 (conditional cache)** | 14.8 → **16.68** | 29.5 → **31.27** | 33.7 → **35.90** | 일부 조건 캐시 미스 발생 시에도 안정적    |

다만, 캐시 구조에 따라 응답 차이가 뚜렷하게 드러났습니다.

- **V1 (캐시 미적용)** 과 **V3 (선택적 캐시 적용)** 은 큰 차이가 없었고,
- **V2 (전체 캐시 적용)** 은 가장 안정적이며 일관된 응답 속도를 보였습니다.

이를 통해 **“캐시 전략의 일관성”이 성능에 직접적인 영향을 준다**는 점이 확인되었습니다.

| 지표                          | 1차 테스트 (200VU) | 2차 테스트 (400VU) | 변화      |
|-----------------------------|----------------|----------------|---------|
| **총 요청 수**                  | 약 160,000      | **323,197**    | ▲ 2배 증가 |
| **평균 응답 시간**                | 약 10.4ms       | **12.2ms**     | 약간 증가   |
| **p(95)**                   | 29.4ms         | **32.7ms**     | 소폭 증가   |
| **에러율 (`http_req_failed`)** | 0.00%          | **0.00%**      | 동일      |
| **checks 성공률**              | 100%           | **100%**       | 동일      |
| **데이터 수신량**                 | 560MB          | **1.1GB**      | 2배 증가   |
| **데이터 전송량**                 | 25MB           | **50MB**       | 2배 증가   |

### 2️⃣ 3차 테스트 (데이터 증가 대비 성능 유지 확인)

- **데이터 규모:** 약 **2만 건**
- **테스트 환경:** **500 VU**

데이터가 1만 건 → 2만 건으로 증가했음에도

전체 응답 속도는 **1~2차 대비 거의 동일한 수준**을 유지했습니다.

특히 **V2(전체 캐시 적용)** 는 **V1 대비 2.2배 빠른 응답 속도**를 기록하며,

캐시 구조의 유효성을 다시 한 번 입증했습니다.

| 버전                | 평균 응답시간 (avg) | 중앙값 (med) | 90% 응답시간 (p90) | 95% 응답시간 (p95) | 최대 응답시간 (max) |
|-------------------|---------------|-----------|----------------|----------------|---------------|
| **v1 (캐시 없음)**    | 226.3ms       | 88.1ms    | 659.4ms        | 785.1ms        | 3679ms        |
| **v2 (전체 캐시 적용)** | 102.6ms       | 5.7ms     | 461.3ms        | 630.1ms        | 3599ms        |
| **v3 (부분 캐시 적용)** | 228.6ms       | 90.1ms    | 664.6ms        | 790.8ms        | 3639ms        |

### 3️⃣ 4차 테스트 (고부하 상황 성능 한계 검증)

- **데이터 규모:** 약 **2만 건**
- **테스트 환경:** **600 VU**

| 항목            | v1 (No Cache)    | v2 (Full Cache) | v3 (Partial Cache) | 전체 평균             |
|---------------|------------------|-----------------|--------------------|-------------------|
| 평균 응답시간(avg)  | **465.13ms**     | **246.13ms**    | **463.70ms**       | **391.69ms**      |
| 중앙값(median)   | 351.23ms         | 23.77ms         | 352.44ms           | 180.45ms          |
| 95% 응답시간(p95) | 1177.38ms        | **1057.53ms**   | 1177.55ms          | **1.14s (임계 초과)** |
| 최대 응답시간(max)  | 3.67s            | 3.91s           | 3.97s              | 3.97s             |
| 에러율(errors)   | 1.29% (전체 5314건) | 0%              | 1.29%              | 1.29%             |
| 총 요청 수        | 409,267회         | 409,267회        | 409,267회           | -                 |

이 시점부터 응답 시간이 유의미하게 증가하기 시작했습니다.

부하가 600 VU로 올라가면서 **V1, V3**는 응답 지연과 함께 **에러 발생률**이 상승했습니다.

반면 **V2(전체 캐시 적용)** 는 여전히 빠르고 안정적인 응답을 유지했습니다.

이를 통해 **DB 병목보다 캐시 정책 최적화가 고부하 대응의 핵심**임을 확인했습니다.

### 4️⃣ 5차 테스트 (인덱스 미적용 상태의 한계)

- **데이터 규모:** 약 **3만 건**
- **테스트 환경:** **600 VU**

데이터가 다시 증가하면서 풀스캔 현상이 발생하기 시작했습니다.

- **V1/V3 평균 응답 시간:** **1초 이상**
- **V2 평균 응답 시간 (median):** **약 374ms**,

  **V1/V3 대비 3배 이상 빠른 응답 속도**

| 항목                 | V1              | V2          | V3           |
|--------------------|-----------------|-------------|--------------|
| **평균 응답 시간 (avg)** | **1135.7ms**    | **678.0ms** | **1138.7ms** |
| **중앙값 (median)**   | 1105.2ms        | 374.9ms     | 1116.9ms     |
| **p90 응답 시간**      | 2196.9ms        | 1943.9ms    | 2196.8ms     |
| **p95 응답 시간**      | 2354.8ms        | 2168.8ms    | 2356.8ms     |
| **최대 응답 시간**       | 6.67s           | 5.71s       | 5.56s        |
| **에러율**            | 약 29.8% (전체 평균) |             |              |
| **총 요청 수**         | 311,702회        |             |              |
| **평균 처리율 (RPS)**   | 167 req/s       |             |              |

다만 인덱스가 적용되지 않은 상태라

정렬 + 필터 조건이 복잡할 때 **쿼리 비용이 급격히 증가**하는 문제가 있었습니다.

이 시점에서 인덱스 도입 필요성을 명확히 확인했습니다.

### 5️⃣ 6차 테스트 (인덱스 도입 후 최적화 결과)

- **데이터 규모:** 약 **3만 건**
- **테스트 환경:** **600 VU (5차와 동일)**

| 구분               | 인덱스 전      | 인덱스 후            | 개선율                   |
|------------------|------------|------------------|-----------------------|
| **v1 평균 응답시간**   | 1135.36 ms | 🟢 **603.76 ms** | ⬇️ **46.8% 개선**       |
| **v2 평균 응답시간**   | 678.09 ms  | 🟢 **336.30 ms** | ⬇️ **50.4% 개선**       |
| **v3 평균 응답시간**   | 1138.22 ms | 🟢 **602.34 ms** | ⬇️ **47.1% 개선**       |
| **p95 응답시간**     | 2.30 s     | 🟢 **1.44 s**    | ⬇️ **37.4% 개선**       |
| **에러율 (errors)** | 29.83%     | 🟢 **4.00%**     | ⬇️ **86.6% 개선**       |
| **전체 요청 수**      | 381,818    | 🟢 **384,218**   | ↔️ **유사 수준 유지 (안정적)** |

인덱스 적용 이후 전반적인 성능이 **확연히 개선**되었습니다.

- **V1:** 단순 DB 조회 속도 **약 2배 향상**
- **V2:** 캐시 미적중 시에도 DB 접근 비용이 줄어들어 **응답 속도 약 50% 개선**
- **V3:** 선택적 캐시로 인해 **V1과 유사한 성능**

또한 전체 에러율은 **4% 수준으로 감소**했습니다.

특히 `status`, `category_id`, `created_at` 인덱스 덕분에

정렬 및 필터링 연산 비용이 크게 줄었으며,

캐시 미적중 상황에서도 일정한 응답 속도를 유지할 수 있었습니다.

키워드도 적용했지만 무의미한 것 같아서 추후 Elasticsearch 도입이 필요해 보입니다.

### ⚠️ V3에 대한 평가

**V3(선택적 캐시)** 는 키워드 검색 중심 시나리오에서

캐시 미스가 자주 발생해 **V1과 거의 동일한 성능**을 보였습니다.

카테고리 + 최신 정렬 조건 외에는 캐시가 비효율적으로 작동했기 때문에

**현재 구조에서는 유지보다는 개선 혹은 폐기**가 타당하다고 판단했습니다.

### 📈 향후 계획

현재 인덱스 도입으로도 충분한 개선 효과를 얻었지만

향후에는 **Elasticsearch 기반 검색 시스템**을 도입하여

상품 검색 및 정렬 속도를 **더 빠르고 확장성 있게 개선**할 예정입니다.

이를 통해 캐시 구조와 검색 시스템을 분리하고

데이터 증가에도 성능 저하 없는 **대규모 트래픽 대응 아키텍처**로 확장할 계획입니다.

</div>
</details>
<details>
<summary>리뷰 등록/신고 동시성 제어</summary>
<div markdown="1">

### 1. 리뷰 등록 중복 삽입 문제

**문제 상황**

- 동일 주문(`orderDetailId`)에 대해 여러 사용자가 동시에 리뷰 작성 시
  `Duplicate entry '...' for key 'review.UK...'` 예외 발생
- 총 10건 요청 중 1건만 정상 저장, 나머지는 DB 제약 위반으로 실패

**원인 분석**

- `Review`의 `@OneToOne` 관계에 `unique = true` 제약 존재
- 동시에 요청 시 각 트랜잭션이 “리뷰 없음”으로 판단 → 모두 INSERT 시도 → DB Unique Key가 중복 감지

**해결 방안**

- 기존 `existsByOrderDetailId()` 사전 검증 로직 유지
- **DB 제약 예외(`DataIntegrityViolationException`)를 잡아 비즈니스 예외(`REVIEW_ALREADY_EXISTS`)로 변환**
- 별도의 락 미적용
    - 리뷰 등록은 쓰기 충돌 가능성이 낮고 요청 빈도가 적은 작업
    - DB의 UNIQUE 제약이 단일 리뷰 등록을 물리적으로 보장하므로

      애플리케이션 수준 락(`@Lock`, `synchronized`)보다 간단하고 빠르게 정합성 확보 가능

    - 대신 DB 제약 위반을 감지하면 **예외를 반환해 사용자에게 중복 등록을 명확히 인지시켜 불필요한 대기 없이 즉시 피드백을 제공함**

### 2. 리뷰 신고 동시성 문제

**문제 상황**

- 여러 사용자가 동시에 동일 리뷰를 신고할 경우 `reportCount++`에서 동시성 충돌 발생
- 10명 동시 신고 시 1이어야 할 `reportCount`가 6으로 증가

**해결 방안**

- **비관적 락(`PESSIMISTIC_WRITE`) 적용**
    - 트랜잭션 시작 시점에 Review 행을 잠가 하나의 트랜잭션만 수정 가능
    - 중복신고는 비즈니스 로직 예외 반환으로 사용자에게 중복 신고 인지

```java

@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Review> findByIdAndDeletedAtIsNull(Long reviewId);
```

**기술 선택 이유**

- **낙관적 락**은 충돌 후 재시도를 필요로 하며, 신고 로직처럼 짧은 트랜잭션이 자주 동시에 발생하는 경우 비효율적
- 비관적 락은 충돌을 사전에 방지하여 정확한 카운트 보장 및 중복 신고 방지 로직 처리에 안정적

**결과**

- 10명 동시 신고 테스트 시 `reportCount`가 **정확히 1로 반영**
- 중복 신고 방지 및 데이터 무결성 확보

</div>
</details>
<details>
<summary>리뷰 필터링 조회 Index 적용을 통한 성능 개선</summary>
<div markdown="1">

**문제 상황**

- 상품당 리뷰가 **1만 건 이상**일 때,

  `rating`, `status`, `deleted_at`, `created_at` 등으로 필터링/정렬 시 **Table Scan**으로 인한 조회 지연 발생

- 기존 실행 계획

    ```
    -> Table scan on review (actual time=0.04..27.1 rows=10000)
    -> Sort: created_at DESC (actual time=33.8..33.9)
    ```

  → 인덱스 미활용으로 풀스캔 발생

**개선 조치**

- 복합 인덱스 추가 (Flyway Migration 적용)

```sql
CREATE INDEX
    idx_review_product_created_at
    ON review (product_id, status, deleted_at, created_at DESC);

CREATE INDEX
    idx_review_product_rating
    ON review (product_id, status, deleted_at, rating DESC);
```

- 주요 필터 조건(`status`, `deleted_at`)과 정렬 기준(`created_at`, `rating`)을 묶어 정렬 비용 및 풀 스캔 최소화
- Flyway 기반으로 버전 관리 및 안전한 배포 자동화 구현

**트러블슈팅**

- 초기 예상:
    - `ORDER BY created_at DESC` → `idx_review_product_created_at` 활용
    - `ORDER BY rating DESC` → `idx_review_product_rating` 활용
- 실제 결과:
    - 중간에 **`product_id`** 단일 인덱스(FK) 추가 후, 옵티마이저가 해당 인덱스를 우선 선택
    - `content LIKE '%keyword%'` 포함 시, 정렬 인덱스 무시 → 범위 조건 우선 탐색
- 따라서 인덱스 활용 계획이 일부 변경되었지만,
  복합 인덱스는 다른 조회 조건(`status`, `rating`, `deleted_at`)에서 **활용 유지**

**성능 개선 효과**

> **아래 공용 리뷰 조회 측정 결과는 `product_id` 인덱스 추가 이전 기준**
> 이후 옵티마이저 선택 변경으로 일부 쿼리 경로는 달라졌으나, 전체 조회 성능 개선 효과는 동일하게 유지됨.
>

공용 리뷰 조회(product_id 인덱스 적용 이전)

| 구분       | 평균 응답 | P95   | P99   | 결과           |
|----------|-------|-------|-------|--------------|
| **적용 전** | 634ms | 966ms | 1.51s | 전체 스캔        |
| **적용 후** | 32ms  | 47ms  | 152ms | 약 **20배 개선** |

관리자 리뷰 조회

| 구분       | 평균 응답  | P95     | P99     | 비고         |
|----------|--------|---------|---------|------------|
| **적용 전** | 67.4ms | 200.3ms | 352.4ms | Table Scan |
| **적용 후** | 19.4ms | 39.7ms  | 65.8ms  | 인덱스 완전 활용  |

- 관리자 리뷰 조회 기준 평균 응답 **3.5~20배 개선**, P95/P99 **5배 이상 단축**
- 동시 200명 부하에서도 **안정적 처리 유지**

</div>
</details>

---

## 👥팀원 소개

| 이름  | 역할  | 담당 기능                                                   | GitHub                                |
|-----|-----|---------------------------------------------------------|---------------------------------------|
| 김성민 | 팀장  | User, Auth, Address, CI/CD, 배포 자동화                      | [🔗](https://github.com/seongmin2223) |
| 김민형 | 부팀장 | Coupon                                                  | [🔗](https://github.com/MinHyeongK)   |
| 서성경 | 팀원  | Category, Product, S3 Presigned                         | [🔗](https://github.com/BibliyaSeo)   |
| 유운선 | 팀원  | Delivery, Order, Payment, Refund, Settlement, Statistic | [🔗](https://github.com/dbdnstjs)     |
| 박혜정 | 팀원  | Cart, Review                                            | [🔗](https://github.com/Hyjpark)      |