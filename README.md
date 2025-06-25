# 🏠 ZARO

> 자취 생활의 빈틈을 채우고, 일상의 즐거움과 유용한 나눔을 만드는 **자취생 커뮤니티 플랫폼**

ZARO는 1인 가구를 위한 지역 기반 커뮤니티 서비스입니다.  
자취생들이 함께 정보를 나누고, 유용한 글을 작성하며, 동네를 더 잘 알아갈 수 있도록 돕습니다.

---

## 🕒 프로젝트 정보

- **프로젝트명:** ZARO
- **개발 기간:** 2025년 5월 4일 ~ 2025년 6월 19일 (총 6주)
- **참여 인원:** 프론트엔드 2명 / 백엔드 4명
- **주요 타겟:** 자취하는 1인 가구
- **기획 목표:** 정보 공유 + 커뮤니티 + 지역 탐색을 모두 아우르는 자취생 맞춤형 커뮤니티

---

## 🔧 기술 스택

| 분류             | 스택                                              |
|------------------|---------------------------------------------------|
| Build Tool       | Gradle                                            |
| Language         | Java                                              |
| Framework        | Spring Boot, Spring Data JPA                      |
| Database         | MySQL (Amazon RDS)                                |
| ORM / Query      | JPA, QueryDSL                                     |
| Auth             | Spring Security, JWT                              |
| Search Engine    | Elasticsearch                                     |
| Cache            | Redis                                             |
| Storage          | AWS S3                                            |
| Real-time        | Server-Sent Events (SSE)                          |
| Infrastructure   | Docker, AWS EC2                                   |
| CI/CD            | Jenkins                                           |

> 🔍 주요 라이브러리: Lombok, Validation, MapStruct, Swagger-ui 등  

## ✨ 주요 기능

### 🔐 사용자 인증 및 관리
- JWT 기반 로그인, 로그아웃(블랙리스트 활용), 리프레시 토큰 재발급
- 카카오 OAuth2 로그인/회원가입
- Custom Validator를 이용한 회원가입 유효성 검증
- 휴면 계정 자동 전환 및 탈퇴 처리 (HTML 이메일 발송 및 스케줄링)

### 📌 커뮤니티 기능
- 게시글/댓글 CRUD
- 게시글 태그 및 카테고리 필터링 검색
- 게시글 좋아요 및 신고 기능
- Elasticsearch 기반 게시글 검색
- 실시간 인기 게시글 랭킹 및 변화량(RankChange) 계산 제공

### 🗺️ 지도 기반 즐겨찾기 장소 관리
- Kakao 지도 API를 통한 장소 검색 및 위치 저장
- 사용자 즐겨찾기 그룹/장소 CRUD
- 위치 기반 사용자 즐겨찾기 장소 추천 기능

### 🔔 실시간 알림 (SSE)
- Spring Event 기반 알림 생성
- SSE(Server-Sent Events) 실시간 전송 구조
- JWT 인증 기반 SSE 구독 + 커스텀 핸드셰이크
- 안정적인 연결 유지 (ping 처리) 및 자동 삭제 스케줄러

### 🧑‍💻 프로필 및 팔로우
- 프로필 CRUD
- 유저 활동(게시글, 댓글, 좋아요) 조회
- 팔로우/팔로잉 기능 및 목록 조회

### 🛠️ 인프라 및 배포
- Docker 기반 개발/운영 환경 구성
- Jenkins를 활용한 CI/CD 파이프라인 구축
- AWS EC2, RDS, S3, CloudFront 인프라 연동 및 무중단 배포

### 💻 프론트엔드 협업 및 지원
- FE 연동을 위한 API 지원 및 디버깅
- 공통 컴포넌트 구현 (푸터, 배너, 약관 페이지 등)
- 상태 관리(Zustand), 실시간 알림 SSE 연동 및 토큰 자동 갱신 처리

---

## 🧑‍🏭 팀원 소개 및 담당 역할

