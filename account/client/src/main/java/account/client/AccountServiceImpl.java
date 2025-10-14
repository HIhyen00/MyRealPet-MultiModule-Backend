package account.client;

import account.client.oauth.OAuthAttributes;
import account.core.AccountRepository;
import account.core.service.AccountService;
import account.core.Account;
import account.dto.LoginResponse;
import account.dto.RegisterRequest;
import com.myrealpet.common.session.UserSessionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    @Qualifier("kakaoApiClient")
    private final WebClient kakaoApiClient;
    private final UserSessionUtil userSessionUtil;

    @Override
    public Account createAccount(String username, String password) {
        Account account = Account.builder()
                .username(username)
                .password(passwordEncoder.encode(password)) // Encode password
                .provider(Account.AuthProvider.LOCAL)
                .build();
        return accountRepository.save(account);
    }

    @Override
    public Account createSocialAccount(String username, Account.AuthProvider provider, String providerId) {
        Account account = Account.builder()
                .username(username)
                .provider(provider)
                .providerId(providerId)
                .build();
        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> findAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> findAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public Optional<Account> findAccountByProvider(Account.AuthProvider provider, String providerId) {
        return accountRepository.findByProviderAndProviderId(provider, providerId);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return accountRepository.existsByUsername(username);
    }

    @Override
    public LoginResponse login(String username, String password) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 세션 토큰 생성 (UUID)
        String sessionToken = UUID.randomUUID().toString();

        // Redis에 세션 저장 (토큰을 키로 사용)
        userSessionUtil.saveSession(sessionToken, account.getId(), account.getUsername(), account.getRole().name());

        return LoginResponse.of(sessionToken, account.getId(),
                               account.getUsername(), account.getRole().name(), Duration.ofHours(24));
    }

    @Override
    public LoginResponse register(RegisterRequest registerRequest) {
        if (isUsernameExists(registerRequest.getId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        Account account = createAccount(registerRequest.getId(), registerRequest.getPassword());

        // 세션 토큰 생성 (UUID)
        String sessionToken = UUID.randomUUID().toString();

        // Redis에 세션 저장 (토큰을 키로 사용)
        userSessionUtil.saveSession(sessionToken, account.getId(), account.getUsername(), account.getRole().name());

        return LoginResponse.of(sessionToken, account.getId(),
                               account.getUsername(), account.getRole().name(), Duration.ofHours(24));
    }

    @Override
    public void logout(String token) {
        // Redis에서 세션 삭제
        userSessionUtil.removeSession(token);
        log.info("User logged out with token: {}", token);
    }

    @Override
    public LoginResponse loginWithKakaoToken(String kakaoAccessToken) {
        Map<String, Object> userInfo = getKakaoUserInfo(kakaoAccessToken);
        OAuthAttributes attributes = OAuthAttributes.of("kakao", "id", userInfo);

        Account account = saveOrUpdateSocialUser(attributes);

        // 세션 토큰 생성 (UUID)
        String sessionToken = UUID.randomUUID().toString();

        // Redis에 세션 저장 (토큰을 키로 사용)
        userSessionUtil.saveSession(sessionToken, account.getId(), account.getName(), account.getRole().name());

        return LoginResponse.of(sessionToken, account.getId(),
                               account.getName(), account.getRole().name(), Duration.ofHours(24));
    }

    private Account saveOrUpdateSocialUser(OAuthAttributes attributes) {
        Account account = accountRepository.findByProviderAndProviderId(
                attributes.getProvider(),
                attributes.getProviderId()
        ).orElse(attributes.toEntity());

        return accountRepository.save(account);
    }

    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        return kakaoApiClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    @Override
    public Account getCurrentUser(String token) {
        // Redis에서 세션 조회
        Map<String, Object> session = userSessionUtil.getSession(token);
        if (session == null) {
            throw new RuntimeException("유효하지 않거나 만료된 토큰입니다.");
        }

        // 세션에서 userId 추출
        Object userIdObj = session.get("userId");
        Long userId = userIdObj instanceof Integer ? ((Integer) userIdObj).longValue() : (Long) userIdObj;

        // userId로 Account 조회
        return accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("계정을 찾을 수 없습니다."));
    }

    @Override
    public void deleteAccount(String token) {
        // 현재 사용자 조회
        Account account = getCurrentUser(token);

        // 세션 삭제 (로그아웃 처리)
        userSessionUtil.removeSession(token);

        // 계정 완전 삭제 (하드 삭제)
        accountRepository.delete(account);

        log.info("Account deleted: userId={}, username={}", account.getId(), account.getUsername());
    }
}