<img width="241" alt="스크린샷 2024-03-12 오전 11 17 13" src="https://github.com/rlaaudgjs2/Travel_app/assets/81517768/8c5b1afa-3874-4d99-b145-9c287a8d649b">
 
  fragment_write_bulletin.xml 화면

 - item0, item1, item2 쭉 있는 부분이 recyclerview 부분

<img width="252" alt="스크린샷 2024-03-12 오전 11 17 57" src="https://github.com/rlaaudgjs2/Travel_app/assets/81517768/ffe3ad67-40b4-4baa-b908-ce388c06c94e">
  
  WriteBulletinFragment 어플 구동 화면

 - one, two라는 요소 두 개를 추가한 모습. 만약 장소 추가 눌러서 three를 작성하면 스크롤을 통해 저기서 확인 가능.

<img width="1019" alt="스크린샷 2024-03-12 오전 11 20 28" src="https://github.com/rlaaudgjs2/Travel_app/assets/81517768/9e294390-df46-4626-b59e-4bfec19e35f1">

  WriteBulletinFragment 146번째 줄부터 있는 PlaceAdapter 코드

 - 현재 title, imgUri, content의 내용이 adapter를 통해 recyclerview에 표시되고 있음

<img width="699" alt="스크린샷 2024-03-12 오전 11 21 39" src="https://github.com/rlaaudgjs2/Travel_app/assets/81517768/6d2630d3-2ef6-46d9-834c-03f20dbfc31f">
<img width="1600" alt="스크린샷 2024-03-12 오전 11 22 53" src="https://github.com/rlaaudgjs2/Travel_app/assets/81517768/c061ddb9-c0eb-43e7-9317-d3ce566b88dc">

  WriteBulletinFragment 65번째 줄부터 있는, viewmodel에 담긴 내용을 placesList에 담아 recyclerview adapter를 업데이트 해주는 코드

 - 이 전까진 이해를 돕기위한 코드. 실질적으로 데이터를 db에 저장할 때 이부분을 잘 봐야 함
 - one, two까지 장소 추가를 하고 난 로그를 보면 recyclerview 하나의 요소 당 title, imgUri, content가 저장된 것을 볼 수 있음
 - 저 placesList를 가지고 db에 삽입하면 될듯
 - WriteBulletinFragment 에 binding.btnRegisterBulletin.setOnClickListener 추가해야함. 이게 오른쪽 위 등록 버튼
 - 저 버튼 눌렀을 때 db에 저장하는 코드 넣으면 될듯.
 - 아직 해시태그는 기능 추가중




hashtag 작성 fragment 추가
1. WriteBulletinFragment의 btnRegisterBulletin(게시글 등록) 버튼을 누르면 hashtag 작성 fragment로 이동.
2. WriteHashTagFragment에서 hashtagList에 사용자가 작성한 해시태그들이 담기게 됨
3. binding.btnRegisterHashtag.setOnClickListener에 있는 Log를 통해 해시태그 목록을 확인할 수 있음


------------------------------------------------------------------------------------------------------
0314 버그 수정


<img width="435" alt="스크린샷 2024-03-14 오후 5 27 38" src="https://github.com/rlaaudgjs2/Travel_app/assets/81517768/2cfbe50a-583b-4d73-a78d-bd6b73ae7b9c">

1. PlaceVieModel.kt에 resetPlaceData() 함수 추가


<img width="551" alt="스크린샷 2024-03-14 오후 5 29 37" src="https://github.com/rlaaudgjs2/Travel_app/assets/81517768/c15644c4-5066-4792-aea3-4770b2496c14">
<img width="390" alt="스크린샷 2024-03-14 오후 5 28 38" src="https://github.com/rlaaudgjs2/Travel_app/assets/81517768/0fcb8a5c-3f15-43f1-b94b-57af865f8e5c">

2. HomeFragment에 onResume() 오버라이드 하고 안에 placeViewModel.resetPlaceData() 추가

