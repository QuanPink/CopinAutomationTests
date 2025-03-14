package asia.decentralab.copin.test.apiTest;

import asia.decentralab.copin.config.BaseUrlConfig;
import asia.decentralab.copin.config.endpoints.LoginReq;
import asia.decentralab.copin.config.endpoints.VerifyLoginReq;
import asia.decentralab.copin.model.VerifyCode;
import asia.decentralab.copin.utils.APIUtils;
import asia.decentralab.copin.utils.JsonUtils;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class LoginSite {
    private static final String PRIVATE_KEY = "d6d7a006ec8e4c64bd3a4952ac79c1ffd415f5bfbe1ec1c918687b609031b151";

    @Test
    public void Login() {
        LoginReq requestPayload = new LoginReq(BaseUrlConfig.DEV_BASE_URL, BaseUrlConfig.ACCOUNT);

        Response response = APIUtils.sendPostRequest(
                requestPayload.getBaseUrl(),
                requestPayload.getApiEndpoints().getPath(),
                requestPayload.getApiEndpoints().getRequestDetails());

        VerifyCode verifyCode = JsonUtils.fromJson(response.getBody().asString(), VerifyCode.class);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);
        String timeNow = formatter.format(Instant.now());

        String message = String.format("I want to login on Copin.io at %s. Login code: %s",
                timeNow, verifyCode.getVerifyCode());

        String signature = signMessage(PRIVATE_KEY, message);
        if (signature != null) {
            System.out.println("Signature: " + signature);
        } else {
            System.out.println("An error occurred during signing.");
        }

        String timeNow1 = formatter.format(Instant.now());

        VerifyLoginReq verifyLoginReq = new VerifyLoginReq(
                BaseUrlConfig.DEV_BASE_URL, BaseUrlConfig.ACCOUNT, signature, timeNow1);

        Response responseA = APIUtils.sendPostRequest(
                verifyLoginReq.getBaseUrl(),
                verifyLoginReq.getApiEndpoints().getPath(),
                verifyLoginReq.getApiEndpoints().getRequestDetails());

        System.out.println(responseA.getBody().asString());
    }

    public static String signMessage(String privateKey, String message) {
        try {
            Credentials credentials = Credentials.create(privateKey);

            byte[] messageHash = MessageDigest.getInstance("SHA3-256").digest(message.getBytes(StandardCharsets.UTF_8));

            Sign.SignatureData signatureData = Sign.signMessage(messageHash, credentials.getEcKeyPair());

            return Numeric.toHexString(signatureData.getR())
                    + Numeric.toHexString(signatureData.getS()).substring(2)
                    + Numeric.toHexString(new byte[]{signatureData.getV()[0]}).substring(2);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
