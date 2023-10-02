package dropthebass.equipo4;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class);

    //Todos los errores vienen a este método
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> todosErrores(Exception e, WebRequest request) {
        //Mensaje que nos de la app
        logger.error(e.getMessage());                               //Nos devuelve por pantalla
        return new ResponseEntity<>("ExceptionHandler Error " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
