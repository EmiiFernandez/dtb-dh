package dropthebass.equipo4.infra;

import dropthebass.equipo4.entity.DetailBooking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class MailManager {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    public MailManager(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Envía un correo electrónico.
     *
     * @param email        La dirección de correo electrónico del destinatario.
     * @param messageEmail El contenido del correo electrónico.
     * @throws MessagingException Si ocurre un error durante el envío del correo.
     */
    public void sendMessage(String email, String messageEmail) throws MessagingException {
        // Crea un objeto MimeMessage para permitir adjuntos (imágenes, videos, etc.)
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            // Configura el asunto del correo
            message.setSubject("Drop The Bass | ¡Gracias por registrarte!");

            // Crea un helper MimeMessageHelper para simplificar la construcción del correo
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Configura el destinatario y el contenido del correo
            helper.setTo(email);
            helper.setText(messageEmail, true);

            // Configura el remitente del correo
            helper.setFrom(sender);

            // Envía el correo
            javaMailSender.send(message);

        } catch (MessagingException e) {
            // Captura la excepción de MessagingException y la relanza como RuntimeException
            throw new RuntimeException("Error al intentar enviar el correo electrónico.", e);
        }
    }

    public void sendMessageBooking(String emailBooking, String messageBooking) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {

          //  String emailBooking = detailBooking.getUser().getEmail();

            mimeMessage.setSubject("Confirmación de reserva | Drop The Bass");
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(emailBooking);
            helper.setText(messageBooking, true);
            helper.setFrom(sender);
            javaMailSender.send(mimeMessage);

        }  catch (MessagingException e) {
            throw new RuntimeException("Error al intentar enviar el correo electrónico: " + e.getMessage());
        }

    }
}
