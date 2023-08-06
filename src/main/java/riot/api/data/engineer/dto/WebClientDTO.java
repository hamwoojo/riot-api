package riot.api.data.engineer.dto;

import lombok.Getter;
import org.springframework.util.CollectionUtils;
import riot.api.data.engineer.entity.api.ApiInfo;
import riot.api.data.engineer.params.WebClientParams;

import java.util.List;
import java.util.Map;

@Getter
public class WebClientDTO {
    private static final String schemeHttps = "https";
    String scheme;
    String host;
    String path;
    String apiName;
    List<String> pathVariable;
    Map<String,String> queryParam;
    Map<String,String> paging;

    public WebClientDTO(ApiInfo apiInfo){
        this.scheme = apiInfo.getApiScheme();
        this.host = apiInfo.getApiHost();
        this.path = apiInfo.getApiUrl();
    }

    public WebClientDTO setWebClientParams(WebClientDTO webClientDTO, WebClientParams webClientParams){

        boolean pathVariableIsEmpty = pathVariableCheck(webClientParams.getPathVariables());
        if(!pathVariableIsEmpty){
            this.pathVariable = webClientParams.getPathVariables();
        }

        boolean queryParamsIsEmpty = queryParamsCheck(webClientParams.getQueryParams());
        if(!queryParamsIsEmpty){
            this.queryParam = webClientParams.getQueryParams();
        }

        boolean pagingMapIsEmpty = pagingMapCheck(webClientParams.getPagingMap());
        if(!pagingMapIsEmpty){
            this.paging = webClientParams.getPagingMap();
        }
        return webClientDTO;
    }


    private boolean pathVariableCheck(List<String> pathVariable) {
        return CollectionUtils.isEmpty(pathVariable);
    }

    private boolean queryParamsCheck(Map<String, String> queryParams) {
        return CollectionUtils.isEmpty(queryParams);
    }

    private boolean pagingMapCheck(Map<String, String> pagingMap) { return CollectionUtils.isEmpty(pagingMap);}


}
