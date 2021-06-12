package exceptions;

public class UnauthorizedUserException extends Exception {
    private final String name;

    public UnauthorizedUserException(String name) {
        super();
        this.name = name;
    }

    public String getName() { return name; }
}
