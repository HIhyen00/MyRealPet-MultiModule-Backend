package account.api.controller;

import account.core.service.AccountService;
import account.dto.*;
import account.core.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 계정 및 인증 관련 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 일반 로그인
     * @param loginRequest 로그인 요청 정보
     * @return 로그인 응답 (토큰 포함)
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = accountService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    /**
     * 회원가입
     * @param registerRequest 회원가입 요청 정보
     * @return 로그인 응답 (토큰 포함)
     */
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest registerRequest) {
        LoginResponse response = accountService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃
     * @param token 인증 토큰
     * @return void
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        accountService.logout(token.replace("Bearer ", ""));
        return ResponseEntity.ok().build();
    }

    /**
     * 현재 로그인된 사용자 정보 조회
     * @param token 인증 토큰
     * @return 계정 정보
     */
    @GetMapping("/me")
    public ResponseEntity<Account> getCurrentUser(@RequestHeader("Authorization") String token) {
        Account user = accountService.getCurrentUser(token.replace("Bearer ", ""));
        return ResponseEntity.ok(user);
    }

    /**
     * 카카오 로그인 (Authorization Code) - 단순화됨
     * @param kakaoLoginRequest 카카오 로그인 요청 정보
     * @return 로그인 응답 (토큰 포함)
     */
    @PostMapping("/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestBody KakaoLoginRequest kakaoLoginRequest) {
        log.info("Kakao login request received with code: {}", kakaoLoginRequest.getCode());
        // 단순화: 복잡한 OAuth 처리 대신 기본 응답
        throw new UnsupportedOperationException("Kakao login temporarily disabled for simplification");
    }

    /**
     * 카카오 로그인 (Access Token)
     * @param kakaoTokenRequest 카카오 토큰 요청 정보
     * @return 로그인 응답 (토큰 포함)
     */
    @PostMapping("/kakao/token")
    public ResponseEntity<LoginResponse> kakaoTokenLogin(@RequestBody KakaoTokenRequest kakaoTokenRequest) {
        log.info("Kakao token login request received");
        LoginResponse loginResponse = accountService.loginWithKakaoToken(kakaoTokenRequest.getAccessToken());
        return ResponseEntity.ok(loginResponse);
    }
}