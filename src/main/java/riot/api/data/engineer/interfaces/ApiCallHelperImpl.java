package riot.api.data.engineer.interfaces;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.dto.WebClientDTO;
import riot.api.data.engineer.dto.api.ApiKey;
import riot.api.data.engineer.entity.Version;
import riot.api.data.engineer.entity.WebClientCaller;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApiCallHelperImpl implements ApiCallHelper {

    @Override
    public List<String> setPathVariableVersion(Version version) {
        List<String> pathVariable = new ArrayList<>();
        pathVariable.add(version.getVersion());
        return pathVariable;
    }

    @Override
    public Object apiCall(WebClientDTO webClientDTO, WebClient webClient, ApiKey apiKey, Class<?> responseType) {

        return WebClientCaller.builder()
                .webClientDTO(webClientDTO)
                .webclient(webClient)
                .build().getWebClientData(apiKey, responseType);
    }


}

