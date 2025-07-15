package com.lineinc.erp.api.server.common.util;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailUtils {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String username, String newPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("[라인ERP] 비밀번호 초기화 안내");
            helper.setText("""
                    <div style="font-family: sans-serif; font-size: 14px;">
                        <p>안녕하세요 <strong>%s</strong> 님,</p>
                    
                        <p>요청하신 비밀번호 초기화가 완료되었습니다.<br>
                        아래 임시 비밀번호로 로그인해주세요:</p>
                    
                        <p><strong>임시 비밀번호:</strong> %s</p>
                    
                        <p>로그인 후 반드시 비밀번호를 변경해주시기 바랍니다.</p>
                    
                        <p>감사합니다.</p>
                    </div>
                    """.formatted(username, newPassword), true);  // true = HTML로 전송

            mailSender.send(message);
            log.info("📧 비밀번호 초기화 메일 전송 성공: to={}", to);
        } catch (MessagingException e) {
            log.error("❌ 비밀번호 초기화 메일 전송 실패: to={}, error={}", to, e.getMessage());
            throw new RuntimeException(ValidationMessages.EMAIL_SEND_FAILURE);
        }
    }
}