package dropthebass.equipo4.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

@Component
public class MessageHTML {

    private ResourceLoader resourceLoader;

    public MessageHTML(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String confirmEmailTemplate() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:/templates/confirm-email.html");
        try (InputStream inputStream = resource.getInputStream()) {
            Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    public String confirmBookingTemplate() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:/templates/confirm-booking.html");
        try (InputStream inputStream = resource.getInputStream()) {
            Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}