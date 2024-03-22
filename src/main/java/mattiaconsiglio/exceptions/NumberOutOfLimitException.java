package mattiaconsiglio.exceptions;

public class NumberOutOfLimitException extends Exception {
    public NumberOutOfLimitException(int min, int max) {
        super("The number is out of the min and max value: " + min + " and " + max);
    }
}
