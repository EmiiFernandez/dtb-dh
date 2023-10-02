package dropthebass.equipo4.utils;

import dropthebass.equipo4.repository.IBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class BookingCodeGenerator {

    private final IBookingRepository bookingRepository;

    @Autowired
    public BookingCodeGenerator(IBookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 8; // Longitud del código de reserva

    private static final Random RANDOM = new SecureRandom();

    public String generateUniqueReservationCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        // Paso 1: Generar un código de reserva aleatorio de la longitud especificada
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }

        // Paso 2: Verificar si el código generado ya existe en la base de datos
        // Si existe, genera otro código único hasta obtener uno que no esté en uso

        // En este ejemplo, asumimos que tienes un método existsByCodigoReserva en tu repositorio
        // para verificar la existencia del código en la base de datos. Debes adaptarlo a tu
        // estructura de datos y repositorio específicos.

        while (bookingRepository.existsByBookingCode(code.toString())) {
            code.setLength(0); // Limpiar el código generado
            for (int i = 0; i < CODE_LENGTH; i++) {
                int randomIndex = RANDOM.nextInt(CHARACTERS.length());
                code.append(CHARACTERS.charAt(randomIndex));
            }
        }

        // Paso 3: Devolver el código de reserva único
        return code.toString();
    }
}
