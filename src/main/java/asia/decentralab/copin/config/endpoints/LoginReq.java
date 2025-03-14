package asia.decentralab.copin.config.endpoints;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class LoginReq {
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
        }
    }

    @Setter
    @Getter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RequestDetails {
        private String address;

        public RequestDetails(String address) {
            this.address = address;
        }
    }

    public LoginReq(String baseUrl, String account) {
        this.baseUrl = baseUrl;
        this.apiEndpoints = new ApiEndpoint(
                "/auth/web3/login",
                new RequestDetails(
                        account
                )
        );
    }
}
