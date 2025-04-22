package com.bookstory.store.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Builder
@Setter
@EqualsAndHashCode
public class User implements UserDetails {

    @Getter
    @EqualsAndHashCode.Include
    Long id;

    @Size(max = 255, message = "Should be not greater than 255 symbols")
    @NotEmpty
    @NotNull
    private String username;

    @Size(max = 255, message = "Should be not greater than 255 symbols")
    @NotEmpty
    @NotNull
    private String password;

    private boolean enabled;

    Collection<? extends GrantedAuthority> authorities;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
