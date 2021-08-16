package com.example.teampandanback.OAuth2;

import com.example.teampandanback.domain.user.User;
import com.example.teampandanback.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    //이름은 loadByUsername이지만
    // OAuth2 방식으로 구현할때 유니크 값은 userId이다.
    public UserDetails loadUserByUsername(String userPk) throws UsernameNotFoundException {
        User user = userRepository.findById(Long.parseLong(userPk))
                .orElseThrow(() -> new UsernameNotFoundException("Can't find " + userPk));

        return new UserDetailsImpl(user);
    }
}
