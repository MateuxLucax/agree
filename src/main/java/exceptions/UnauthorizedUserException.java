package exceptions;

public class UnauthorizedUserException extends Exception {
    public UnauthorizedUserException() { super(); }
    public UnauthorizedUserException(String message) { super(message); }
}
