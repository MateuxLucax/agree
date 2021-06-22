package services.login;

import exceptions.NameAlreadyInUseException;
import exceptions.UnauthorizedUserException;
import exceptions.UnsafePasswordException;
import models.User;
import repositories.user.IUserRepository;
import repositories.user.UserRepositoryInFile;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class LoginService implements ILoginService {

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String SALT = "kaB6tokCjL$bUaHr";
    private final IUserRepository repository;

    public LoginService() {
        this.repository = new UserRepositoryInFile();
    }

    @Override
    public User authenticate(String name, String password) throws UnauthorizedUserException {
        User user = this.repository.getUser(name, generateSecurePassword(password));
        if (user == null) {
            throw new UnauthorizedUserException("User unauthorized.");
        }

        return user;
    }

    @Override
    public boolean createUser(User user) throws NameAlreadyInUseException, UnsafePasswordException {
        if (repository.userExists(user.getNickname())) {
            throw new NameAlreadyInUseException();
        }

        // boolean passwordIsSafe = password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\\\S+$).{8,}");
        boolean passwordIsSafe = user.getPassword().length() > 2;
        if (!passwordIsSafe) {
            throw new UnsafePasswordException();
        }

        user.setPassword(generateSecurePassword(user.getPassword()));
        return this.repository.storeUser(user);
    }

    @Override
    public boolean storeUser(User user) {
        return this.repository.storeUser(user);
    }

    public static byte[] hash(char[] password) {
        PBEKeySpec spec = new PBEKeySpec(password, SALT.getBytes(StandardCharsets.UTF_8), ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return secretKeyFactory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    public static String generateSecurePassword(String password) {
        byte[] securePassword = hash(password.toCharArray());

        return Base64.getEncoder().encodeToString(securePassword);
    }

    public static boolean verifyUserPassword(String providedPassword, String securedPassword) {
        return generateSecurePassword(providedPassword).equals(securedPassword);
    }
}
