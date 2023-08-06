package riot.api.data.engineer.serviceimpl;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.dto.WebClientDTO;
import riot.api.data.engineer.entity.api.ApiKey;
import riot.api.data.engineer.entity.KafkaInfo;
import riot.api.data.engineer.entity.MyProducer;
import riot.api.data.engineer.entity.Version;
import riot.api.data.engineer.entity.api.ApiInfo;
import riot.api.data.engineer.dto.champions.Champions;
import riot.api.data.engineer.dto.champions.Data;
import riot.api.data.engineer.interfaces.ApiCallHelper;
import riot.api.data.engineer.params.WebClientParams;
import riot.api.data.engineer.service.ApiInfoService;
import riot.api.data.engineer.service.ChampionsService;
import riot.api.data.engineer.service.VersionService;
import riot.api.data.engineer.utils.UtilManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChampionsServiceImpl implements ChampionsService {
    private static final String JSON_OBJECT_DATA = "data";
    private final MyProducer myProducer;
    private final ApiInfoService apiInfoService;
    private final VersionService versionService;
    private final ApiCallHelper apiCallHelper;
    private final WebClient webClient;

    @Override
    public Champions setChampions(String response) {
        JsonObject responseJsonObject = UtilManager.StringToJsonObject(response);
        Champions champions = new Champions(responseJsonObject);

        JsonObject championData = responseJsonObject.getAsJsonObject(JSON_OBJECT_DATA);
        List<Data> dataList = new ArrayList<>();
        dataList = setChampionsDataList(dataList,championData);
        champions.setDataList(dataList);
        return champions;
    }

    private List<Data> setChampionsDataList(List<Data> dataList, JsonObject championData) {
        Gson gson = new Gson();
        championData.keySet().forEach(key ->{
            JsonObject championJsonObject = championData.getAsJsonObject(key);
            Data data = gson.fromJson(championJsonObject,Data.class);
            dataList.add(data);
        });
        return dataList;
    }

    @Override
    public List<Data> sendKafkaMessage(KafkaInfo kafkaInfo, Champions champions) {
        List<Data> datalist = champions.getDataList();
        datalist = setChampionVersion(datalist, champions);
        String jsonData = UtilManager.collectionToJsonString(datalist);
        myProducer.sendMessage(kafkaInfo, jsonData);
        return datalist;
    }

    @Override
    public List<Data> getChampions() {
        ApiInfo apiInfo = apiInfoService.findOneByName(new Exception().getStackTrace()[0].getMethodName());
        Version version = versionService.findOneByCurrentVersion();

        List<String> pathVariable = apiCallHelper.setPathVariableVersion(version);

        WebClientDTO webClientDTO = new WebClientDTO(apiInfo);
        WebClientParams webClientParams = WebClientParams.builder().pathVariables(pathVariable).build();
        webClientDTO.setWebClientParams(webClientDTO,webClientParams);

        ApiKey apiKey = new ApiKey().getEmptyApiKey();
        String response = (String) apiCallHelper.apiCall(webClientDTO,webClient,apiKey,String.class);

        Champions champions = setChampions(response);

        List<Data> dataList = sendKafkaMessage(apiInfo.getKafkaInfo(), champions);

        return dataList;
    }

    private List<Data> setChampionVersion(List<Data> dataList, Champions champions) {
        return dataList.stream().map(data -> {
                    data.setVersion(champions.getVersion().replaceAll("\"", ""));
                    return data;
                })
                .collect(Collectors.toList());
    }

}
