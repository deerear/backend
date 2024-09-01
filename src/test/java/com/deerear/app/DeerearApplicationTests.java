//package com.deerear.deerear;
//
//import com.deerear.deerear.dto.JwtToken;
//import com.deerear.deerear.dto.MemberDto;
//import com.deerear.deerear.dto.SignInDto;
//import com.deerear.deerear.dto.SignUpDto;
//import com.deerear.deerear.service.MemberService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.*;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class DeerearApplicationTests {
//
//	@Autowired
//	MemberService memberService;
//	@Autowired
//	TestRestTemplate testRestTemplate;
//
//	@LocalServerPort
//	int randomServerPort;
//
//	private SignUpDto signUpDto;
//
//	@BeforeEach
//	void beforeEach() {
//		// Member 회원가입
//		signUpDto = SignUpDto.builder()
//				.username("member")
//				.password("12345678")
//				.nickname("닉네임")
//				.address("서울시 광진구")
//				.phone("010-1234-5678")
//				.build();
//	}
//
//
//	@Test
//	public void signUpTest() {
//
//		// API 요청 설정
//		String url = "http://localhost:" + randomServerPort + "/members/sign-up";
//		ResponseEntity<MemberDto> responseEntity = testRestTemplate.postForEntity(url, signUpDto, MemberDto.class);
//
//		// 응답 검증
//		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//		MemberDto savedMemberDto = responseEntity.getBody();
//		assertThat(savedMemberDto.getUsername()).isEqualTo(signUpDto.getUsername());
//		assertThat(savedMemberDto.getNickname()).isEqualTo(signUpDto.getNickname());
//	}
//
//	@Test
//	public void signInTest() {
//
//		SignInDto signInDto = SignInDto.builder()
//				.username("member")
//				.password("12345678").build();
//
//		// 로그인 요청
//		JwtToken jwtToken = memberService.signIn(signInDto);
//
//		// HttpHeaders 객체 생성 및 토큰 추가
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.setBearerAuth(jwtToken.getAccessToken());
//		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//		// API 요청 설정
//		String url = "http://localhost:" + randomServerPort + "/members/user";
//		ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(url, new HttpEntity<>(httpHeaders), String.class);
//		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//		assertThat(responseEntity.getBody()).isEqualTo(signInDto.getUsername());
//
////        assertThat(SecurityUtil.getCurrentUsername()).isEqualTo(signInDto.getUsername()); // -> 테스트 코드에서는 인증을 위한 절차를 거치지 X. SecurityContextHolder 에 인증 정보가 존재하지 않는다.
//
//
//	}
//
//}
