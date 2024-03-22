package mattiaconsiglio.exceptions;

public class RecordNotFoundException extends Exception {
    public RecordNotFoundException(String recodType, long message) {
        super(recodType + " " + message + " not found!");
    }
}
