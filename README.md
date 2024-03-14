# 게시판(bulletin Table 구축)
![image](https://github.com/rlaaudgjs2/Travel_app/assets/68803644/54895f10-c724-4833-941d-1a70eb7d2cc7)

  bulletin테이블을 다음과 같이 만듬
  - user 테이블에서 userID primary키로 변경
  -for_ID는 외래키로 설정(1. bullet Table 클릭 2. 상단에 구조 누르기 3. 릴레이션 뷰 클릭 4. 제약 사항 기본으로 놓고 컬럼명은 for_ID, 데이터베이스 user 테이블 user 컬럼명 userID로 만들기 
  ![image](https://github.com/rlaaudgjs2/Travel_app/assets/68803644/4264cb79-32bd-4e4f-aac4-34e8ecee984d)

  Kotlin 코드는 커밋한 것 확인. 
  bulletin.php파일 htdocs에 넣기( 카카오톡 방에 올림)


  
![image](https://github.com/rlaaudgjs2/Travel_app/assets/68803644/66f6f73c-8f3b-42d0-a5a3-8e2f836c1157)

   이렇게 작성하고 등록한뒤, 다시 글쓰기를 들어가면 전에 작성한 내용이 그대로 있음


 # 상세 게시판(Day Table 구축)
 ![image](https://github.com/rlaaudgjs2/Travel_app/assets/68803644/cbe954b0-93b3-4128-a6da-f4277e2b0934)
  - 테이블 이름 : day
  - 깃허브에 올린 부분(상세 게시판 데이터베이스 구축) 확인
  - 위의 사진과 같이 키 설정
  - 이미지 파일 인코딩 하여 BLOB에 저장.
  - 추후 게시판 가져올 때 디코딩해야 사진 확인 가능
  - day.php는 톡방 공유 예정  
