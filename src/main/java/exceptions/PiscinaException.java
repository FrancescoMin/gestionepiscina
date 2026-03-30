package exceptions;

// Estendiamo Exception per creare la nostra eccezione personalizzata
public class PiscinaException extends Exception {

    public PiscinaException(String message) {
        super(message);
    }

    // Puoi anche aggiungere costruttori per loggare l'errore originale, se vuoi
    public PiscinaException(String message, Throwable cause) {
        super(message, cause);
    }
}