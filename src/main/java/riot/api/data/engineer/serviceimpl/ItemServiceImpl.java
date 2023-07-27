package riot.api.data.engineer.serviceimpl;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.dto.WebClientDTO;
import riot.api.data.engineer.dto.api.ApiKey;
import riot.api.data.engineer.entity.KafkaInfo;
import riot.api.data.engineer.entity.MyProducer;
import riot.api.data.engineer.entity.Version;
import riot.api.data.engineer.dto.api.ApiInfo;
import riot.api.data.engineer.dto.items.Item;
import riot.api.data.engineer.dto.items.Items;
import riot.api.data.engineer.interfaces.ApiCallHelper;
import riot.api.data.engineer.params.WebClientParams;
import riot.api.data.engineer.service.ApiInfoService;
import riot.api.data.engineer.service.ItemService;
import riot.api.data.engineer.service.VersionService;
import riot.api.data.engineer.utils.UtilManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final String JSON_OBJECT_DATA = "data";

    private final MyProducer myProducer;
    private final ApiInfoService apiInfoService;
    private final VersionService versionService;
    private final WebClient webClient;
    private final ApiCallHelper apiCallHelper;

    @Override
    public Items setItems(String response) {
        JsonObject responseJsonObject = UtilManager.StringToJsonObject(response);

        Items items = new Items(responseJsonObject);
        JsonObject itemsData = responseJsonObject.getAsJsonObject(JSON_OBJECT_DATA);

        items = setDataList(items,itemsData);

        return items;
    }

    private Items setDataList(Items items, JsonObject itemsData) {
        Gson gson = new Gson();

        itemsData.keySet().forEach(key -> {
            JsonObject itemJson = itemsData.getAsJsonObject(key);
            Item item = gson.fromJson(itemJson,Item.class);
            item.setItemId(key);
            items.getItemList().add(item);
        });

        return items;
    }

    @Override
    public List<Item> sendKafkaMessage(KafkaInfo kafkaInfo, Items items) {
        Gson gson = new Gson();
        items.getItemList().forEach( item -> {
            item.setVersion(items.getVersion().replaceAll("\"",""));
            item.setDescription(item.getDescription().replaceAll("<[^>]+>",""));
            String json = gson.toJson(item);
            myProducer.sendMessage(kafkaInfo, json);
        });
        return items.getItemList();
    }

    @Override
    public List<Item> getItems() {
        ApiInfo apiInfo = apiInfoService.findOneByName(new Exception().getStackTrace()[0].getMethodName());
        Version version = versionService.findOneByCurrentVersion();

        List<String> pathVariable = apiCallHelper.setPathVariableVersion(version);

        WebClientDTO webClientDTO = new WebClientDTO(apiInfo);
        WebClientParams webClientParams = WebClientParams.builder().pathVariables(pathVariable).build();
        webClientDTO.setWebClientParams(webClientDTO,webClientParams);
        ApiKey apiKey = new ApiKey().getEmptyApiKey();

        String response = (String) apiCallHelper.apiCall(webClientDTO,webClient,apiKey,String.class);

        Items itemList = setItems(response);
        List<Item> items = sendKafkaMessage(apiInfo.getKafkaInfo(), itemList);

        return items;
    }

}
