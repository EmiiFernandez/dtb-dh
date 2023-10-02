package dropthebass.equipo4.exeptions;

//Creando excepción personalizada

//Al extender de Exception ya me trae todos los métodos, las excepciones
public class BadRequestException extends Exception{
                            //Mensaje personalizado
    public BadRequestException(String message) {
        //El super para que podamos usar en otras clases
        //NO return
        super(message);
    }
}
