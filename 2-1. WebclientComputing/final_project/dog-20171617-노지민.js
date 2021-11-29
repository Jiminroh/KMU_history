var reset = false; // 게임 실핼여부
var t = 0;        // 강아지를 찾는 시간
var n = 0;        // 24개의 달걀중 강아지가 숨은 달걀 수 
var f = 0;  	  // 실패한 횟수	 

var random_num = new Array();	// 랜덤하게 n개의 수를 받을 Array 

// 각각의  오디오 객체 생성
var start_audio = new Audio();
start_audio.src = "media/bi.mp3";
var time_audio = new Audio();
time_audio.src = "media/clock.mp3";
var end_audio = new Audio();
end_audio.src = "media/ending.mp3";
var click_audio = new Audio();


function main (){
	var time = 10;			// 미리보기 10초

	Choice_Dog();			// 24개의 달걀 중 강아지가 들어갈 달걀 고르는 함수
	Show_Dog();				// 고른 달걀 보여주는 함수

	Run(time);				// 게임 시작 함수

}
function Reset(){
	n = parseInt(prompt("찾을 강아지의 수를 입력하세요(8마리 미만)")); // n값을 다시 설정
	if(n >= 8){
		n = parseInt(prompt("너무 많은 강아지의 수를 입력했습니다. 다시 입력하세요")); // 8초과값이 입력되면 예외처리 
	}	
	else if(isNaN(n)== true){
		n = parseInt(prompt("숫자를 입력하세요"));	// 숫자가 아니면 예외처리
	}

	t = prompt("수행가능한 시간을 입력하세요(30초 미만)");	// t값을 다시 설정
	if(t >= 30){
		t = parseInt(prompt("너무 많은 시간을 입력했습니다. 다시 입력하세요")); // 30초과값이 입력되면 예외처리 
	}	
	else if(isNaN(t)== true){
		t = parseInt(prompt("숫자를 입력하세요"));	// 숫자가 아니면 예외처리
	}

	f = 0;										// f값을 초기화
	random_num = [];							// 랜덤하게 받을 수 초기화

	var random_img = document.images;			// 달걀 이미지들의 배열 생성

	for(var i=0; i<24; i++){					// 보여지는 달걀 초기화
		document.images[i].style.border = "none";
		random_img[i].src = "media/img1.gif";
	}
	document.getElementById('over').style.visibility = "hidden";
	
	reset = true;	// 다시 게임이 가능하게 변경
	
}

// 메뉴 선택창 변경
function Contents_init(){								// <jquery 활용>
	$("#div_start").css("visibility","hidden");
	$("#div_remain").text("남은수 : " + n);
	$("#div_fail").text("실패수 : " + f);
	$("#div_time").text("남은 시간 : " + t);
	$("#div_text").text("숨은 그림을 보세요");
} 
// 게임 실행
function Run(time){		// 미리보기 -> 강아지 숨기기 -> 강아지 찾기 -> 결과 순으로 진행
	$("#div_time").text("미리보기 : " + time);

	if(time != 0){	// 미리보기 10초후 else문 실행
		time--;
		setTimeout("Run(time)", 1000);	
	}
	else{
		Hide_Dog();		// 강아지 숨기기
		Check_Dog(); 	// 강아지 찾기
		Time(t);		// 시간초 시작
	}

	this.time=time;
}

function Time(t){
	if(reset == false){return;}	// 게임시작이 불가하면 return 

	$("#div_time").text("남은 시간 : " + t);// t초 타이머 생성 <jquery 활용>

	if(t != 0){	
		setTimeout("Time(t)", 1000);
		t--;
		if(t<=5){
			time_audio.play(); // 시간초가 5초이하가 되면 똑딱똑딱 실행
		}
	}

	else{
		Fail(); // 시간초가 끝나면 실패
	}

	this.t = t;
}

// 랜덤하게 n개의 강아지가 들어갈 달걀 선택
function Choice_Dog(){
	for(var i=0; i<n; i++){
		var rn = Math.floor(Math.random()*24); 	// 0~23의 랜덤숫자 생성
		if (random_num.indexOf(rn) == -1) 		// 중복 숫자 제외
			random_num[i] = rn;					// 랜덤숫자가 랜덤숫자배열에 있는지 확인하고 없으면 배열의 추가 있으면 한번 더 실행 
		else
			i--;
	}
}

