package riot.api.data.engineer.dto.champions;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Champions {
    private static final String JSON_OBJECT_VERSION = "version";
    private static final String JSON_OBJECT_TYPE = "type";
    private static final String JSON_OBJECT_FORMAT = "format";

    private String type;
    private String version;
    private String format;
    private List<Data> dataList;


    public Champions(JsonObject jsonObject){
        this.version = String.valueOf(jsonObject.get(JSON_OBJECT_VERSION));
        this.type = String.valueOf(jsonObject.get(JSON_OBJECT_TYPE));
        this.format = String.valueOf(jsonObject.get(JSON_OBJECT_FORMAT));
    }
}
