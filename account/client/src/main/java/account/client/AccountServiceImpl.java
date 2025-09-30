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
    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public List<Account> findInactiveAccounts() {
        return accountRepository.findInactiveAccounts();
    }

    @Override
    public Account updatePassword(Long accountId, String newPassword) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
        account.updatePassword(passwordEncoder.encode(newPassword)); // Encode new password
        return accountRepository.save(account);
    }

    @Override
    public Account deactivateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
        account.deactivate();
        return accountRepository.save(account);
    }

    @Override
    public Account activateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
        account.activate();
        return accountRepository.save(account);
    }

    @Override
    public void deleteAccount(Long accountId) {
        accountRepository.deleteById(accountId);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return accountRepository.existsByUsername(username);
    }

    @Override
    public LoginResponse login(String username, String password) {
        Account account = accountRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // 세션 토큰 생성 (UUID)
        String sessionToken = UUID.randomUUID().toString();

        // Redis에 세션 저장 (토큰을 키로 사용)
        userSessionUtil.saveSession(sessionToken, account.getId(), account.getUsername());

        return LoginResponse.of(sessionToken, account.getId(),
                               account.getUsername(), Duration.ofHours(24));
    }

    @Override
    public LoginResponse register(RegisterRequest registerRequest) {
        if (isUsernameExists(registerRequest.getId())) {
            throw new IllegalArgumentException("Username '" + registerRequest.getId() + "' already exists");
        }

        Account account = createAccount(registerRequest.getId(), registerRequest.getPassword());

        // 세션 토큰 생성 (UUID)
        String sessionToken = UUID.randomUUID().toString();

        // Redis에 세션 저장 (토큰을 키로 사용)
        userSessionUtil.saveSession(sessionToken, account.getId(), account.getUsername());

        return LoginResponse.of(sessionToken, account.getId(),
                               account.getUsername(), Duration.ofHours(24));
    }

    @Override
    public void logout(String token) {
        // Redis에서 세션 삭제
        userSessionUtil.removeSession(token);
        log.info("User logged out with token: {}", token);
    }

    @Override
    public void logoutAll(Long accountId) {
        log.info("Logout all for account: {}", accountId);
        // Note: 전체 로그아웃 기능은 향후 Redis를 통한 토큰 관리가 구현되면 추가 예정
    }

    @Override
    public LoginResponse loginWithKakaoToken(String kakaoAccessToken) {
        Map<String, Object> userInfo = getKakaoUserInfo(kakaoAccessToken);
        OAuthAttributes attributes = OAuthAttributes.of("kakao", "id", userInfo);

        Account account = saveOrUpdateSocialUser(attributes);

        // 세션 토큰 생성 (UUID)
        String sessionToken = UUID.randomUUID().toString();

        // Redis에 세션 저장 (토큰을 키로 사용)
        userSessionUtil.saveSession(sessionToken, account.getId(), account.getName());

        return LoginResponse.of(sessionToken, account.getId(),
                               account.getName(), Duration.ofHours(24));
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
        // 단순화: 토큰 검증 없이 직접 처리
        // 실제로는 인터셉터에서 이미 세션 검증을 했으므로 불필요
        throw new UnsupportedOperationException("Use getUserById instead");
    }

    public Account getUserById(Long userId) {
        return accountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + userId));
    }
}