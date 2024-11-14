package com.bluesoft.currencyexchange.service;

import com.bluesoft.currencyexchange.dto.CurrencyRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CurrencyRateProvider {

    private final NbpClient nbpClient;

    public BigDecimal getUsdToPlnRate() {
        CurrencyRateResponse response = nbpClient.getUsdToPlnRate();
        return BigDecimal.valueOf(response.getRates().get(0).getMid());
    }

}
