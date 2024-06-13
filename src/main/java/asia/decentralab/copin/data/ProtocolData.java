package asia.decentralab.copin.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Setter
@Getter
@ToString
public class ProtocolData {
    private String url;
    private String method;
    private Map<String, Object> header;
    private RequestBody body;

    @Getter
    @Setter
    @ToString
    public static class RequestBody {
        private Pagination pagination;
        private Query[] queries;
        private Range[] ranges;
        private String sortBy;
        private String sortType;
    }

    @Getter
    @Setter
    @ToString
    public static class Pagination {
        private int limit;
        private int offset;
    }

    @Getter
    @Setter
    @ToString
    public static class Query {
        private String fieldName;
        private String value;
    }

    @Getter
    @Setter
    @ToString
    public static class Range {
        private String fieldName;
        private int gte;
    }
}
