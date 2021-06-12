package exceptions;

public class NameAlreadyInUseException extends Exception {
    public NameAlreadyInUseException() { super(); }
    public NameAlreadyInUseException(String msg) { super(msg); }
}
