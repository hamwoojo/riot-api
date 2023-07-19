package riot.api.data.engineer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.apiresult.ApiResult;
import riot.api.data.engineer.entity.KafkaInfo;
import riot.api.data.engineer.entity.Version;
import riot.api.data.engineer.entity.api.ApiInfo;
import riot.api.data.engineer.entity.champions.Champions;
import riot.api.data.engineer.entity.champions.Data;
import riot.api.data.engineer.service.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/ddragon/champions")
@RequiredArgsConstructor
public class ChampionsController {

    private final ChampionsService championsService;
    private final ApiInfoService apiInfoService;
    private final VersionService versionService;
    private final KafkaInfoService kafkaInfoService;
    private final WebClient webClient;

    @GetMapping("")
    public ResponseEntity<ApiResult> getChampions() {
        try {
            ApiInfo apiInfo = apiInfoService.findOneByName(new Exception().getStackTrace()[0].getMethodName());
            Version version = versionService.findOneByCurrentVersion();
            List<String> pathVariable = championsService.setPathVariableVersion(version);

            String response = championsService.apiCall(webClient, apiInfo, pathVariable);
            Champions champions = championsService.setChampions(response);

            KafkaInfo kafkaInfo = kafkaInfoService.findOneByApiInfoId(apiInfo.getApiInfoId());
            List<Data> dataList = championsService.sendKafkaMessage(kafkaInfo, champions);

            return new ResponseEntity<>(new ApiResult(ApiResult.ApiStatus.OK.getStatusCode(), ApiResult.ApiStatus.OK.getStatus(), dataList), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResult(ApiResult.ApiStatus.INTERNAL_SERVER_ERROR.getStatusCode(),ApiResult.ApiStatus.INTERNAL_SERVER_ERROR.getStatus(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
