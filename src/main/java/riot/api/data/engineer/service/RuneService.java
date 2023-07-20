package riot.api.data.engineer.service;

import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.entity.KafkaInfo;
import riot.api.data.engineer.entity.Version;
import riot.api.data.engineer.dto.api.ApiInfo;
import riot.api.data.engineer.dto.runes.Rune;
import riot.api.data.engineer.dto.runes.RuneList;

import java.util.List;

public interface RuneService {
    RuneList setRuneList(String response);

    List<String> setPathVariableVersion(Version version);

    String apiCall(WebClient webClient, ApiInfo apiInfo, List<String> pathVariable);

    List<Rune> sendKafkaMessage(KafkaInfo kafkaInfo, RuneList runeList,Version version);
}