<table align="center">
  <thead>
    <tr>
      <th width="180px">BE / PM</th>
      <th width="180px">BE / BE Leader</th>
      <th width="180px">BE</th>
      <th width="180px">BE</th>
      <th width="180px">FE / FE Leader</th>
      <th width="180px">FE</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td align="center">
        <img src="https://github.com/user-attachments/assets/81b95839-c831-4ccd-bb98-dc1e2802ae5e" width="130"/>
      </td>
      <td align="center">
        <img src="https://github.com/user-attachments/assets/73cff447-cee8-4b26-8fbf-480980b52264" width="130"/>
      </td>
      <td align="center">
        <img src="https://github.com/user-attachments/assets/2bf87d62-123f-4348-968e-3c60b3b6a39a" width="130"/>
      </td>
      <td align="center">
        <img src="https://github.com/user-attachments/assets/2f8f6824-fc6b-4035-8308-14be350141d1" width="130"/>
      </td>
      <td align="center">
        <img src="https://github.com/user-attachments/assets/59162ca7-2717-4429-adb9-d4439c8868e6" width="130"/>
      </td>
      <td align="center">
        <img src="https://github.com/user-attachments/assets/67e81ea4-6de2-4b01-9bab-f079d33a93e6" width="130"/>
      </td>
    </tr>
    <tr>
      <td align="center">
        <a href="https://github.com/nahyukk" target="_blank">김나현</a>
      </td>
      <td align="center">
        <a href="https://github.com/Saaad9" target="_blank">심동훈</a>
      </td>
      <td align="center">
        <a href="https://github.com/yoorym-kim" target="_blank">김유림</a>
      </td>
      <td align="center">
        <a href="https://github.com/LJIEUN" target="_blank">이지은</a>
      </td>
      <td align="center">
        <a href="https://github.com/mini0212" target="_blank">김성민</a>
      </td>
      <td align="center">
        <a href="https://github.com/JinWorkBench" target="_blank">진상휘</a>
      </td>
    </tr>
    <tr>
      <td align="center">
        회원API <br/> 
        댓글, 검색 API <br/>
        서버, CI/CD <br/>
        [FE] <br/>
        푸터/배너 <br/>
        로그아웃/회원탈퇴 <br/>
      </td>
      <td align="center">
       지도 API <br/>
        즐겨찾기 API <br/>
        그룹 API <br/>
        [FE] <br/>
        동네탐방(지도) <br/>
        프로필 <br/>
      </td>
      <td align="center">
        프로필 API <br/>
        팔로우 API <br/>
        알림 API <br/>
        [FE] <br/>
        알림 컴포넌트 <br/>
        SSE연결 <br/>
      </td>
      <td align="center">
       게시판 API <br/>
        실시간 랭킹 API <br/>
        신고 API <br/>
        검색 API <br/>
        [FE] <br/>
        실시간 랭킹 컴포넌트 <br/>
      </td>
      <td align="center">
      [FE] <br/>
        회원가입/로그인 <br/>
        헤더, 사이드바 <br/>
        홈, 게시판 <br/>
        검색, 댓글 <br/>
        프로필, 피드, 내정보 <br/>
      </td>
      <td align="center">
       [FE] <br/>
        글쓰기 에디터 <br/>
        게시판 상세 <br/>
        지도 - 즐겨찾기 <br/>
        좋아요/팔로우 <br/>
        프로필 <br/>
      </td>
    </tr>
  </tbody>
</table>


## 👥 팀원별 역할 (상세)

### 👩‍💻 나현 (BE / 팀 리드)

#### 🔧 전역 처리 및 공통 설정
- 전역 예외 처리 및 에러 메시지 응답 통일화

#### 🔐 인증/인가 및 사용자 관리
- 회원가입 API 구현 및 Custom Validator 기반 유효성 검증 로직 설계  
- JWT 기반 로그인 + Redis 블랙리스트 활용 로그아웃 처리  
- Refresh Token 기반 로그인 유지 (Redis 저장)  
- 카카오 OAuth2 로그인 및 회원가입  
- 휴면 전환 및 탈퇴 자동 처리 (HTML 메일 발송 + 스케줄링)  
- 계정 정보 수정 API 구현

#### 💬 커뮤니티 기능
- 댓글 CRUD API  
- Elasticsearch 기반 게시글 검색 API  
- S3 Presigned URL 발급 API

#### 🧱 인프라 및 배포 환경 구성
- Dockerfile 및 docker-compose 설정  
- AWS EC2, RDS, S3, CloudFront 구성  
- Jenkins 기반 CI/CD 파이프라인 및 무중단 배포 환경 구축

#### 💻 프론트엔드 협업 지원
- 로그아웃 및 회원탈퇴 UI 연동  
- 약관/정책 페이지 구현  
- 공통 컴포넌트(푸터, 배너 등) 구현

---

### 👨‍💻 동훈 (BE 리드)

#### 🔧 프로젝트 초기 세팅
- Spring Boot 기반 구조 설계 및 환경 설정  
- CORS 포함 프론트엔드 연동 환경 구성  
- application-dev / application-prod 설정  
- GitHub 협업용 PR 규칙 및 가이드 문서 작성

#### ⚠ 전역 예외 처리
- @RestControllerAdvice 기반 예외 응답 포맷 표준화  
- 커스텀 예외 정의 및 상태 코드 일관화

#### 📌 즐겨찾기 및 커뮤니티 기능
- 즐겨찾기 및 그룹 CRUD API  
- 카카오 지도 API 연동 장소 검색 및 위치 저장  
- 즐겨찾기 기반 추천 장소 조회 기능  
- 외부 API → DB 가공 저장

