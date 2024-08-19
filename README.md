# 기능적 요구사항 명세서 

## 사용자 

 - 여행을 계획하고 있는 사람
 - 무계획자

## 지역 카테고리

- 사용자는 지역 카테고리를 통해 도/시/구/군 등의 단위로 지역을 선택할 수 있다.

## 플래너 

 - 사용자는 정해져 있는 표에서 계획표를 작성 할 수 있다.
 - 사용자는 작성한 계획표를 어플리케이션 사용자 끼리 공유 할 수 있다. 
 - 사용자는 플래너에 시간, 요일을 등록하면 가게 오픈 여부를 확인할 수 있다.

## 글쓰기

- 사용자는 제목, 본문 입력란을 통해 여행 사진 또는 글을 작성할 수 있다.
- 글쓴이가 작성한 여행지 루트를 여행 플래너에 불러올 수 있다.
- 사용자는 게시글 작성 시 작성 내용과 관련된 해시태그를 작성할 수 있다.
- 사용자는 댓글을 작성할 수 있다. / 댓글을 신고할 수 있다.
- 사용자는 게시글을 추천하거나 비추천할 수 있다.


## 해시태그 검색기능

- 여행 지역을 선택하지 않고 여행의 요소들(ex. 술/힐링/액티비티/오션뷰 등)로 여행 경험을 찾고 싶은 사용자는 검색창을 통해 여행 요소를 검색할 수 있다.


## 언어
- spring boot
- kotlin

## 담당 분야
- 김명헌 : 로그인 구현
- 김규원 : ui 구현 
- 홍성민 : ui 구현  

## 회의 
01.30 : 데이터베이스 설계 및 ui 정리 
02.07 : 구글 로그인 제외 로그인 구현, 캘린더, 부분 ui 구성

##진행상황 
- 로그인
- 게시판
- 지도 불러오기
- 해시태그
- 플래너 ui
- 검색 - 지역

##해야할것
- 데이터베이스 구조 수정
- 마무리 작업 때 Security사용 로그인 보안 강화
- 가장먼저 해야할 것 (백엔드 스프링부트 작업 다 끝내기)
  (데이터베이스 수정 고려해서 h2데이터베이스 사용) -명헌 
- 카테고리별 주변 장소 추천
- 사용자 장소 수정 요청.
- 관리자 페이지.
- 장소, 카테고리, 지역이름, 좌표
- 구글 맵 거리 계산
- 스프링부트 전체 작업 
- 플래너 가게 영업시간 데이터(구글 플레이스 api)
- 일정 추가, 찜해온 데이터 가져오기.
- 인원 설정 페이지
- 장소 설정 페이지
- 일정 알림 기능

  Android Studio
  
   Okhttp
  장점 : 유연하고 많은기능을 사용할 수 있다. 
  단점 : 어렵고 복잡하다.
  Retrofit
  장점 : 직관적이고 간결하다.
  단점 : 유연성이 떨어진다.



## 클래스 정리

### Bulletin 
#### -> PlaceRequest, PostInterface, PostRequest, PostResponse
### Planner 
#### -> DayRequest, Plan, PlanInterface, PlanPlaceRequest, PlanRequest, PlanResponse
### User 
#### -> LoginResponse, UserIdResponse, UserInterface
### ServerClient
### AuthCodeHandlerActivity
### BigRegion
#### 담당 : 
#### 역할 : 
### BigRegionAdapter
### CalendarDecorator
### CloudService
### CreateSchedule
### HomeFragment
### ImageAdapter
### KakaoApplication
### MainActivity
### MoreFragment
### MySchedule
### MyScheduleAdapter
### NaviActivity
### PlaceAdapter
### PostAdapter
### RegionSearchFragment
### SelectRegion
### SignIn
### SignUp
### SmallRegion
### SmallRegionAdapter
### TestAPIFragment
### WriteBulletinFragment
### WriteHashTagFragment
### WritePlannerFragment
