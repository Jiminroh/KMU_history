## README   
hough space를 생성 후 radius의 변화에 따라 일정 threshhold 이상 되는 점 생성

### STEP LOG
1. 반지름의 변화를 최소 5부터 최대 영상의 width까지 수행    
code -> for r in range(5, rows)   
   - 반지름을 영상의 width까지 수행하면 원은 반지름을 넘어가버림

2. 반지름의 변화를 최소 5부터 최대 영상의 <u>**width/2**</u>까지 수행   
code -> for r in range(5, length), length = int(rows/2)   
   - 반지름 변화량을 1로 하면 시간이 너무 오래걸림 (1시간 수행 후 강제종료)

3. 반지름의 변화를 최소 5부터 최대 영상의 width/2까지 수행(<u>**반지름 변화량 10단위**</u>)   
code -> for r in range(5, length, 10), length = int(rows/2)   
   - 모든 원 검출 성공 
   - 하나의 원에서 많은 원 검출  
   - 반지름이 60 ~ 95까지 원 검출 105 이후부터는 검출 X
   - 수행시간 : 42분 55초

4. 반지름의 변화를 최소 5부터 최대 영상의 width/2까지 수행(반지름 변화량 10단위, <u>**반지름 60 ~ 100까지 실행**</u>)   
code -> for r in range(60, 101, 10), length = int(rows/2)   
   - 하나의 원 검출 실패 
   - 하나의 원에서 많은 원 검출    
   - 수행시간 : 7분 42초

5. 반지름의 변화를 최소 5부터 최대 영상의 width/2까지 수행(<u>**반지름 변화량 5단위**</u>, 반지름 60 ~ 100까지 실행)   
code -> for r in range(60, 101, 5), length = int(rows/2)   
   - 모든 원 검출   
   - 하나의 원에서 많은 원 검출    
   - 수행시간 : 13분 34초

6. 반지름의 변화를 최소 5부터 최대 영상의 width/2까지 수행(반지름 변화량 5단위, 반지름 60 ~ 100까지 실행)   
<u>**축적배열의 threshold값 조정에 따른 변화(기존 threshold: 150)**</u>   

- acc threshold > 300
   - r: 60, 65, 80, 85, 90, 100 검출 X
   - 6개의 원 검출
      - r=70 -> 1개
      - r=75 -> 1개
      - r=95 -> 4개     
   - 3개의 원 검출 실패
   - 수행시간 : 10분 12초

- acc threshold > 250
   - r: 60, 65, 80, 85, 90, 100 검출 X
   - 9개의 원 검출
      - r=70 -> 2개
      - r=75 -> 2개
      - r=95 -> 5개     
   - 1개의 원 검출 실패
   - 수행시간 : 10분 19초

7. <u>**검출되지 않은 중간 크기 원의 최대 축적 값을 알아보기 위해 반지름 80~85까지 변화률 1로 실행**</u>   

radius  | max acc value 
--------|--------------
80 | 182
81 | 195
82 | 290
83 | 310
84 | 354
85 | 197

   - r: 83, 84에서는 threshold 300이상인 값 확인
   - 수행시간 : 8분 51초

8. <u>**radius변화를 [70, 75, 84, 85, 95]로 실행, threshold = 250**</u>   

radius  | max acc value | count | circle point
--------|:---------------:|-------|-------------
70 | 339 | 2개 | (x=130,y=689), (x=634,y=554)
75 | 304 | 2개 | (x=118,y=339), (x=538,y=273)
83 | 310 | 1개 | (x=421,y=415)
84 | 354 | 1개 | x=421, y=415
95 | 197 | 4개 | (x=241,y=502), (x=412,y=500), (x=299,y=228), (x=329,y=789), (x=330,y=787)

   - 모든 원 검출 
   - 수행시간 : 7분 20초   

9. <u>**일반적인 이미지에서 radius = 5 ~ width/2, acc threshold = 250~300으로 실행 시 모든 원을 검출 가능하다고 예측(threshold는 edge이미지를 어떻게 만들었느냐에 따라 다름)**</u>   
  - 컴퓨터 성능 부족으로 수행 X 

### 결론 
- 컴퓨터의 성능과 시간에 영향 받지 않고 정확한 값을 얻기위해 제일 좋은 방법은 
  - radius는 min: 5 ~ max: image's width/2
  - radius의 변화률 = 1
  - acc value threshold는 250 ~ 300   

##### 참고 
- 현재 구현한 코드는 단시간 모든 원이 검출되는 8단계 구현 (최적의 코드는 주석처리 line:14) 
- step별 이미지 첨부 (step 4~8, step 7생략)