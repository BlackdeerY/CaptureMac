# CaptureMac
Capture Tool for Mac OS

## 단축키
* Ctrl(hold) + Command(hold): 캡처 영역 지정
  * Ctrl Up or Command Up: 캡처 
* Shift: 캡처한 이미지를 현재 위치에 고정/해제
* `: 캡처한 이미지를 숨기기/보이기
* esc: 캡처한 이미지 버리기
* Ctrl + F11: 캡처한 이미지를 ~/Downloads에 "yyyyMMdd_HH;mm;ss.png"로 저장
* Ctrl + F12: 전체 화면을 ~/Downloads에 "yyyyMMdd_HH;mm;ss.png"로 저장
* Command(hold) + MButton : 앱 메뉴 호출
  * Command Up: 선택된 앱 실행

## 주의사항
내부 라이브러리에 오류가 있어,
jar 빌드한 파일을 실행할 수 없습니다.
IDE 환경에서만 실행할 수 있습니다.
  
Apple Silicon(ARM)에서도 구동 가능한 라이브러리 버전에 오류가 있어
다운그레이드할 수 없는 상황입니다.
  
레티나 디스플레이 등 고해상도에서 캡처시 원본 해상도보다 낮게 표시되는 상황입니다.
고해상도 캡처까지는 되는데, 캡처 크기에 맞게 줄이면 다시 뭉개지는 상황이라
이미지를 작게 줄였을 때 뭉개지지 않도록 하는 방향을 찾는 중입니다.
  
저장용 캡처보다는 화면을 잠깐 띄우기 위한 용도로만 사용을 추천드립니다.
