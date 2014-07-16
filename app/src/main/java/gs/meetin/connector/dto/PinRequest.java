package gs.meetin.connector.dto;


public class PinRequest {
    final String email;
    final String include_pin;
    final String allow_register;

    public PinRequest(String email) {
        this.email = email;
        this.include_pin = "1";
        this.allow_register = "1";
    }

    public class PinResult {

    }
}
