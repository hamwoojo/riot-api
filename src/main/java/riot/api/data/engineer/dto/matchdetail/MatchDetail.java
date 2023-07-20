package riot.api.data.engineer.dto.matchdetail;


import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MatchDetail {

    @SerializedName("info")
    private Info info;
    @SerializedName("metadata")
    private MetaData metadata;

    @SerializedName("collect_date")
    private String collectDate;

}

