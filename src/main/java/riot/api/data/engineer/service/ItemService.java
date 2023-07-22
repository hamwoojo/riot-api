package riot.api.data.engineer.service;


import riot.api.data.engineer.entity.KafkaInfo;
import riot.api.data.engineer.dto.items.Item;
import riot.api.data.engineer.dto.items.Items;

import java.util.List;

public interface ItemService {
    Items setItems(String response);

    List<Item> sendKafkaMessage(KafkaInfo kafkaInfo, Items items);

    List<Item> getItems();
}
