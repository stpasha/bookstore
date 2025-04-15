package com.bookstory.store.repository;

import com.bookstory.store.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepository {

    private final DatabaseClient databaseClient;

    private final PasswordEncoder passwordEncoder;

    public Mono<UserDetails> findByUsername(String username) {
        return databaseClient.sql("""
            SELECT
                usr.user_id,
                usr.username,
                usr.password,
                usr.enabled,
                auth.role
            FROM
                storedata.users AS usr
            INNER JOIN
                storedata.users_roles AS auth
                ON (usr.user_id = auth.user_id)
            WHERE usr.username = :username
            """)
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

    private Mono<Long> insertUser(String username, String password, boolean enabled) {
        String insertUserSql = """
            INSERT INTO storedata.users (username, password, enabled)
            VALUES (:username, :password, :enabled)
            RETURNING user_id
            """;
        log.info("insrt user {}", username);
        return databaseClient.sql(insertUserSql)
                .bind("username", username)
                .bind("password", password)
                .bind("enabled", enabled)
                .map((row, metadata) -> row.get("user_id", Long.class))
                .one();
    }

    private Mono<Void> insertUserRole(Long userId, String role) {
        String insertRoleSql = """
            INSERT INTO storedata.users_roles (user_id, role)
            VALUES (:user_id, :role)
            """;
        log.info("insrt authorities {} {}", userId, role);
        return databaseClient.sql(insertRoleSql)
                .bind("user_id", userId)
                .bind("role", role)
                .then();
    }

    private Mono<Void> insertBasicAmount(Long userId) {
        String insertAccSql = """
                INSERT INTO storedata.accounts (amount, version, user_id)
                        VALUES
                        (1000, 1, :user_id)
            """;
        log.info("insrt 1000 rub {}", userId);
        return databaseClient.sql(insertAccSql)
                .bind("user_id", userId)
                .then();
    }

    public Mono<Void> createUserWithRoles(User user) {
        return insertUser(user.getUsername(), passwordEncoder.encode(user.getPassword()), user.isEnabled())
                .flatMap(userId -> {
                    log.info("prepare to db {} ", userId );
                    return Flux.fromIterable(user.getAuthorities())
                            .flatMap(auth -> insertUserRole(userId, auth.getAuthority()))
                            .then(insertBasicAmount(userId));
                });
    }

    private record UserRoleRow(Long userId, String username, String password, Boolean enabled, String role) {}
}
