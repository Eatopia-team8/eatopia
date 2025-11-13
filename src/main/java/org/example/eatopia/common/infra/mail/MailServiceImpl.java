package org.example.eatopia.common.infra.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 실제 메일 발송을 담당하는 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    //보내는사람 이메일주소를 설정파일에서 읽어옴
    @Value("${NAVER_MAIL_USER}")
    private String fromEmail;

    @Override
    public void sendPasswordResetMail(String toEmail, String token) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            //MimeMessageHelper를 사용하여 메일내용과 수신자 설정
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            String mailContent = "<html><body>"
                    + "<h2>비밀번호 재설정 토큰</h2>"
                    + "<p>이 토큰을 Postman의 다음 요청에 사용하세요: /v1/users/password-reset</p>"
                    + "<h3>재설정 토큰: <strong>" + token + "</strong></h3>"
                    + "</body></html>";

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("비밀번호 재설정 토큰 발급");
            helper.setText(mailContent, true);

            javaMailSender.send(mimeMessage);

            log.info("[메일 발송 완료] 수신자: {}", toEmail);
        } catch (MessagingException e) {
            log.error("메일 발송 중 오류 발생: 수신자={}", toEmail, e);
            throw new MailSendException("비밀번호 재설정 메일 발송에 실패했습니다.", e);
        }
    }
}
