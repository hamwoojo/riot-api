package riot.api.data.engineer.interfaces;

import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.dto.WebClientDTO;
import riot.api.data.engineer.dto.api.ApiInfo;
import riot.api.data.engineer.dto.api.ApiKey;
import riot.api.data.engineer.entity.Version;
import riot.api.data.engineer.entity.WebClientCaller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApiCallHelperImpl implements ApiCallHelper{
    @Override
    public WebClientDTO builderWebClientDTO(ApiInfo apiInfo, List<String> pathVariable, Map<String, String> queryParams){
        WebClientDTO.WebClientDTOBuilder builder = WebClientDTO.builder().
                scheme(apiInfo.getApiScheme())
                .host(apiInfo.getApiHost())
                .path(apiInfo.getApiUrl());

        boolean pathVariableIsEmpty = pathVariableCheck(pathVariable);
        if(!pathVariableIsEmpty){
            builder.pathVariable(pathVariable);
        }

        boolean queryParamsIsEmpty = queryParamsCheck(queryParams);
        if(!queryParamsIsEmpty){
            builder.queryParam(queryParams);
        }

        return builder.build();
    }

    @Override
    public List<String> setPathVariableVersion(Version version) {
        List<String> pathVariable = new ArrayList<>();
        pathVariable.add(version.getVersion());
        return pathVariable;
    }

    @Override
    public Object apiCall(WebClientDTO webClientDTO,WebClient webClient, ApiKey apiKey, Class<?> responseType) {

        return WebClientCaller.builder()
                .webClientDTO(webClientDTO)
                .webclient(webClient)
                .build().getWebClientData(apiKey,responseType);
    }

    private boolean pathVariableCheck(List<String> pathVariable) {
        return CollectionUtils.isEmpty(pathVariable);
    }

    private boolean queryParamsCheck(Map<String, String> queryParams) {
        return CollectionUtils.isEmpty(queryParams);
    }
}