// 선택한 강아지 보여주는 함수
function Show_Dog(){
	var random_img = document.images; // 이미지 객체 만들기 

	for(var i=0; i<n; i++){
		random_img[random_num[i]].src = "media/img2.gif";	// 랜덤 숫자를 이미지객체의 index로받아 src변경
	}

}

// 선택한 강아지 숨기는 함수
function Hide_Dog(){
	var random_img = document.images; // 이미지 객체 만들기 

	for(var i=0; i<n; i++){
		random_img[random_num[i]].src = "media/img1.gif";	// 랜덤 숫자를 이미지객체의 index로받아 src변경
	}
}

// 강아지 찾기
function Check_Dog(){
	if(reset == false){return;}	// 게임시작이 불가하면 return 

	$("#div_text").text("정답을 찾으세요");	// <jquery 활용>

	var random_img = document.images;	// 이미지 객체 생성
	var images_num = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23]; // 0~23까지의 배열생성 

	for(var i=0; i<random_num.length; i++){ 	// 선택받지못한 images의 배열생성
		for(var j=0; j<images_num.length; j++){	// random_num배열과 images_num배열를 비교하여 같으면 제거
			if(random_num[i] == images_num[j]){
				images_num.splice(j,1);
			}
		}
	}

	for (var i=0; i<random_num.length; i++) {
			document.images[random_num[i]].onclick = function(e){ // 선택한 이미지객체에 onclick이벤트 설정
				click_audio.src = "media/tada.mp3";
				click_audio.play();
			
				n--;	// 이미지가 클릭되면 남은수 감소

				$("#div_remain").text("남은수 : " + n); // <jquery 활용>

				if(n == 0){		// 남은 수가 없으면 Win()살행
					Win();
					for(var i=0; i<images_num.length; i++){		// 게임이 끝난 후 onclick이벤트 제거
						document.images[images_num[i]].onclick = function(){};
					}
				}

				e.target.src = "media/img2.gif";	// 클릭되면 그림 변경

				this.onclick = function(){};	// 이미 맞춘 그림을 또 선택하는 중복 제거
			};
	}

	for(var i=0; i<images_num.length; i++){
		document.images[images_num[i]].onclick = function(){ //선택하지 않은 이미지 객체에 onclick이벤트 설정
			
			click_audio.src = "media/bad.mp3";
			click_audio.play();
			
			f++;	// 실패횟수 증가

			$("#div_fail").text("실패수 : " + f);	// <jquery 활용>

			if(f>5){	// 실패한 횟수가 5를 초과하면 Fail()실행
				$("#div_fail").text("실패횟수 초과");
				Fail();
				for(var i=0; i<images_num.length; i++){		// 게임이 끝난 후 onclick이벤트 제거
					document.images[images_num[i]].onclick = function(){};
				}
				for (var i=0; i<random_num.length; i++) {	// 게임이 끝난 후 onclick이벤트 제거
					document.images[random_num[i]].onclick = function(e){};
				}
			}
		};
	}
			
}

function Fail(){
	reset = false; // 게임 종료

	var random_img = document.images; // 이미지 객체 생성

	for(var i=0; i<random_num.length; i++){ // 게임이 끝난후 선택한 강아지들 보여주기
		random_img[random_num[i]].src = "media/img2.gif";
		document.images[random_num[i]].style.border = "2px solid red";
	}

	$("#div_start").css("visibility", "visible");	// 게임시작메뉴 다시 보이게하기 <jquery 활용>
	$("#div_text").text("실패");
	$("#over").text("GAME OVER");
	$("#over").css("visibility", "visible");		// GAME OVER 메세지 출력 <jquery 활용>
}

function Win(){
	reset = false;	// 게임 종료
	
	end_audio.play();
	
	$("#div_start").css("visibility", "visible");	// 게임시작메뉴 다시 보이게하기 <jquery 활용>
	$("#div_text").text("성공");
	$("#over").text("VICTORY");
	$("#over").css("visibility", "visible");		// VICTORY 메세지 출력 <jquery 활용>
}