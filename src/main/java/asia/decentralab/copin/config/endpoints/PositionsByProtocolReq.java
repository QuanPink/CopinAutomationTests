package asia.decentralab.copin.config.endpoints;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PositionsByProtocolReq {
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
        private Pagination pagination;
        private String sortBy;
        private String sortType;

        public RequestDetails(Pagination pagination, String sortBy, String sortType) {
            this.pagination = pagination;
            this.sortBy = sortBy;
            this.sortType = sortType;
        }
    }

    @Setter
    @Getter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Pagination {
        private int limit;
        private int offset;

        public Pagination(int limit, int offset) {
            this.limit = limit;
            this.offset = offset;
        }
    }

    public PositionsByProtocolReq(String baseUrl, String protocol) {
        this.baseUrl = baseUrl;
        this.apiEndpoints = new ApiEndpoint(
                "/" + protocol + "/position/filter",
                new RequestDetails(
                        new Pagination(100, 0),
                        "openBlockTime",
                        "desc"
                )
        );
    }
}