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
import riot.api.data.engineer.dto.spells.Spell;
import riot.api.data.engineer.dto.spells.Spells;
import riot.api.data.engineer.interfaces.ApiCallHelper;
import riot.api.data.engineer.params.WebClientParams;
import riot.api.data.engineer.service.ApiInfoService;
import riot.api.data.engineer.service.SpellService;
import riot.api.data.engineer.service.VersionService;
import riot.api.data.engineer.utils.UtilManager;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpellServiceImpl implements SpellService {
    private final MyProducer myProducer;
    private final ApiInfoService apiInfoService;
    private final VersionService versionService;
    private final WebClient webClient;
    private final ApiCallHelper apiCallHelper;

    private Spells setSpells(String response) {

        JsonObject jsonObject = UtilManager.StringToJsonObject(response);

        Spells spells = new Spells(jsonObject);
        JsonObject spellsData = jsonObject.getAsJsonObject(Spells.JSON_data);

        List<Spell> spellList = getSpellList(spellsData);
        spells.setSpellList(spellList);

        return spells;
    }

    private List<Spell> getSpellList(JsonObject spellsData){
        Gson gson = new Gson();
        List<Spell> spellList = new ArrayList<>();
        spellsData.keySet().forEach( key -> {
            String spellName = key;
            JsonObject spellJson = spellsData.getAsJsonObject(spellName);
            Spell spell = gson.fromJson(spellJson,Spell.class);
            spellList.add(spell);
        });
        return spellList;
    }

    @Override
    public List<Spell> sendKafkaMessage(KafkaInfo kafkaInfo, Spells spellList) {
        Gson gson = new Gson();
        spellList.getSpellList().stream().forEach(spell -> {
            spell.setVersion(spellList.getVersion().replaceAll("\"",""));
            String json = gson.toJson(spell);
            myProducer.sendMessage(kafkaInfo,json);
        });
        return spellList.getSpellList();
    }

    @Override
    public List<Spell> getSpells() {
        ApiInfo apiInfo = apiInfoService.findOneByName(new Exception().getStackTrace()[0].getMethodName());
        Version version = versionService.findOneByCurrentVersion();

        List<String> pathVariable = apiCallHelper.setPathVariableVersion(version);
        WebClientDTO webClientDTO = new WebClientDTO(apiInfo);
        WebClientParams webClientParams = WebClientParams.builder().pathVariables(pathVariable).build();
        webClientDTO.setWebClientParams(webClientDTO, webClientParams);
        ApiKey apiKey = new ApiKey().getEmptyApiKey();

        String response = (String) apiCallHelper.apiCall(webClientDTO,webClient,apiKey,String.class);

        Spells spellList = setSpells(response);
        List<Spell> spells = sendKafkaMessage(apiInfo.getKafkaInfo(), spellList);

        return spells;
    }




}
