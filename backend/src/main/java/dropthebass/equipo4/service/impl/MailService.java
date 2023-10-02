package dropthebass.equipo4.service.impl;

import dropthebass.equipo4.dto.DetailBookingDTO;
import dropthebass.equipo4.entity.DetailBooking;
import dropthebass.equipo4.infra.MailManager;
import dropthebass.equipo4.utils.MessageHTML;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class MailService {

    private final MailManager mailManager;
    private final MessageHTML messageHTML;

    @Autowired
    public MailService(MailManager mailManager, MessageHTML messageHTML) {
        this.mailManager = mailManager;
        this.messageHTML = messageHTML;
    }

    /**
     * Envía un correo de confirmación al usuario.
     *
     * @param email La dirección de correo electrónico del destinatario.
     * @param name  El nombre del usuario.
     * @throws MessagingException Si ocurre un error durante el envío del correo.
     * @throws IOException        Si ocurre un error al cargar la plantilla HTML.
     */
    public void sendMessageUser(String email, String name) throws MessagingException, IOException {
        try {
            // Obtiene el contenido de la plantilla HTML para el correo de confirmación
            String htmlContent = messageHTML.confirmEmailTemplate();

            // Reemplaza las variables de la plantilla con los datos del usuario
            String finalMessage = htmlContent
                    .replace("[Correo electrónico del usuario]", email)
                    .replace("[Enlace para continuar]", "https://frontend-grupo4-integradora.vercel.app/login")
                    .replace("[Nombre del usuario]", name);

            // Envía el correo
            mailManager.sendMessage(email, finalMessage);
        } catch (MessagingException e) {
            // Relanza la excepción de MessagingException con un mensaje descriptivo
            throw new MessagingException("Error al intentar enviar el correo de confirmación.", e);
        } catch (IOException e) {
            // Relanza la excepción de IOException con un mensaje descriptivo
            throw new IOException("Error al cargar la plantilla HTML para el correo de confirmación.", e);
        }
    }

    public void sendMessageBooking(String emailBooking, List<DetailBooking> detailBookings) throws MessagingException, IOException {
        try {
            String htmlContent = messageHTML.confirmBookingTemplate();

            // Generar el contenido de la tabla de detalles de reserva
            StringBuilder detailRows = new StringBuilder();
            for (DetailBooking detailBooking : detailBookings) {
                String formattedStartDate = formatDate(detailBooking.getStartBooking());
                String formattedEndDate = formatDate(detailBooking.getEndBooking());

                String row = "<tr>" +
                        "<td>" + detailBooking.getProductName() + "</td>" +
                        "<td>" + detailBooking.getProductBrand() + "</td>" +
                        "<td>$" + detailBooking.getProductPrice() + "</td>" +
                        "<td>" + formattedStartDate + "</td>" +
                        "<td>" + formattedEndDate + "</td>" +
                        "</tr>";
                detailRows.append(row);
            }

            // Reemplazar el marcador de posición en la plantilla HTML
            String finalMessage = htmlContent
                    .replace("[Nombre usuario]", detailBookings.get(0).getUser().getName())
                    .replace("[Codigo reserva]", detailBookings.get(0).getBooking().getBookingCode())
                    .replace("[Detalles de reserva]", detailRows.toString())
                    .replace("[Costo total]", String.valueOf(detailBookings.get(0).getBooking().getTotalCost())); // Cambia a String.valueOf

            // Enviar el correo
            mailManager.sendMessageBooking(emailBooking, finalMessage);

        } catch (IOException e) {
            // Relanza la excepción de IOException con un mensaje descriptivo
            throw new IOException("Error al cargar la plantilla HTML para el correo de reserva.", e);
        }
    }

    public String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }
}
