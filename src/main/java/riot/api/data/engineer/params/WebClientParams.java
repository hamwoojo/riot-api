package riot.api.data.engineer.params;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class WebClientParams {
    private List<String> pathVariables;
    private Map<String, String> queryParams;
    private Map<String, String> pagingMap;

    public static class Builder {
        private List<String> pathVariables;
        private Map<String, String> queryParams;
        private Map<String, String> pagingMap;

        public Builder pathVariables(List<String> pathVariables) {
            this.pathVariables = pathVariables;
            return this;
        }

        public Builder queryParams(Map<String, String> queryParams) {
            this.queryParams = queryParams;
            return this;
        }

        public Builder pagingMap(Map<String, String> pagingMap) {
            this.pagingMap = pagingMap;
            return this;
        }

        public WebClientParams build() {
            WebClientParams params = new WebClientParams();
            params.pathVariables = this.pathVariables;
            params.queryParams = this.queryParams;
            params.pagingMap = this.pagingMap;
            return params;
        }
    }
}
