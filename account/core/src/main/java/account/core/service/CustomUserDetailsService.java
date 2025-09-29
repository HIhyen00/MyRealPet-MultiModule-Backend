package account.core.service;

import account.core.Account;
import account.core.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // In our case, username can be email, username, or kakaoId
        return accountRepository.findByUsername(username)
                .or(() -> accountRepository.findByEmail(username))
                .or(() -> accountRepository.findByKakaoId(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
