package org.example.eatopia.common.infra.portone;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.infra.portone.dto.request.PortonePayoutRequest;
import org.example.eatopia.common.infra.portone.dto.request.PortoneTokenRequest;
import org.example.eatopia.common.infra.portone.dto.response.PortonePayoutResponse;
import org.example.eatopia.common.infra.portone.dto.response.PortoneTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PortonePayoutClient {

    private static final String API_BASE_URL = "https://api.sandbox.iamport.kr";
    private final RestTemplate restTemplate;
    @Value("${portone.api-key}")
    private String apiKey;
    @Value("${portone.api-secret}")
    private String apiSecret;

    public String requestPayout(String merchantPayoutUid, BigDecimal amount, String bankCode, String bankAccount, String bankHolder) {
        String token = getAccessToken();

        String url = API_BASE_URL + "/payouts";

        PortonePayoutRequest body = new PortonePayoutRequest(
                merchantPayoutUid,
                amount,
                bankCode,
                bankAccount,
                bankHolder
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<PortonePayoutRequest> entity = new HttpEntity<>(body, headers);

        ResponseEntity<PortonePayoutResponse> response = restTemplate.postForEntity(
                url, entity, PortonePayoutResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            PortonePayoutResponse payoutResponse = response.getBody();
            if (payoutResponse.code() == 0 && payoutResponse.response() != null) {
                return payoutResponse.response().impUid();
            } else {
                throw new RuntimeException("Payout API 응답 오류: " + payoutResponse.message());
            }
        } else {
            throw new RuntimeException("Payout 요청 실패: " + response.getStatusCode());
        }
    }

    private String getAccessToken() {
        String url = API_BASE_URL + "/users/getToken";

        PortoneTokenRequest body = new PortoneTokenRequest(apiKey, apiSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PortoneTokenRequest> entity = new HttpEntity<>(body, headers);

        ResponseEntity<PortoneTokenResponse> response = restTemplate.postForEntity(
                url, entity, PortoneTokenResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            PortoneTokenResponse tokenResponse = response.getBody();
            if (tokenResponse.code() == 0 && tokenResponse.response() != null) {
                return tokenResponse.response().accessToken();
            } else {
                throw new RuntimeException("Access Token API 응답 오류: " + tokenResponse.message());
            }
        } else {
            throw new RuntimeException("Access Token 발급 실패");
        }
    }
}