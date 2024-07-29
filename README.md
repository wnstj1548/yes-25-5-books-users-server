---
YES 25.5 도서,회원 서버입니다.</br></br>
---

## 🛠️ 기술 스택

### 환경
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"/> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"/>

### 개발
<img src="https://img.shields.io/badge/java-ff7f00?style=for-the-badge&logo=java&logoColor=white"/> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/> <img src="https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge&logo=JPA&logoColor=white"> 
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white">
<img src="https://img.shields.io/badge/spring cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/>

### 클라우드
<img src="https://img.shields.io/badge/nhn cloud-blue?style=for-the-badge&logo=nhncloud&logoColor=white"/>

### 데이터베이스
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>

### Other
<img src= "https://img.shields.io/badge/-ElasticSearch-005571?style=for-the-badge&logo=elasticsearch">


<br/>
<br/>

## 🖥️ 화면 구성

![스크린샷 2024-07-29 오후 1 48 57](https://github.com/user-attachments/assets/43960798-5355-4a98-a083-9d37eeb71f27)

> 홈 화면

![스크린샷 2024-07-29 오후 1 49 12](https://github.com/user-attachments/assets/ee4310b5-ff6a-4cf8-a842-05b51352dd24)

> 도서 검색 및 카테고리 클릭 리스트 화면

## 기능목록![스크린샷 2024-07-29 오후 1 51 38](https://github.com/user-attachments/assets/3cc0ca7c-61da-4793-b7de-006b63d1ed48)

> 도서 상세 페이지 화면

## 기능 목록

- [x] 도서
    - [x] 도서 등록
        - [x] 네이버 책 API를 활용하여 도서를 등록합니다.
        - [x] 도서를 직접 등록할 수 있습니다.
        - [x] NHN Cloud Image Manager를 사용하여 이미지를 등록합니다.
        - [x] Toast UI를 사용하여 책 설명을 등록합니다. 
              
    - [x] 도서 수정
        - [x] 도서를 수정합니다.

    - [x] 도서 조회
        - [x] 페이징 처리하여 조회합니다.
        - [x] 정렬 기준 별로 조회할 수 있습니다.
         
    - [x] 도서 삭제
        - [x] 도서를 삭제합니다. (도서의 isDeleted를 조작하여 사용합니다.)
 
    - [x] 좋아요 기능
        - [x] 가입한 회원은 도서에 좋아요를 설정할 수 있습니다.
         
    - [x] 도서 검색
        - [x] Spring Data ElasticSearch 활용 (full text 검색)
        - [x] 동의어, 유의어로 검색이 가능합니다.
        - [x] 형태소 분석기를 사용하여 단어로 검색이 가능합니다.
        - [x] 카테고리, 설명, 제목, 태그, 작가로 검색이 가능합니다. 
              
- [x] 카테고리

    - [x] 카테고리 조회
        - [x] 페이징 처리하여 조회합니다.
                
    - [x] 카테고리 등록
        - [x] 카테고리를 등록합니다.
        - [x] 2단계 이상 카테고리가 가능합니다.
              
    - [x] 카테고리 수정
        - [x] 카테고리 이름을 수정할 수 있습니다.
         
    - [x] 카테고리 삭제
        - [x] 카테고리를 삭제합니다.
         
- [x] 태그

    - [x] 태그 조회
        - [x] 페이징 처리하여 조회합니다.
                
    - [x] 태그 등록
        - [x] 태그를 등록합니다.
              
    - [x] 태그 수정
        - [x] 태그 이름을 수정할 수 있습니다.
         
    - [x] 태그 삭제
        - [x] 태그를 삭제합니다.
         
- [x] API 명세서
    - [x] Swagger를 통하여 API 명세서를 작성합니다.  
