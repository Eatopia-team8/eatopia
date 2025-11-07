package org.example.eatopia.domain.settlement.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum BankCode {

    // Portone 공식 은행 코드
    IBK("003", "IBK기업은행"),
    KOOKMIN("004", "KB국민은행"),
    SUHYUP("007", "수협은행"),
    NONGHYUP("011", "NH농협은행"),
    WOORI("020", "우리은행"),
    SC("023", "SC제일은행"),
    CITI("027", "씨티은행"),
    DAEGU("031", "대구은행"),
    BUSAN("032", "부산은행"),
    KYUNGNAM("039", "경남은행"),
    HANA("081", "하나은행"),
    SHINHAN("088", "신한은행"),
    K_BANK("089", "케이뱅크"),
    KAKAO_BANK("090", "카카오뱅크"),
    TOSS_BANK("092", "토스뱅크");

    private final String code;
    private final String description;

    @JsonCreator
    public static BankCode from(String codeOrName) {
        return Stream.of(BankCode.values())
                .filter(b -> b.name().equalsIgnoreCase(codeOrName) || b.getCode().equals(codeOrName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 은행 코드입니다: " + codeOrName));
    }
}