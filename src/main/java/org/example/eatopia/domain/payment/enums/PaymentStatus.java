package org.example.eatopia.domain.payment.enums;

public enum PaymentStatus {

    PENDING,  //결제 대기
    CANCELED, //결제 취소
    PARTIALLY_REFUND, //부분 환불
    SUCCESS   //결제 성공
}
