package riot.api.data.engineer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.dto.ApiResultDTO;
import riot.api.data.engineer.dto.WebClientDTO;
import riot.api.data.engineer.dto.api.ApiKey;
import riot.api.data.engineer.entity.KafkaInfo;
import riot.api.data.engineer.entity.Version;
import riot.api.data.engineer.dto.api.ApiInfo;
import riot.api.data.engineer.dto.champions.Champions;
import riot.api.data.engineer.dto.champions.Data;
import riot.api.data.engineer.interfaces.ApiCallHelper;
import riot.api.data.engineer.service.*;

import java.util.Collections;
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
    private final ApiCallHelper apiCallHelper;
    private final WebClient webClient;

    @GetMapping("")
    public ResponseEntity<ApiResultDTO> getChampions() {
        try {
            ApiInfo apiInfo = apiInfoService.findOneByName(new Exception().getStackTrace()[0].getMethodName());
            Version version = versionService.findOneByCurrentVersion();

            List<String> pathVariable = apiCallHelper.setPathVariableVersion(version);
            WebClientDTO webClientDTO = apiCallHelper.getWebClientDTO(apiInfo,pathVariable, Collections.emptyMap());
            ApiKey apiKey = new ApiKey().getEmptyApiKey();
            String response = (String) apiCallHelper.apiCall(webClientDTO,webClient,apiKey,String.class);

            Champions champions = championsService.setChampions(response);

            KafkaInfo kafkaInfo = kafkaInfoService.findOneByApiInfoId(apiInfo.getApiInfoId());
            List<Data> dataList = championsService.sendKafkaMessage(kafkaInfo, champions);

            return new ResponseEntity<>(new ApiResultDTO(ApiResultDTO.ApiStatus.OK.getStatusCode(), ApiResultDTO.ApiStatus.OK.getStatus(), dataList), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResultDTO(ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR.getStatusCode(), ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR.getStatus(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
