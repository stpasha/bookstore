package com.bookstory.store.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserDTO {
    @Getter
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

    private List<String> authorities;
}
