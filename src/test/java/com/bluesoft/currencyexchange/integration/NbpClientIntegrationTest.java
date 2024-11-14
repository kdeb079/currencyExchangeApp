package com.bluesoft.currencyexchange.integration;

import com.bluesoft.currencyexchange.dto.CurrencyRateResponse;
import com.bluesoft.currencyexchange.service.NbpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class NbpClientIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.2")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private NbpClient nbpClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.properties.hibernate.id.new_generator_mappings", () -> true);
    }

    @Test
    void shouldFetchUsdToPlnRateWithinTolerance() {
        // Pobieranie kursu USD-PLN
        CurrencyRateResponse response = nbpClient.getUsdToPlnRate();
        assertThat(response).isNotNull();

        // Ustal wartość odniesienia i tolerancję
        BigDecimal baseRate = new BigDecimal("4.00"); // Zakładana wartość startowa
        BigDecimal tolerance = baseRate.multiply(BigDecimal.valueOf(0.15)); // 15% tolerancji

        // Pobieranie faktycznej wartości kursu i sprawdzenie tolerancji
        BigDecimal actualRate = BigDecimal.valueOf(response.getRates().get(0).getMid());
        assertThat(actualRate).isBetween(baseRate.subtract(tolerance), baseRate.add(tolerance));
    }
}
