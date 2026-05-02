package bg.sofia.uni.fmi.issuetracker.utils;

import bg.sofia.uni.fmi.issuetracker.model.auth.Token;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
public class EmailUtils {
    @Value("${services.email.default_sender}")
    private String sender;

    private final JavaMailSender javaMailSender;
    private final ExecutorService emailSenderThreadPool;

    public EmailUtils(JavaMailSender javaMailSender, ExecutorService emailSenderThreadPool) {
        this.javaMailSender = javaMailSender;
        this.emailSenderThreadPool = emailSenderThreadPool;
    }

    public void sendForgotPasswordEmail(String receiver, String redirectUrl, Token token) {
        CompletableFuture.supplyAsync(() -> {
            sendForgotPasswordEmailInternal(receiver, redirectUrl, token);
            return "";
        }, emailSenderThreadPool).exceptionally(ex -> {
            throw new RuntimeException(ex);
        });
    }

    void sendForgotPasswordEmailInternal(String receiver, String redirectUrl, Token token) {
        String fullRedirectUrl = redirectUrl + "?token=" + token.getTokenValue();

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(sender);
            helper.setTo(receiver);
            helper.setSubject(Constants.FORGOT_PASSWORD_EMAIL_SUBJECT);
            helper.setText("""
                    <p>Forgot password: <a href="%s">here</a></p>
                    """.formatted(fullRedirectUrl), true);
            javaMailSender.send(message);
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
