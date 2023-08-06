package riot.api.data.engineer.serviceimpl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.dto.WebClientDTO;
import riot.api.data.engineer.entity.api.ApiInfo;
import riot.api.data.engineer.entity.api.ApiKey;
import riot.api.data.engineer.dto.runes.Rune;
import riot.api.data.engineer.entity.KafkaInfo;
import riot.api.data.engineer.entity.MyProducer;
import riot.api.data.engineer.entity.Version;
import riot.api.data.engineer.interfaces.ApiCallHelper;
import riot.api.data.engineer.params.WebClientParams;
import riot.api.data.engineer.service.ApiInfoService;
import riot.api.data.engineer.service.RuneService;
import riot.api.data.engineer.service.VersionService;
import riot.api.data.engineer.utils.UtilManager;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RuneServiceImpl implements RuneService {
    private final MyProducer myProducer;
    private final ApiInfoService apiInfoService;
    private final VersionService versionService;
    private final WebClient webClient;
    private final ApiCallHelper apiCallHelper;

    private List<Rune> setRuneList(String response) {
        Gson gson = new Gson();

        List<Rune> runes = new ArrayList<>();

        JsonArray jsonArray = UtilManager.StringToJsonArray(response);
        jsonArray.forEach(jsonElement -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Rune rune = gson.fromJson(jsonObject, Rune.class);
            rune.getSlots().forEach(slot -> slot.getRunes().forEach(runeDetail -> {
                runeDetail.setShortDesc(runeDetail.getShortDesc().replaceAll("<[^>]+>", ""));
                runeDetail.setLongDesc(runeDetail.getLongDesc().replaceAll("<[^>]+>", ""));
            }));
            runes.add(rune);
        });
        return runes;
    }

    public List<Rune> sendKafkaMessage(KafkaInfo kafkaInfo, List<Rune> runeList, Version version) {
        Gson gson = new Gson();
        runeList.forEach(rune -> {
            rune.setVersion(version.getVersion().replaceAll("\"", ""));
            String json = gson.toJson(rune);
            myProducer.sendMessage(kafkaInfo, json);
        });
        return runeList;
    }

    @Override
    public List<Rune> getRunes() {
        ApiInfo apiInfo = apiInfoService.findOneByName(new Exception().getStackTrace()[0].getMethodName());
        Version version = versionService.findOneByCurrentVersion();

        List<String> pathVariable = apiCallHelper.setPathVariableVersion(version);
        WebClientDTO webClientDTO = new WebClientDTO(apiInfo);
        WebClientParams webClientParams = WebClientParams.builder().pathVariables(pathVariable).build();
        webClientDTO.setWebClientParams(webClientDTO, webClientParams);
        ApiKey apiKey = new ApiKey().getEmptyApiKey();

        String response = (String) apiCallHelper.apiCall(webClientDTO, webClient, apiKey, String.class);

        List<Rune> runes = setRuneList(response);

        runes = sendKafkaMessage(apiInfo.getKafkaInfo(), runes, version);
        return runes;
    }

}
