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

### Bulletin : 게시판 작업 - 명헌
#### PlaceRequest : 명헌 / 장소 데이터. 장소 이름 저장
#### PostInterface : 명헌 / 게시판 데이터를 보내기 위한 인터페이스. 게시판 정보를 SpringBoot와 통신하기 위해 get,post 함수를 사용
#### PostRequest  : 명헌 / 게시판 데이터에 대한 엔티티 집합소.
#### PostResponse : 명헌 / 위와 같음. 현재 데이터 수정중
### Planner 
#### DayRequest 
#### Plan 
#### PlanInterface 
#### PlanPlaceRequest
#### PlanRequest
#### PlanResponse
### User - 명헌
#### LoginResponse : 명헌 / 로그인에 대한 정보
#### UserIdResponse : 명헌 / 유저 아이디 정보 
#### UserInterface : 명헌 / 로그인 정보를 보내기 위해 SpringBoot와 통신.
### ServerClient : 명헌 / 안드로이드와 SpringBoot 통신을 위해 주소 및 규약을 작성
### AuthCodeHandlerActivity : 명헌 / 카카오 로그인에 대한 클래스. 추후 수정 필요.
### BigRegion
#### 담당 : 
#### 역할 : 
### BigRegionAdapter
### CalendarDecorator
### CloudService : 명헌 / 구글 클라우드를 사용하여 사진 저장하기 위해 만든 클래스. 버킷에 대한 json, 사진 보내는 장소를 작성
### CreateSchedule
### HomeFragment : 명헌 / 게시판 보여주는 view Class. 현재 게시판 뷰, 검색, 글쓰기 페이지 및 하단 바를 보여주고 있음. 
### ImageAdapter : 명헌 / 사진 이미지 개수, 이미지 표시, 사진 주소를 표시하는 클래스. 
### KakaoApplication : 명헌 / 카카오 SDK에 대한 주소값을 저장하는 클래스 
### MainActivity
### MoreFragment
### MySchedule
### MyScheduleAdapter
### NaviActivity
### PlaceAdapter
### PostAdapter : 명헌 / 이미지, 닉네임, 해시태그, 현재 날짜, 저장 방식에 대해 적은 클래스. 현 adapter는 homefragment에서 사용해 저장된 게시판을 보여줄 수 있게함.  
### RegionSearchFragment
### SelectRegion
### SignIn : 명헌 / 구글, 카카오톡, 일반 로그인 세개의 방식은 담은 클래스. 현 구글, 카카오는 애뮬레이터 사용 불가능으로 인해 기능 테스트를 진행x, 일반 로그인은 springboot와 통신을 통해 로그인 가능
### SignUp ; 명헌 / 구글, 카카오톡, 일반 로그인 세개의 방식을 담아 회원가입 하는 클래스, 일반 로그인은 springboot와 통신하여 비밀번호는 해시값으로, csrf를 통해 통신규약 및 보안 설정
### SmallRegion
### SmallRegionAdapter
### TestAPIFragment
### WriteBulletinFragment : 명헌 / 게시판 데이터를 저장하는 클래스, 제목, 장소, 사진을 저장함. 모든 데이터는 bundle에 담아 writehasgtagFragment에 보냄. 
### WriteHashTagFragment : 명헌 / WriteBulletinFragment에서 받은 게시판 정보 및, 현 페이지에 작성된 해시태그를 담아 Postrequest에 보냄. 그 뒤, SpringBoot에 저장됨. 
### WritePlannerFragment : 
