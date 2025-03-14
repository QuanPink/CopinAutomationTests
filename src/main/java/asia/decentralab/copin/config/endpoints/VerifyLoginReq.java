package asia.decentralab.copin.config.endpoints;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VerifyLoginReq {
    private String baseUrl;
    private ApiEndpoint apiEndpoints;

    @Setter
    @Getter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ApiEndpoint {
        private String path;
        private RequestDetails requestDetails;

        public ApiEndpoint(String path, RequestDetails requestDetails) {
            this.path = path;
            this.requestDetails = requestDetails;
        }}

        @Setter
        @Getter
        @ToString
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class RequestDetails {
            private String address;
            private String sign;
            private String time;

            public RequestDetails(String address, String sign, String time) {
                this.address = address;
                this.sign = sign;
                this.time = time;
            }
        }

    public VerifyLoginReq(String baseUrl, String address, String sign, String time) {
        this.baseUrl = baseUrl;
        this.apiEndpoints = new ApiEndpoint(
                "/auth/web3/verify-login",
                new VerifyLoginReq.RequestDetails(
                        address,
                        sign,
                        time
                )
        );
    }
}
