package org.example.eatopia.common.core.consts;

public abstract class Const {

    public static final String LOGIN_USER = "LOGIN_USER";

    // 비밀번호 재설정 토큰 만료 시간 (초 단위)
    public static final int RESET_TOKEN_EXPIRATION_SECONDS = 60;

    // 비밀번호 재설정 토큰 재발급 쿨다운 시간 (분 단위)
    public static final int RE_ISSUE_COOL_DOWN_MINUTES = 5;

    // Coupon
    public static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final int CODE_LENGTH = 8;
    public static final java.security.SecureRandom RANDOM = new java.security.SecureRandom();

}
