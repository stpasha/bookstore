package com.bookstory.store.web.dto;

import com.bookstory.store.domain.AccountDTO;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CartDTO implements Serializable {
    private Map<Long, ItemDTO> items;
    private String comment;
    private String username;
    private AccountDTO accountDTO;
}
