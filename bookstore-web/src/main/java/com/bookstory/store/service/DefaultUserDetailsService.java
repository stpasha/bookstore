package com.bookstory.store.service;

import com.bookstory.store.model.User;
import com.bookstory.store.repository.UserRepository;
import com.bookstory.store.web.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultUserDetailsService implements ReactiveUserDetailsService, UserDetailsService {

    private final UserRepository userRepository;

    private static User apply(UserDTO userDTO) {
        return User.builder().username(userDTO.getUsername()).password(userDTO.getPassword()).enabled(userDTO.isEnabled())
                .authorities(userDTO.getAuthorities().stream().map(SimpleGrantedAuthority::new).toList()).build();
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username).filter(UserDetails::isEnabled);
    }


    @Override
    @Transactional
    @Secured("ADMIN")
    public Mono<Void> createUser(Mono<UserDTO> user) {
        return user.map(DefaultUserDetailsService::apply).flatMap(user1 -> {
            log.info("Create user " + user1);
            return userRepository.createUserWithRoles(user1);
        });
    }
}

