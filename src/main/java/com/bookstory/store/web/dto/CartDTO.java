package com.bookstory.store.web.dto;

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
}
