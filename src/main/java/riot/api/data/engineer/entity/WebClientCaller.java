package riot.api.data.engineer.entity;

import lombok.Builder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import riot.api.data.engineer.dto.WebClientDTO;
import riot.api.data.engineer.dto.api.ApiKey;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Builder
public class WebClientCaller {
    private static final String PAGE = "page";

    private WebClient webclient;
    private WebClientDTO webClientDTO;

    public Object getWebClientData(ApiKey apiKey, Class<?> responseType) {
        UriBuilder builder = createUriBuilder();
        URI uri = createURI(builder, webClientDTO.getPathVariable());
        WebClient.RequestBodySpec request = (WebClient.RequestBodySpec) createHeader(uri);
        boolean apiKeyIsEmpty = apiKeyNullCheck(apiKey);
        if(!apiKeyIsEmpty){
            request = request.header(apiKey.getKeyName(),apiKey.getApiKey());
        }
        return request.retrieve().bodyToMono(responseType).block();
    }

    private boolean apiKeyNullCheck(ApiKey apiKey) {
        return apiKey.getApiKey().isEmpty() || apiKey.getKeyName().isEmpty();
    }

    private URI createURI(UriBuilder uriBuilder, List<String> pathVariable){
        if(CollectionUtils.isNotEmpty(pathVariable)){
            return uriBuilder.build(pathVariable.toArray(new String[0]));
        }else{
            return uriBuilder.build();
        }
    }

    private WebClient.RequestHeadersSpec<?> createHeader(URI uri){
            return webclient.get().uri(uri);
    }

    private UriBuilder createUriBuilder(){

        UriBuilder uriBuilder = getUriBuilder();

        Optional<Map<String,String>> queryParams = Optional.ofNullable(webClientDTO.getQueryParam());
        Optional<Map<String,String>> paging = Optional.ofNullable(webClientDTO.getPaging());

        if(queryParams.isPresent()){
            for(Map.Entry<String, String> entry : queryParams.get().entrySet()){
                uriBuilder.queryParam(entry.getKey(), entry.getValue());
            }
        }
        paging.ifPresent(stringStringMap -> uriBuilder.queryParamIfPresent(PAGE, Optional.ofNullable(stringStringMap.get(PAGE))).build());
        return uriBuilder;
    }

    private UriBuilder getUriBuilder(){
        return UriComponentsBuilder.newInstance()
                .scheme(webClientDTO.getScheme())
                .host(webClientDTO.getHost())
                .path(webClientDTO.getPath());
    }
}
