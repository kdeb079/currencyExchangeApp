package com.bluesoft.currencyexchange.mapper;

import com.bluesoft.currencyexchange.dto.AccountDto;
import com.bluesoft.currencyexchange.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDto toDto(Account account);
}
