package org.example.eatopia.common.infra.mail;

public interface MailService {
    
    //지정된 이메일 주소로 비밀번호 재설정 토큰을 포함한 메일을 발송
    void sendPasswordResetMail(String toEmail, String token);
}
