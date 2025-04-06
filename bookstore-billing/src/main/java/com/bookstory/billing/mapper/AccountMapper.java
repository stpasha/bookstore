package com.bookstory.billing.mapper;

import com.bookstory.billing.domain.Account;
import com.bookstory.billing.domain.AccountDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDTO toDto(Account entity);
    Account toEntity(AccountDTO dto);
}
