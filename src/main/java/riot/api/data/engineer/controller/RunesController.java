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
import riot.api.data.engineer.entity.KafkaInfo;
import riot.api.data.engineer.entity.Version;
import riot.api.data.engineer.dto.api.ApiInfo;
import riot.api.data.engineer.dto.runes.Rune;
import riot.api.data.engineer.dto.runes.RuneList;
import riot.api.data.engineer.service.ApiInfoService;
import riot.api.data.engineer.service.KafkaInfoService;
import riot.api.data.engineer.service.RuneService;
import riot.api.data.engineer.service.VersionService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/ddragon/runes")
@RequiredArgsConstructor
public class RunesController {

    private final ApiInfoService apiInfoService;
    private final VersionService versionService;
    private final KafkaInfoService kafkaInfoService;
    private final RuneService runeService;
    private final WebClient webClient;

    @GetMapping("")
    public ResponseEntity<ApiResultDTO> getRunes() {
        try {
            ApiInfo apiInfo = apiInfoService.findOneByName(new Exception().getStackTrace()[0].getMethodName());
            Version version = versionService.findOneByCurrentVersion();
            KafkaInfo kafkaInfo = kafkaInfoService.findOneByApiInfoId(apiInfo.getApiInfoId());
            List<String> pathVariable = runeService.setPathVariableVersion(version);
            String response = runeService.apiCall(webClient, apiInfo, pathVariable);
            RuneList runeList = runeService.setRuneList(response);
            List<Rune> runes = runeService.sendKafkaMessage(kafkaInfo, runeList, version);

            return new ResponseEntity<>(new ApiResultDTO(200, "success", runes), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResultDTO(500, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
