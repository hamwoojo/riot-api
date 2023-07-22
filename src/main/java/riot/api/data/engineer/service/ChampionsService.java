package riot.api.data.engineer.service;


import riot.api.data.engineer.entity.KafkaInfo;
import riot.api.data.engineer.dto.champions.Champions;
import riot.api.data.engineer.dto.champions.Data;

import java.util.List;

public interface ChampionsService {

    Champions setChampions(String response);

    List<Data> sendKafkaMessage(KafkaInfo kafkaInfo, Champions champions);

    List<Data> getChampions();
}
