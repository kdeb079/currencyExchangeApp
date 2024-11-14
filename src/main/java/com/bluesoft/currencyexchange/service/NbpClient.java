package com.bluesoft.currencyexchange.service;

import com.bluesoft.currencyexchange.config.NbpClientConfig;
import com.bluesoft.currencyexchange.dto.CurrencyRateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "nbpClient", url = "http://api.nbp.pl/api/exchangerates", configuration = NbpClientConfig.class)
public interface NbpClient {

    @GetMapping("/rates/A/USD?format=json")
    CurrencyRateResponse getUsdToPlnRate();
}
