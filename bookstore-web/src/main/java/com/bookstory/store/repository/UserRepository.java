package com.bookstory.store.repository;

import com.bookstory.store.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private static final String SELECT = """
            SELECT
                usr.user_id,
                usr.username,
                usr.password,
                usr.enabled,
                auth.role
            FROM
                storedata.users AS usr
            INNER JOIN
                storedata.authorities AS auth
                ON (usr.user_id = auth.user_id)
            WHERE usr.username = :username
            """;

    private final DatabaseClient databaseClient;

    public Mono<UserDetails> findByUsername(String username) {
        return databaseClient.sql(SELECT)
                .bind("username", username)
                .map((row, metadata) -> new UserRoleRow(
                        row.get("user_id", Long.class),
                        row.get("username", String.class),
                        row.get("password", String.class),
                        row.get("enabled", Boolean.class),
                        row.get("role", String.class)
                ))
                .all()
                .collectList()
                .flatMap(rows -> {
                    if (rows.isEmpty()) {
                        return Mono.empty();
                    }
                    UserRoleRow firstRow = rows.getFirst();
                    List<SimpleGrantedAuthority> authorities = rows.stream()
                            .map(r -> new SimpleGrantedAuthority(r.role()))
                            .collect(Collectors.toList());

                    return Mono.just(User.builder()
                            .id(firstRow.userId())
                            .username(firstRow.username())
                            .password(firstRow.password())
                            .enabled(firstRow.enabled())
                            .authorities(authorities)
                            .build());
                });
    }

    private record UserRoleRow(Long userId, String username, String password, Boolean enabled, String role) {}
}
