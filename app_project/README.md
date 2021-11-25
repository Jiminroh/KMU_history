# APP_project
대학교 2학년 2학기
모바일 프로그래밍 (팀프로젝트)

# Image
![activity_main](https://user-images.githubusercontent.com/94786383/143447639-ed78cf8d-36d8-4b1d-843f-dd976e00e346.png)

# Version 
Android Studio Version 4.0.1    
AVD :    
Name - Pixel 2 API 29    
Resolution - 1080x1920: 420dpi   
Api - 29   
Target - Android 10.0 (Google play)    
CPU/ABI - x86   

# Code
- JAVA
    - MainActivity.java (로그인 페이지)   
    - SecondActivity.java (회원가입 페이지)    
    - ThirdActivity.java (종 종류, 선택위치별 선택 페이지)   
    - FourthActivity.java (종종류별 회원목록)   
    - FifthActivity.java (지하철 선택 목록)   
    - SixthActivity.java (선택위치별 회원목록)   
    - SeventhActivity.java (친구 목록)   
    - EighthActivity.java (채팅 방)   
    - ChatData.java  (채팅에서 이름과 메세지의 getmethod(),setmethod())   
    - Information.java (firebase의 값의 getmethod(),setmethod())   
    - ListViewAdapter.java (FourthActivity,SixthActivity의 Adapter)   
    - ListViewItem.java (ListViewAdapter에서 사용할 아이템들의 getmethod(),setmethod())   
    - MessageAdapter.java (EighthActivity의 Adapter)   
    - MsgAdapter.java (SeventhActivity의 Adapter)   
    - PopupActivity_1.java (이용약관)   
    - PopupActivity_2.java (개인정보정책)   
    - SearchActivity.java (아이디 중복검사)   

- XML
    - activity_main (MainActivity의 xml)   
    - activity_second (SecondActivity의 xml)   
    - activity_third (ThirdActivity의 xml)   
    - activity_fourth (FourthActivity의 xml)   
    - activity_fifth (FifthActivity의 xml)   
    - activity_sixth (SixthActivity의 xml)   
    - activity_seventh (SeventhActivity의 xml)   
    - activity_eighth (EighthActivity의 xml)   
    - activity_popup_1 (PopupActivity_1의 xml)   
    - activity_popup_2 (PopupActivity_2의 xml)   
    - listview_item (activity_fourth lisview에 들어갈 custom xml)   
    - listview_message (activity_eighth lisview에 들어갈 custom xml)   
    - listview_msg (activity_seventh lisview에 들어갈 custom xml)   

# 주요기능

- 로그인 기능    
    1. ID 와 PW의 EditerText의 값을 받아 온 후 String 값에 저장한다.   
    2. firebase에 있는 회원정보들을 가져온 후 ID값만 추출한다.   
    3. 1번과 2번의 값들을 서로 비교한다   
    4. 둘이 일치하면 로그인 일치하지 않으면 Toast메세지 발생   

- 회원가입기능

- 선택별 회원정보 보기 기능 
    1. Custom xml(listview_item.xml)을 만든다   
    2. Adapter(ListViewAdapter)를 생성한다   
    3. (FourthActivity,SixthActivity)의 리스트와 연결   
    4. 로그인페이지(MainActivity)에서 현재 접속 중인 자신의 아이디를 intent로 받아온다   
    5. firebase에 있는 회원정보들을 가져온 후 ID값만 추출한다.   
    6. 자신의 ID값과 다른 회원정보들만 adapter에 추가한다.   
    7. adpter를 갱신한다.   

- 친구 추가 기능 
    1. 버튼을 누르면 이벤트 발생(ListViewAdapter.java)   
    2. Toast메세지 발생("친구 목록에 추가되었습니다.")   
    3. fisebase에 접근    
    4. 저장 경로이름을 ("friend")로 변경   
    5. 기존 경로이름에서 .child(내이름).child(상대이름)로 변경   
    6. 친구추가하고 싶은 상대 정보를 저장   

- 채팅기능 
    1. Custom xml(listview_message.xml)을 만든다   
    2. Adapter(MessageAdapter)를 생성한다   
    3. Adapter(MessageAdapter)에서 intent로 상대 아이디와 내 아이디를 넘겨줌   
    4. EighthActivity에서 리스트와 연결   
    5. Firebase에 child(“friend”).child(상대방 아이디)에 있는 회원정보들을 가져온다.   
    6. 회원정보의 아이디 중 자신의 아이디가 있는지 확인    
    7. 있으면 대화창(EighthActivity,java)으로 화면 전환 후 Toast메세지(상대방아이디+"님과의 채팅방입니다.") 발생    
    8. 없으면 for문 종료 후 Toast메세지(내 아이디+"님을 친구추가 하지 않으셨습니다.") 발생   
    9. String Array에 myId(내 아이디), choiceId(상대아이디)를 넣고 정열 후 rname(채팅방 이름)을 조건에 맞춰 생성 (Sorting하는 이유 -> 나와 상대가 같은 채팅방에 들어가기 위함)   
    Ex) 내 ID : id1, 상대 ID : id2, 방 이름 : id1 and id2   
        내 ID : id2, 상대 ID : id2, 방 이름 : id1 and id2   
    10. Firebase에서 회원정보를 가져온 뒤 경로를 (“message”)로 저장   
    11. EditText(보낼 메세지)를 String 값으로 받아옴    
    12. 채팅 보내기 버튼을 누르면 이벤트 발생   
    13. 조건(보낼 text != “”)이 아니면 기존경로.child(채팅방 이름)에 message저장   
    14. child(채팅방 이름)에 message 추가시 이벤트 발생   
    15. chatData()의 getmethod()를 이용하여 Adapter에 추가   
    16. Adapter 갱신   