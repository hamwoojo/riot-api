package riot.api.data.engineer.service;

import org.springframework.http.ResponseEntity;
import riot.api.data.engineer.dto.ApiResultDTO;
import riot.api.data.engineer.entity.MatchInfo;
import riot.api.data.engineer.dto.api.ApiInfo;
import riot.api.data.engineer.dto.api.ApiKey;

import java.util.List;

public interface MatchInfoService {

    MatchInfo matchInfoSave(MatchInfo matchInfo);

    void matchListApiCall(ApiKey apiKey, String apiName, String startDate, String endDate);

    ResponseEntity<ApiResultDTO> createMatchInfoTasks(String method, String startDate, String endDate);

    List<MatchInfo> findMatchInfoList();

    ResponseEntity<ApiResultDTO> createMatchInfoDetailTasks(ApiInfo apiInfo, List<ApiKey> apiKeyList);

    ApiResultDTO deleteAllByCollectCompleteYn(String collectCompleteYn);

}
