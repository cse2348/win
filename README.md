발달장애인, 어린이, 고령자 등 누구나 이해할 수 있도록, 뉴스를 쉬운 단어와 문장으로 요약하여 제공하는 접근성 중심 서비스입니다.  

---

## 주요 기능 요약

- Excel 파일로 뉴스 데이터 등록 (정치/경제/사회/문화 카테고리)
- OpenAI API를 통한 쉬운 말 요약 및 키워드 추출
- 최신 뉴스 5건 조회 API (`/api/news/latest`)
- Docker + GitHub Actions + EC2 기반 자동 배포

---

## 백엔드 기술 스택

| 계층 | 기술 |
|------|------|
| Backend | Spring Boot 3, Java 17 |
| DB | MySQL |
| Cache | Redis |
| 배포 | AWS EC2, Docker, ECR, GitHub Actions |
| AI 요약 | OpenAI GPT API |
| 데이터 처리 | Apache POI (Excel) |

---

## 프로젝트 구조
backend/
├── controller/ # REST API 컨트롤러
├── dto/ # DTO 클래스
├── entity/ # JPA 엔티티
├── repository/ # JPA 리포지토리
├── service/ # 비즈니스 로직
├── config/ # 설정 파일
└── resources/
└── application.yml


## API 목록
| Method | Endpoint | 설명 |
|------|------|------|
GET	| /api/news/latest	| 최신 뉴스 5건 요약 조회

## 팀정보
| 역할       | 담당자   |
| -------- | ----- |
| 백엔드 개발   | 김서연,배서연 |
| 프론트엔드 개발 | 이혜림  |
| 기획 및 운영  | 이가영,전예담 |
| 디자인  | 박지수 |

