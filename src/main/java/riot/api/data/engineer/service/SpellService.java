package riot.api.data.engineer.service;

import riot.api.data.engineer.entity.KafkaInfo;
import riot.api.data.engineer.dto.spells.Spell;
import riot.api.data.engineer.dto.spells.Spells;

import java.util.List;

public interface SpellService {
    List<Spell> sendKafkaMessage(KafkaInfo kafkaInfo, Spells spellList);

    List<Spell> getSpells();
}
