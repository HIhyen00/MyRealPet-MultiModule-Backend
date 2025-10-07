package account.core.service;

import account.dto.LoginResponse;
import account.dto.RegisterRequest;
import account.core.Account;

import java.util.Optional;

public interface AccountService {

    Account createAccount(String username, String password);

    Account createSocialAccount(String username, Account.AuthProvider provider, String providerId);

    Optional<Account> findAccountById(Long id);

    Optional<Account> findAccountByUsername(String username);

    Optional<Account> findAccountByProvider(Account.AuthProvider provider, String providerId);

    boolean isUsernameExists(String username);

    LoginResponse login(String username, String password);

    LoginResponse register(RegisterRequest registerRequest);

    void logout(String token);

    Account getCurrentUser(String token);

    LoginResponse loginWithKakaoToken(String kakaoAccessToken);

    void deleteAccount(String token);
}