package asia.decentralab.copin.constants;

public class ApiEndpoints {
    // Authentication Flow endpoints
    public static final String SEND_OTP = "https://auth.privy.io/api/v1/passwordless/init";
    public static final String VERIFY_OTP = "https://auth.privy.io/api/v1/passwordless/authenticate";
    public static final String LOGIN = "/auth/privy/login";
}
