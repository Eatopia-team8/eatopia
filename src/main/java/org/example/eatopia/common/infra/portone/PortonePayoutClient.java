package org.example.eatopia.common.infra.portone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class PortonePayoutClient {

    private static final String API_BASE_URL = "https://api.sandbox.iamport.kr";
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${portone.api-key}")
    private String apiKey;
    @Value("${portone.api-secret}")
    private String apiSecret;

    public String requestPayout(String merchantPayoutUid, BigDecimal amount, String bankCode, String bankAccount, String bankHolder) {
        String token = getAccessToken();

        // [수정] 운영 URL -> 테스트 URL
        String url = API_BASE_URL + "/payouts";

        Map<String, Object> body = new HashMap<>();
        body.put("merchant_uid", merchantPayoutUid);
        body.put("amount", amount);
        body.put("bank_code", bankCode);
        body.put("bank_account", bankAccount);
        body.put("bank_holder", bankHolder);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> res = (Map<String, Object>) response.getBody().get("response");
            return (String) res.get("imp_uid");
        } else {
            throw new RuntimeException("Payout 요청 실패: " + response.getStatusCode());
        }
    }

    private String getAccessToken() {
        String url = API_BASE_URL + "/users/getToken";

        Map<String, String> body = new HashMap<>();
        body.put("imp_key", apiKey);
        body.put("imp_secret", apiSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("response");
            return (String) data.get("access_token");
        } else {
            throw new RuntimeException("Access Token 발급 실패");
        }
    }
}