#### 💻 프론트엔드 협업 지원
- 지도 페이지 FE 담당 및 API 연동  
- Zustand 기반 상태 관리 및 UI 리팩토링  
- FE-BE 연동 오류 디버깅 및 인터페이스 보완

---

### 👩‍💻 유림 (BE)

#### 🔔 알림 기능 (SSE 기반)
- 알림 목록/단일/전체 읽기 API  
- Spring Event 기반 알림 생성 구조  
- 순환 참조 방지를 위한 구조 리팩토링  
- JWT 인증 기반 SSE 구독 및 핸드셰이크 처리  
- ping 처리 및 알림 자동 삭제 스케줄러

#### 👤 프로필 및 활동 조회
- 프로필 CRUD API  
- 게시글/댓글/좋아요 조회 API  
- 팔로우/팔로잉 기능 및 리스트 API

#### 💻 프론트엔드 협업 지원
- SSE 연결 구조 (`event-source-polyfill`) 작성  
- Zustand 기반 알림 상태 및 `useSse` 훅 구현  
- 실시간 알림 UI 연동  
- 로그인 후 자동 SSE 연결 로직 구현  
- Vercel ↔ EC2 인증 이슈 해결

---

### 👩‍💻 지은 (BE)

#### 📌 커뮤니티 게시판 기능
- 게시글 CRUD API  
- 태그 필수 및 이미지 조건 설정  
- 필터링 기반 목록 조회  
- 좋아요 및 권한 분기 처리

#### 🔥 인기 게시글 기능
- 실시간 랭킹 비교 및 RankChange 계산 로직 설계  
- 인기글 UI에 변화량 반영 가능하도록 설계

#### 🚨 게시글 신고 기능
- 중복 신고 방지 및 본인 글 신고 예외 처리  
- 신고 누적 시 자동 상태 전환 처리

#### 💻 프론트엔드 협업 지원
- 인기 게시글 UI 및 API 연동  
- 동적 데이터 흐름에 맞춘 데이터 조율

---

### 👩‍💻 성민 (FE 리드)

#### ⚙ 프론트엔드 구조 설계
- 프로젝트 구조 및 라우팅 전략 설계  
- SSR/CSR 혼합 구성 및 SEO 최적화  
- 카카오 소셜 로그인 연동  
- 커뮤니티 UI 및 댓글/신고 기능 구현  
- 프로필, 팔로우, 검색 UI 구현  
- 공통 컴포넌트 및 hook 구조 설계  
- CSP 헤더 설정(XSS 방지 및 외부 스크립트 제어)

---

### 👨‍💻 상휘 (FE)

#### 📝 글쓰기/상세 페이지
- Toast UI 기반 에디터 구현 (이미지 업로드 포함)  
- 게시글 상세 페이지 및 메타 정보 출력  
- 공유 기능 구현

#### ⭐ 즐겨찾기 및 장소 기능
- 즐겨찾기 CRUD 및 UX 최적화  
- 지도 기반 장소 탐색 기능 개발

---

## 🖼 스크린 샷
|로그인|회원가입|
|--|--|
|<img width="1397" alt="image" src="https://github.com/user-attachments/assets/dd5d24be-a74d-454c-9afa-61e105927412" />|<img width="1397" alt="image" src="https://github.com/user-attachments/assets/52142891-06a7-481f-a179-edec5a14b883" />|
|홈|게시글 리스트|
|<img width="1397" alt="image" src="https://github.com/user-attachments/assets/79318e3e-beae-43cb-86df-8d88c5b68472" />|<img width="1397" alt="image" src="https://github.com/user-attachments/assets/a3e2b7b7-bd9e-4adc-b949-a1a4eddc63e8" />|
|게시글 상세|글쓰기|
|<img width="1397" alt="image" src="https://github.com/user-attachments/assets/ef2877c8-338a-4fe1-9131-0c315015bae1" />|<img width="1397" alt="image" src="https://github.com/user-attachments/assets/ee724f6d-cd4c-491c-9833-adeacc717901" />|
|지도|프로필|
|<img width="1397" alt="image" src="https://github.com/user-attachments/assets/61ab351d-85f5-482e-beda-d6e4e195dca0" />|<img width="1397" alt="image" src="https://github.com/user-attachments/assets/1e0667c9-33ad-4a7c-bf2e-da2b727e309c" />|
|프로필 수정|알림(SSE)|
|<img width="1397" alt="image" src="https://github.com/user-attachments/assets/2140b3b1-f91a-4c7e-b5ae-fb7d7da18607" />|<img width="403" alt="image" src="https://github.com/user-attachments/assets/33dd9e2a-97c4-410f-a4ad-fd8a26150420" />|



---

## 🎥 시연 영상

[👉 ZARO 프로젝트 시연 영상 보러가기](https://www.youtube.com/watch?v=j5y-rUP4AF4)
