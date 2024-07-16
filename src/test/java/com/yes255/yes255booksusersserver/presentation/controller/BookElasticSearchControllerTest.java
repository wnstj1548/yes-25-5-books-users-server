package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.BookSearchService;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookIndexResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class BookElasticSearchControllerTest {

    @Mock
    private BookSearchService bookSearchService;

    @InjectMocks
    private BookElasticSearchController bookElasticSearchController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("책 이름 검색 - 성공")
    @Test
    void searchByName_success() {

        String keyword = "Spring";
        Pageable pageable = PageRequest.of(0, 10);
        List<BookIndexResponse> bookIndexResponses = List.of(
                BookIndexResponse.builder()
                        .bookId("37")
                        .bookIsbn("9791198265173")
                        .bookName("마음이 부는 곳")
                        .bookDescription("**“바람이 분다.”**\n" +
                                "\n" +
                                "“바람은 어디서 왔지?”\n" +
                                "“음, 바람은 마음에서 왔지.”\n" +
                                "“마음이 분다.”\n" +
                                "\n" +
                                "이 책은 과거 여행을 많이 하던 당시 작가가 이국에서 만난 사람과 시간에 대한 기록입니다. 모로코 페즈라는 작은 소도시에서부터 사하라 사막까지. 「마음이 부는 곳」은 과거 제가 잃어버린 마음을 찾아 세계 각국을 떠돌아다니던 시절, 여행을 통해 종국에 “마음”을 발견하게 된 계기가 된 사건에 대한 기록입니다.\n" +
                                "\n" +
                                "“실은 그간 많은 글을 썼지만, 내면의 절반의 절반도 아직 보여 주지 못했다. 아직 보여주지 못한 풍경이 많다. 언젠가 한 번은 발설할 때가 있겠지, 그 이야기를 이제는 꺼내어보아도 좋겠지, 하는 마음. 오랜 서랍 속 깊숙이 잘 접어둔 지도를 펼쳐보듯 조심스럽고 떨리는 손으로 그것을 서서히 열어본다. 언젠가 꼭 해야 할 일. 다시금 흐트러진 길들을 배열해 기억의 지도를 완성하는 일. 이제는 쓸 수 있는 글, 써야만 하는 글, 그리고 이제는 써도 될 것 같은 글.” \\_ [본문 중에서]")
                        .bookPublisher("홀로씨의 테이블")
                        .bookPrice(new BigDecimal("12600.00"))
                        .bookSellingPrice(new BigDecimal("11100.00"))
                        .bookImage("http://image.toast.com/aaaacuf/yes25-5-images/heart.jpeg")
                        .quantity(9999)
                        .reviewCount(0)
                        .hitsCount(13)
                        .searchCount(0)
                        .bookIsPackable(true)
                        .authors(null)
                        .tags(null)
                        .build()
        );
        Page<BookIndexResponse> page = new PageImpl<>(bookIndexResponses);

        when(bookSearchService.searchBookByNamePaging(anyString(), any(Pageable.class), anyString())).thenReturn(page);


        ResponseEntity<Page<BookIndexResponse>> responseEntity = bookElasticSearchController.searchByName(keyword, pageable, "popularity");


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(page, responseEntity.getBody());
    }

    @DisplayName("책 설명 검색 - 성공")
    @Test
    void searchByDescription_success() {

        String keyword = "mbti";
        Pageable pageable = PageRequest.of(0, 10);
        List<BookIndexResponse> bookIndexResponses = List.of(
                BookIndexResponse.builder()
                        .bookId("2")
                        .bookIsbn("9791138372862")
                        .bookName("조선왕조 MBTI 실록 (조선을 이끈 인물들의 반전 있는 MBTI 이야기)")
                        .bookDescription("**나는 조선 시대에 어떤 인물이었을까?**\n" +
                                "\n" +
                                "**조선을 이끈 32인의 반전 있는 MBTI 이야기**\n" +
                                "\n" +
                                "혈액형의 자리를 단번에 밀어내고, ‘성격’하면 떠오르게 된 것은 MBTI다. 그런데 과연 MBTI가 현재를 살고 있는 우리에게만 해당될까? 『조선왕조MBTI실록』은 ‘나의 MBTI는 역사 속에서 어떤 인물이었을까?’라는 호기심에서 시작한다. 그리고 그 호기심을 해결해 줄 대상이 바로 조선 500년의 역사를 기록한 『조선왕조실록』이었다. 유네스코 세계기록유산으로 등재될 만큼 상세히 기록된 조선의 역사를 통해 프로손절러 INFJ 이성계부터 팩폭러 INTP 박문수까지, 32인의 성격을 분석하여 그들의 인생사와 함께 MBTI를 흥미롭게 풀어냈다.\n" +
                                "\n" +
                                "이 책에는 역사의 한 획을 그을 정도로 성공한 사람도, 지금까지도 왜 그런 행동을 했는지 수백 년 동안 욕(?)을 먹는 사람도 있다. 역사 속 인물을 만나 그들의 삶과 인생 해법도 알아보며, 만약 나였다면 그 상황에서 어떻게 행동했을지 상상해보자. 그저 실록에 적힌 낡은 역사가 아닌, 생생하게 느껴지는 재미를 느낄 수 있을 것이다.\n" +
                                "\n" +
                                "* \n" +
                                "    <br>\n")
                        .bookPublisher("시대인")
                        .bookPrice(new BigDecimal("15300.00"))
                        .bookSellingPrice(new BigDecimal("13800.00"))
                        .bookImage("http://image.toast.com/aaaacuf/yes25-5-images/mbti.jpeg")
                        .quantity(1111)
                        .reviewCount(0)
                        .hitsCount(null)
                        .searchCount(0)
                        .bookIsPackable(true)
                        .authors(null)
                        .tags(null)
                        .build()
        );
        Page<BookIndexResponse> page = new PageImpl<>(bookIndexResponses);

        when(bookSearchService.searchBookByDescription(anyString(), any(Pageable.class), anyString())).thenReturn(page);


        ResponseEntity<Page<BookIndexResponse>> responseEntity = bookElasticSearchController.searchByDescription(keyword, pageable, "popularity");


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(page, responseEntity.getBody());
    }

    @DisplayName("태그로 검색 - 성공")
    @Test
    void searchByTagName_success() {
        // given
        String keyword = "기술";
        Pageable pageable = PageRequest.of(0, 10);
        List<BookIndexResponse> bookIndexResponses = List.of(
                BookIndexResponse.builder()
                        .bookId("39")
                        .bookIsbn("9791157847549")
                        .bookName("통찰하는 기계 질문하는 리더 (AI 시대, 대체 불가능한 리더의 첫 번째 조건)")
                        .bookDescription("**\\*\\*최창원, 송길영, 유필화, 신수정\\*\\***\n" +
                                "\n" +
                                "기업과 학계 선도자들의 강력 추천!\n" +
                                "\n" +
                                "국내 최고 AI 비즈니스 전문가가 제안하는\n" +
                                "**개인과 기업을 위한 AI 시대 독파 전략**\n" +
                                "\n" +
                                "챗GPT의 충격적 등장 이후 우리는 급격하게 변화하는 일상과 비즈니스를 실시간으로 경험하고 있다. 생각의 속도보다 더 빠르게 변화하는 대혼돈의 시대에 리더는 어떻게 기술 발전에 대응하고 원하는 미래로 조직을 이끌어갈 것인가? 《통찰하는 기계 질문하는 리더》의 저자로 국내외에서 AI 관련 이론적 기초와 다양한 현장 경험을 쌓아온 변형균 퓨처웨이브 대표는 단언한다. “문제는 이 기술을 ‘어떻게 사용할 수 있는가?’가 아니다. AI를 사용해 ‘어떻게 제품 또는 서비스를 재정의하거나 완전히 새로운 비즈니스 모델을 만들 수 있는가?’라는 질문이 올바른 접근이다.”\n" +
                                "\n" +
                                "AI는 칼 야스퍼스가 말한 ‘새로운 축’에 버금가는 거대한 변화를 특히 기업의 세계에 가져올 것이다. 따라서 새로운 시대에 걸맞는 새로운 리더십은 기술을 이해하고, 그 이해의 바탕 위에서 “올바른 질문을 던지는 것”이 되어야 한다고 저자는 강변한다. AI 기술의 진화사부터 변화의 맥락, 주요 플레이어의 전략과 선구자들의 통찰로 가득한 이 책은 당신을 능숙한 질문자로 이끄는 첫 단추가 되어줄 것이다.\n" +
                                "\n" +
                                "“변화 속에서 성찰과 성장을 열망하는 리더들에게 일독을 권한다.”\n" +
                                "\\_최창원(SK디스커버리 부회장)")
                        .bookPublisher("한빛비즈")
                        .bookPrice(new BigDecimal("18000.00"))
                        .bookSellingPrice(new BigDecimal("16000.00"))
                        .bookImage("http://image.toast.com/aaaacuf/yes25-5-images/leader.jpeg")
                        .quantity(1000)
                        .reviewCount(0)
                        .hitsCount(null)
                        .searchCount(0)
                        .bookIsPackable(true)
                        .authors(null)
                        .tags(null)
                        .build()
        );
        Page<BookIndexResponse> page = new PageImpl<>(bookIndexResponses);

        when(bookSearchService.searchBookByTagName(anyString(), any(Pageable.class), anyString())).thenReturn(page);


        ResponseEntity<Page<BookIndexResponse>> responseEntity = bookElasticSearchController.searchByTagName(keyword, pageable, "popularity");


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(page, responseEntity.getBody());
    }

    @DisplayName("작가로 검색 - 성공")
    @Test
    void searchByAuthorName_success() {

        String keyword = "한국사";
        Pageable pageable = PageRequest.of(0, 10);
        List<BookIndexResponse> bookIndexResponses = List.of(
                BookIndexResponse.builder()
                        .bookId("40")
                        .bookIsbn("9791193401125")
                        .bookName("벌거벗은 한국사: 고려편 (격동의 500년이 단숨에 이해되는 스토리텔링 고려사)")
                        .bookDescription("**10만 독자가 열광한 『벌거벗은 한국사』 ‘시대편’ 완간!**\n" +
                                "\n" +
                                "그 마지막 이야기, 감동의 대한민국 근현대사를 만나다\n" +
                                "\n" +
                                "“대한민국 역사상 가장 어두웠던 시대,\n" +
                                "**희망의 빛을 밝힌 담대하고도 숭고한 여정!”**\n" +
                                "\n" +
                                "98주 연속 역사 베스트셀러로 그 입지를 굳힌 대한민국 대표 한국사 시리즈『벌거벗은 한국사』가 근현대의 역사 이야기로 ‘시대편’의 마침표를 찍는다. 이번에는 tvN STORY 간판 교양 예능 〈벌거벗은 한국사〉에서 소개된 이야기 중 감동의 근현대사 100년을 대표하는 사건과 인물을 엄선해 입체적으로 조망했다. 근현대의 역사는 오늘의 삶과 맞닿아 있는 최근의 역사로, 근현대사를 모르면 지금 우리나라에서 벌어지는 일을 제대로 이해하기 어렵다. 해방 후 지금까지 남과 북의 분단 상황이 이어지고 있고, 나라 안팎으로 역사 왜곡의 시도가 계속되고 있는 가운데 한국이 나아갈 올바른 길을 찾기 위해서 과거 역사를 배우는 일은 선택이 아닌 필수가 되었다.\n" +
                                "\n" +
                                "대한민국 근현대사는 일본의 침략과 강점, 국내외에서 일어난 독립운동, 해방 그리고 분단까지 격변의 연속이었다. 『벌거벗은 한국사: 근현대편』은 이 격동의 풍파 속에서도 꿈을 향해 자신의 모든 것을 건 이들에게 주목한다. 감옥 안에서도 만세운동을 일으킨 유관순부터 시대의 한계에 맞선 신여성 나혜석, 일제에 적극적으로 맞서지 못한 것을 부끄러워하면서도 한글로 시 쓰기를 멈추지 않았던 윤동주까지 자신의 운명을 짊어진 이들의 이야기를 통해 100여 년 동안 우리나라가 겪은 고난과 영광을 마주하게 된다. 이 책을 통해 독자들은 한 번쯤 들어보았던 인물과 사건에 숨은 이야기에 감동하면서 근현대 시기 역사의 맥락과 교훈을 이해하게 될 것이다. 또 그 속에서 지금의 문제를 해결할 실마리를 찾고, 삶과 역사의 주체로 자신을 바로 세우게 될 것이다. 어두웠던 시대에도 꺼지지 않고 밝게 타오른 희망의 역사 속으로 지금 떠나보자.")
                        .bookPublisher("프런트페이지")
                        .bookPrice(new BigDecimal("16920.00"))
                        .bookSellingPrice(new BigDecimal("15000.00"))
                        .bookImage("http://image.toast.com/aaaacuf/yes25-5-images/korean.jpeg")
                        .quantity(1000)
                        .reviewCount(0)
                        .hitsCount(0)
                        .searchCount(0)
                        .bookIsPackable(false)
                        .authors(List.of("tvN STORY 〈벌거벗은 한국사〉 제작팀"))
                        .tags(List.of("교육"))
                        .build()
        );
        Page<BookIndexResponse> page = new PageImpl<>(bookIndexResponses);

        when(bookSearchService.searchBookByAuthorName(anyString(), any(Pageable.class), anyString())).thenReturn(page);


        ResponseEntity<Page<BookIndexResponse>> responseEntity = bookElasticSearchController.searchByAuthorName(keyword, pageable, "popularity");


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(page, responseEntity.getBody());
    }


}
