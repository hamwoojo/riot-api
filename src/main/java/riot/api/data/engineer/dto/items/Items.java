package riot.api.data.engineer.dto.items;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Items {

    private static final String JSON_OBJECT_VERSION = "version";
    private static final String JSON_OBJECT_TYPE = "type";

    private String type;
    private String version;
    private List<Item> itemList;

    public Items(JsonObject responseJsonObject){
        this.type = String.valueOf(responseJsonObject.get(JSON_OBJECT_TYPE));
        this.version = String.valueOf(responseJsonObject.get(JSON_OBJECT_VERSION));
        this.itemList = new ArrayList<>();
    }


}
