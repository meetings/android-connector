package gs.meetin.connector.dto;

public class LoginRequest {

    // Repsonse
    public User result;
    public ApiError error;

    // Request
    private String email;
    private String pin;
    private String deviceType;

    public LoginRequest(String email, String pin) {
        this.email = email;
        this.pin = pin;
        this.deviceType = "android connector";
    }

    public class User {
        public String userId;
        public String token;
    }
}
