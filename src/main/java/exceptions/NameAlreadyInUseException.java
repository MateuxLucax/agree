package exceptions;

public class NameAlreadyInUseException extends Exception {
    private String name;

    public NameAlreadyInUseException(String name) {
        super();
        this.name = name;
    }

    public String getName() { return name; }
}
