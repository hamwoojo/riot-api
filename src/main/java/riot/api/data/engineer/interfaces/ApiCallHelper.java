package riot.api.data.engineer.interfaces;

import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.dto.WebClientDTO;
import riot.api.data.engineer.entity.api.ApiKey;
import riot.api.data.engineer.entity.Version;

import java.util.List;

public interface ApiCallHelper {

    List<String> setPathVariableVersion(Version version);

    Object apiCall(WebClientDTO webClientDTO, WebClient webClient, ApiKey apiKey, Class<?> responseType);


}
