# OrderFlow
# 📌 Git Flow 팀용 요약 카드

## 1️⃣ 초기 설정 (최초 1회만!)
```bash
git clone https://github.com/JuniorNaver/OrderFlow.git    # 레포 클론
cd OrderFlow                 # 클론한 폴더로 이동
git checkout -b develop      # develop 브랜치 생성 (main 기준)
git push -u origin develop   # 원격에 올리기
```
## 2️⃣ 작업 시작 (매 작업 시작 때마다 / 새 기능 브랜치 생성)
```bash
git checkout develop           # develop 이동
git pull origin develop        # 최신 코드 가져오기
git checkout -b feature/BI     # 새 기능 브랜치 생성 (예: BI)
```
기능별 브랜치 이름
feature/PR → 발주요청

feature/PO → 발주

feature/GR → 입고

feature/STK → 재고

feature/SD → 판매

feature/BI → BI 분석

## 3️⃣ 작업 중 (커밋 & 푸시)
```bash
코드 복사
git status                     # 변경 사항 확인
git add .                       # 변경 파일 스테이징
git commit -m "Add BI page"     # 커밋
git push origin feature/BI      # 원격 저장소에 푸시
```
GitHub에서 PR 생성 → 리뷰 요청 → develop 병합

## 4️⃣ 작업 끝난 후 (브랜치 정리)
```bash
코드 복사
git checkout develop                     # develop 이동
git pull origin develop                  # 최신 코드 가져오기
git branch -d feature/BI                 # 로컬 feature 브랜치 삭제
git push origin --delete feature/BI      # 원격 feature 브랜치 삭제
```
다음 작업 때는 같은 이름이어도 develop에서 새 브랜치 생성

## 5️⃣ 브랜치 생명주기 그림
css
코드 복사
develop ──────────────┐
   │                  │
   │   feature/BI     │
   └─ merge → develop │
                       \
                        main (배포)
기능 개발 중 → feature 브랜치 유지

기능 완료 → develop 병합 → 브랜치 삭제

새 기능/개선 → develop에서 새 feature 브랜치 생성

## 🔑 핵심 규칙
main은 절대 직접 수정 금지

모든 개발은 feature/* 브랜치에서 → PR → develop

feature 브랜치 작업 끝나면 삭제

필요 시 같은 이름의 feature 브랜치를 새로 생성 가능
