package riot.api.data.engineer.params;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class WebClientParams {
    private List<String> pathVariables;
    private Map<String, String> queryParams;
    private Map<String, String> pagingMap;

}
