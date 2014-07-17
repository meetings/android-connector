package gs.meetin.connector.dto;

public class LoginRequest {

    // Repsonse
    public User result;
    public ApiError error;

    // Request
    private String email;
    private String pin;

    public LoginRequest(String email, String pin) {
        this.email = email;
        this.pin = pin;
    }

    public class User {
        public String user_id;
        public String token;
    }
}
