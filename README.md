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
git checkout develop         # develop 브랜치 체크아웃 (main 기준)
git push -u origin develop   # 원격 레포와 연동
```
