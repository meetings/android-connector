package gs.meetin.connector.dto;

public class LoginRequest {

    public String email;
    public String pin;

    public LoginRequest(String email, String pin) {
        this.email = email;
        this.pin = pin;
    }

    public class LoginResult {
        public User result;
        public ApiError error;
    }

    public class User {
        public String user_id;
        public String token;
    }


}
