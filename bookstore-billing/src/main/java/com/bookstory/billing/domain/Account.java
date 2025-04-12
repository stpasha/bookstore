package com.bookstory.billing.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table(name = "accounts", schema = "storedata")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
    @Id
    @Column("account_id")
    private Long id;
    @Column("amount")
    private BigDecimal amount;
    @Column("version")
    @Version
    private Integer version;
    @Column("user_id")
    private Long userId;
}
