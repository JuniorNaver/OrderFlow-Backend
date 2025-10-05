## 🚀 OrderFlow-Backend Git Flow 팀 가이드

## 🔑 핵심 규칙
main은 배포 전용 → 직접 수정 ❌

모든 개발은 feature/* 브랜치에서 시작

기능 작업 완료 후 → PR → develop 병합

병합 완료된 feature/* 브랜치는 삭제

## 1️⃣ 작업 시작 전 (매 작업 시작 때마다 / 새 기능 브랜치 생성)
```bash
git checkout develop           # develop 이동
git pull origin develop        # 최신 코드 가져오기
git checkout -b feature/BI     # 새 기능 브랜치 생성 (예: BI)
```
✅ 브랜치 네이밍 규칙

feature/PR → 발주요청

feature/PO → 발주

feature/GR → 입고

feature/STK → 재고

feature/SD → 판매

feature/BI → BI 분석

## 2️⃣‼ 여기서부터 로컬(개인 컴퓨터)에서 작업 시작

## 3️⃣ 작업 후 (커밋 & 푸시)
```bash
git status                      # 변경 사항 확인
git add .                       # 변경 파일 스테이징
git status                      # 변경 사항 확인
git commit -m "Add BI page"     # 커밋
git push origin feature/BI      # 원격 저장소에 푸시
```

## 4️⃣‼ GitHub에서 PR 생성 → 리뷰 요청 → develop 병합

## 5️⃣ PR -> 병합 끝난 후 (‼ 병합 전에 하지 마세요 / 브랜치 정리)
```bash
git checkout develop                     # develop 이동
git pull origin develop                  # 최신 코드 가져오기
git branch -d feature/BI                 # 로컬 feature 브랜치 삭제
git push origin --delete feature/BI      # 원격 feature 브랜치 삭제
```
👉 다음 작업 때는 같은 이름이어도 develop에서 다시 새로 브랜치 생성

## 📌 브랜치 생명주기
```css
develop ──────────────┐
   │                  │
   │   feature/BI     │
   └─ merge → develop │
                       \
                        main (배포)
```
기능 개발 중 → feature 브랜치 유지

기능 완료 → develop 병합 → 브랜치 삭제

새 기능/개선 → develop에서 새 feature 브랜치 생성

## 🛠 초기 설정 (최초 1회만)
```bash
git clone https://github.com/JuniorNaver/OrderFlow-Backend.git    # 레포 클론
cd OrderFlow-Backend              # 클론한 폴더로 이동
git checkout develop              # develop 브랜치 체크아웃
git push -u origin develop        # 원격 레포와 연동
```

## ⚙️ 초기 환경 세팅 (Oracle Cloud 연동 / 모든 팀원 공통)

> ⚠️ 이 단계는 GitHub에 없는 **보안 파일(wallet, .env)** 을 세팅하기 위한 절차입니다.

### 1️⃣ 노션에서 필수 파일 다운로드

📎 **[OrderFlow 환경 설정 자료실 (Notion)]**

- `wallet.zip` (Oracle Cloud 인증 폴더)
- `.env` (운영 DB 접속 정보)

---

### 2️⃣ wallet 설정

1. `wallet.zip` 파일을 다운로드 후 압축 해제  
2. 프로젝트 내부에 아래 구조로 배치
```bash
src/main/resources/wallet/
```
안에 다음 파일들이 있어야 합니다 👇
```yaml
cwallet.sso
ewallet.p12
keystore.jks
sqlnet.ora
tnsnames.ora
truststore.jks
ojdbc.properties
```

---

### 3️⃣ .env 설정

1. `.env` 파일을 프로젝트 **루트 폴더**(= `build.gradle`이 있는 위치)에 둡니다.
2. 내용 예시:
```yaml
DB_URL=jdbc:oracle:thin:@orderflow_high?TNS_ADMIN=./src/main/resources/wallet
DB_USERNAME=orderflowadmin
DB_PASSWORD=OrderFlow1234
```

> ⚠️ `.env`는 **절대 깃허브에 업로드하지 마세요.**
> (`.gitignore`에 이미 등록되어 있습니다.)

---

### 4️⃣ 실행 확인

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```
Spring Boot가 .env의 환경변수를 읽고 Oracle Cloud DB(orderflow_high)에 연결됩니다.

### ✅ 폴더 구조 예시
```bash
OrderFlow-Backend/
 ├── build.gradle
 ├── .env                      # 환경변수 파일 (Git에 올리지 않음)
 ├── .gitignore
 └── src/
     └── main/
         └── resources/
             ├── application.yml
             ├── application-dev.yml
             ├── application-prod.yml
             └── wallet/         # Oracle 인증 폴더 (Git에 올리지 않음)
```
### 🧱 기본 규칙
| 항목 | 설명 |
|------|------|
| `.env` | DB 접속 정보 포함 → 업로드 금지 |
| `wallet/` | Oracle 인증서 포함 → 업로드 금지 |
| `application-prod.yml` | 환경변수 참조만 (`${DB_URL}` 등) 사용 |
| `application-dev.yml` | 로컬 DB(javauser/java1234) 테스트용 가능 |

### 💡 오류 발생 시
| 증상 | 원인 | 해결 방법 |
|------|------|-----------|
| `ORA-29024: Certificate validation failure` | wallet 경로 잘못됨 | `.env`의 `TNS_ADMIN` 경로 확인 |
| `Invalid username/password` | DB 계정 틀림 | `.env` 값 재확인 |
| `Listener refused connection` | TNS 이름 불일치 | wallet 내부 `tnsnames.ora` 확인 |
| `Could not resolve placeholder` | .env 미설정 | `.env`를 루트 폴더에 추가 |
