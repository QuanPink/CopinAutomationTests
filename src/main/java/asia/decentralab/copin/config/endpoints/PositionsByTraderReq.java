package asia.decentralab.copin.config.endpoints;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class PositionsByTraderReq {
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
        private List<Query> queries;
        private String sortBy;
        private String sortType;

        public RequestDetails(Pagination pagination, List<Query> queries, String sortBy, String sortType) {
            this.pagination = pagination;
            this.queries = queries;
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

    @Setter
    @Getter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Query {
        private String fieldName;
        private String value;

        public Query(String fieldName, String value) {
            this.fieldName = fieldName;
            this.value = value;
        }
    }

    public PositionsByTraderReq(String baseUrl, String protocol, String account) {
        this.baseUrl = baseUrl;
        this.apiEndpoints = new ApiEndpoint(
                "/" + protocol + "/position/filter",
                new RequestDetails(
                        new Pagination(3000, 0),
                        List.of(new Query("status", "CLOSE"),
                                new Query("account", account)),
                        "closeBlockTime",
                        "desc"
                )
        );
    }
}
