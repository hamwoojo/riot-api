package riot.api.data.engineer.interfaces;

import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.dto.WebClientDTO;
import riot.api.data.engineer.dto.api.ApiInfo;
import riot.api.data.engineer.dto.api.ApiKey;
import riot.api.data.engineer.entity.Version;

import java.util.List;
import java.util.Map;

public interface ApiCallHelper {
    WebClientDTO getWebClientDTO(ApiInfo apiInfo, List<String> pathVariable, Map<String, String> queryParams);

    List<String> setPathVariableVersion(Version version);

    Object apiCall(WebClientDTO webClientDTO, WebClient webClient, ApiKey apiKey, Class<?> responseType);


}
