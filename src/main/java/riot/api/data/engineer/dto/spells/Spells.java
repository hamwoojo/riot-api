package riot.api.data.engineer.dto.spells;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class Spells {
    public static final String JSON_TYPE = "type";
    public static final String JSON_VERSION = "version";
    public static final String JSON_data = "data";

    String type;
    String version;
    List<Spell> spellList;

    public Spells(JsonObject jsonObject){
        this.type = String.valueOf(jsonObject.get(JSON_TYPE));
        this.version = String.valueOf(jsonObject.get(JSON_VERSION));
    }
}
