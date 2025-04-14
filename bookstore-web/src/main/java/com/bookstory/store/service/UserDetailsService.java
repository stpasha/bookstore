package com.bookstory.store.service;

import com.bookstory.store.web.dto.UserDTO;
import reactor.core.publisher.Mono;

public interface UserDetailsService {
    Mono<Void> createUser(Mono<UserDTO> username);
}
