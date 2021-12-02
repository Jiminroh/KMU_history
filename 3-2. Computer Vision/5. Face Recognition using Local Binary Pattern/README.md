# Local Binary Pattern을 활용한 Face Recognition

### Dataset Description:
- 그레이 스케일 face 이미지 17장 + Non-face 이미지 1장 총 18장으로 구성
- 각 이미지의 사이즈는 195 × 231

- apple1_gray.jpg, 
  subject01.centerlight.jpg, subject01.happy.jpg, subject01.normal.jpg,
  subject02.normal.jpg, subject03.normal.jpg, subject07.centerlight.jpg, subject07.happy.jpg, subject07.normal.jpg, subject10.normal.jpg, subject11.centerlight.jpg, subject11.happy.jpg, subject11.normal.jpg, subject12.normal.jpg, subject14.happy.jpg, subject14.normal.jpg, subject14.sad.jpg, subject15.normal.jpg

### squence:
- get pixel
- LBP calculation to pixel
- histogram calculation 
- print lbp_image
- print lbp_histogram