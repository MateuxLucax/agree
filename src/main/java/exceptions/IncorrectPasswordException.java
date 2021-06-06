package exceptions;

// Could also store information about what specific password requirement
// wasn't satisfied -- was it too short? does it need numbers? etc.
// But that's too much for our purposes.
public class IncorrectPasswordException extends Exception {
    private String username;

    public IncorrectPasswordException(String username) {
        super();
        this.username = username;
    }

    // This is to answer the question "which user is this not the password of?"
    public String getUsername() { return username; }
}
