package riot.api.data.engineer.serviceimpl;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.dto.WebClientDTO;
import riot.api.data.engineer.entity.KafkaInfo;
import riot.api.data.engineer.entity.MyProducer;
import riot.api.data.engineer.entity.Version;
import riot.api.data.engineer.entity.WebClientCaller;
import riot.api.data.engineer.entity.api.ApiInfo;
import riot.api.data.engineer.entity.champions.Champions;
import riot.api.data.engineer.entity.champions.Data;
import riot.api.data.engineer.service.ChampionsService;
import riot.api.data.engineer.utils.UtilManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChampionsServiceImpl implements ChampionsService {
    private static final String JSON_OBJECT_DATA = "data";
    private final MyProducer myProducer;

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
            String championName = key;
            JsonObject championJsonObject = championData.getAsJsonObject(championName);
            Data data = gson.fromJson(championJsonObject,Data.class);
            dataList.add(data);
        });
        return dataList;
    }

    @Override
    public String apiCall(WebClient webClient, ApiInfo apiInfo, List<String> pathVariable) {

        WebClientDTO webClientDTO = WebClientDTO.builder()
                .scheme(apiInfo.getApiScheme())
                .host(apiInfo.getApiHost())
                .path(apiInfo.getApiUrl())
                .pathVariable(pathVariable)
                .build();

        WebClientCaller webClientCaller = WebClientCaller.builder()
                .webclient(webClient)
                .webClientDTO(webClientDTO)
                .build();

        return webClientCaller.getWebClientToString();
    }

    @Override
    public List<String> setPathVariableVersion(Version version) {
        List<String> pathVariable = new ArrayList<>();
        pathVariable.add(version.getVersion());
        return pathVariable;
    }

    @Override
    public List<Data> sendKafkaMessage(KafkaInfo kafkaInfo, Champions champions) {
        List<Data> datalist = champions.getDataList();
        datalist = setChampionVersion(datalist, champions);
        String jsonData = UtilManager.collectionToJsonString(datalist);
        myProducer.sendMessage(kafkaInfo, jsonData);
        return datalist;
    }

    private List<Data> setChampionVersion(List<Data> dataList, Champions champions) {
        return dataList.stream().map(data -> {
                    data.setVersion(champions.getVersion().replaceAll("\"", ""));
                    return data;
                })
                .collect(Collectors.toList());
    }

}
