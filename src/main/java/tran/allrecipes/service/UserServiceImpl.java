package tran.allrecipes.service;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * @author Todd
 * A class to provide logical operations for a user such as operations requiring validation
 * for user related data members concerning credential verification.
 */
@Service
public class UserServiceImpl {
    /** number of logarithmic rounds to apply while hashing a password. */
	private static final int NUMBER_LOG_ROUNDS = 14;
	/** The username. */
    private String userName;
    /** The message to display if the authentication check goes wrong. */
    private String message;
    /** The password. */
    private String password;
    /** A password validation field. */
    private String validatePassword;

    public UserServiceImpl() {}
    
    public UserServiceImpl(String userName) {
		super();
		this.userName = userName;
	}

	public UserServiceImpl(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public UserServiceImpl(String userName, String password, String validatePassword) {
        this.userName = userName;
        this.password = password;
        this.validatePassword = validatePassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getValidatePassword() {
        return validatePassword;
    }

    public void setValidatePassword(String validatePassword) {
        this.validatePassword = validatePassword;
    }

    /** Validates the registration for a user. */
    public boolean validate() {
        String userNameRegex = "^[a-z0-9_-]{6,35}$";
        String passwordRegex = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%])(?!.*\\s).{6,20})";

        if(!userName.matches(userNameRegex)) {
            message = "The user name must be at least 6 characters long and up to 35 characters.\nOnly lower case letters, numbers, an underscore , or hyphen are allowed!";
            return false;
        }

        if(!password.matches(passwordRegex)) {
            message = "The password must have at least one number, one lower and upper case letter, and one of the special symbols: '@', '#', '$', '%'.\nThe length must be between 6 to 20 characters.";
            return false;
        }
        if(!password.equals(validatePassword)) {
            message = "The entered passwords must match, try again!";
            return false;
        }

        return true;
    }

    /** validates for user password change. */
    public boolean validatePasswords() {
        String passwordRegex = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%])(?!.*\\s).{6,20})";

        if(!password.matches(passwordRegex)) {
            message = "The password must have at least one number, one lower and upper case letter, and one of the special symbols: '@', '#', '$', '%'.\nThe length must be between 6 to 20 characters.";
            return false;
        }
        if(!password.equals(validatePassword)) {
            message = "The entered passwords must match, try again!";
            return false;
        }

        return true;
    }

    /**
     * @param entered_user_name The user name to check
     * @return True if the user name passes the required format, false otherwise.
     */
    public boolean checkUserName(String entered_user_name) {
        String userNameRegex = "^[a-z0-9_-]{6,35}$";
        if(!entered_user_name.matches(userNameRegex)) {
            message = "The user name must be 6 to 35 characters.\nOnly lower case letters, numbers, an underscore , or hyphen are allowed!";
            return false;
        }
        return true;
    }

    /**
     * @return True if the user name and password fields meet the required regex requirements.
     */
    public boolean validateLogin() {
        String userNameRegex = "^[a-z0-9_-]{6,35}$";
        String passwordRegex = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%])(?!.*\\s).{6,20})";

        if(!userName.matches(userNameRegex)) {
            message = "The user name must be at least 6 characters long and up to 35 characters.\nOnly lower case letters, numbers, an underscore , or hyphen are allowed!";
            return false;
        }

        if(!password.matches(passwordRegex)) {
            message = "The password must have at least one number, one lower and upper case letter, and one of the special symbols: '@', '#', '$', '%'.\nThe length must be between 6 to 20 characters.";
            return false;
        }

        return true;
    }
    
    /**
     * @param plainTextPassword The plain text password to encrypt
     * @return An encrypted password.
     */
    public String encryptPassword(String plainTextPassword) {
    	return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(NUMBER_LOG_ROUNDS));
    }
    
}