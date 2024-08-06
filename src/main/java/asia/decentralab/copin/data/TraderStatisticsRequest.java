package asia.decentralab.copin.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class TraderStatisticsRequest {
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
        private List<Range> ranges;
        private String sortBy;
        private String sortType;

        public RequestDetails(Pagination pagination, List<Query> queries, List<Range> ranges, String sortBy, String sortType) {
            this.pagination = pagination;
            this.queries = queries;
            this.ranges = ranges;
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

    @Getter
    @Setter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Range {
        private String fieldName;
        private Integer gte;
        private Integer lte;
        private Integer lt;
        private Integer gt;

        public Range(String fieldName, Integer gte, Integer lte) {
            this.fieldName = fieldName;
            this.gte = gte;
            this.lte = lte;
        }
    }

    public TraderStatisticsRequest(String baseUrl, String path, String time) {
        this.baseUrl = baseUrl;
        this.apiEndpoints = new ApiEndpoint(
                path, new RequestDetails(
                new Pagination(1000, 0),
                List.of(new Query("type", time)),
                List.of(new Range("realisedPnl", 100, null),
                        new Range("winRate", 51, null)),
                "realisedPnl",
                "desc"));
    }

    public TraderStatisticsRequest(String baseUrl, String path, String time, String sortBy, String sortType) {
        this.baseUrl = baseUrl;
        this.apiEndpoints = new ApiEndpoint(
                path, new RequestDetails(
                new Pagination(2000, 0),
                List.of(new Query("type", time)),
                List.of(new Range("realisedPnl", 100, null),
                        new Range("winRate", 51, null)),
                sortBy,
                sortType));
    }

}
