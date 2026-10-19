# Bookit

Spring Boot 기반 예약 서비스입니다. 단순 예약 CRUD를 넘어서, 마지막 한 자리 동시 예약 상황에서 발생하는 초과 예약 문제를 재현하고 해결 전략을 비교하는 것을 목표로 합니다.

## 주요 기능

- JWT 기반 회원가입, 로그인, 내 정보 조회
- 서비스와 예약 가능 시간(슬롯) 관리
- 슬롯별 전체 정원·예약 완료 수·남은 좌석·예약 가능 여부 조회
- 예약 생성, 조회, 취소
- 예약 생성/취소 이력 저장
- 예약 24시간 전 알림 예약 및 스케줄러 처리
- 비관적 락, 낙관적 락, Redis 분산 락 기반 예약 API
- Docker Compose로 Spring Boot, MySQL, Redis 실행

## 동시성 제어

예약 가능 인원이 1명인 슬롯에 두 요청이 동시에 도착하는 상황을 다룹니다.

| 방식 | 예약 API | 특징 |
| --- | --- | --- |
| 비관적 락 | `POST /api/reservations` | DB 행을 먼저 잠가 충돌을 직렬화 |
| 낙관적 락 | `POST /api/reservations/optimistic` | 버전 충돌 시 재시도 |
| Redis 분산 락 | `POST /api/reservations/redis` | 여러 애플리케이션 인스턴스에서도 같은 슬롯을 제어 |

`ReservationConcurrencyTest`와 `OptimisticReservationConcurrencyTest`는 두 명의 사용자가 마지막 한 자리를 동시에 예약하는 상황에서 성공 예약이 정확히 한 건인지 검증합니다.

## 로컬 실행

MySQL을 실행한 뒤 Eclipse에서 `BookitApplication`을 **Run As → Java Application**으로 실행합니다.

```text
http://localhost:8080/api/health
```

## API 문서

서버 실행 후 Swagger UI에서 API 목록, 요청 형식, 응답을 확인하고 직접 호출할 수 있습니다.

```text
http://localhost:8080/swagger-ui.html
```

JWT가 필요한 API는 Swagger UI 우측 상단의 `Authorize`에서 로그인 응답의 access token을 입력합니다.

## Docker 실행

```powershell
docker compose --profile full up -d --build
docker compose --profile full ps
```

중지할 때는 데이터를 유지하는 다음 명령을 사용합니다.

```powershell
docker compose --profile full down
```

Docker의 MySQL은 PC에 직접 설치한 MySQL과 별도의 데이터베이스이므로, Docker 환경에서는 회원가입부터 다시 진행해야 합니다.

## CI

GitHub의 `main` 또는 `master` 브랜치에 push하거나 Pull Request를 만들면 GitHub Actions가 Java 21 환경에서 `./mvnw test`를 자동 실행합니다.

## 예약 알림

예약이 생성되면 예약 시간 24시간 전을 발송 시각으로 하는 알림이 `reservation_notification` 테이블에 `SCHEDULED` 상태로 저장됩니다. 스케줄러는 1분마다 발송 시각이 지난 알림을 처리하고 `SENT`로 바꿉니다. 현재는 서버 로그 기록 방식이며, 이후 이메일·SMS·푸시 서비스 연동 지점으로 확장할 수 있습니다.

```text
GET /api/reservations/me/notifications
```

## 예약 가능 인원 조회

서비스의 슬롯 목록을 조회하면 취소된 예약을 제외한 예약 완료 수와 남은 좌석을 함께 반환합니다.

```text
GET /api/services/{serviceId}/slots
```

```json
{
  "capacity": 1,
  "reservedCount": 0,
  "remainingCapacity": 1,
  "available": true
}
```
