package riot.api.data.engineer.service;

import org.springframework.http.ResponseEntity;
import riot.api.data.engineer.dto.ApiResultDTO;
import riot.api.data.engineer.entity.UserInfo;
import riot.api.data.engineer.dto.api.ApiInfo;
import riot.api.data.engineer.dto.api.ApiKey;

import java.util.List;

public interface UserInfoService {


    ResponseEntity<ApiResultDTO> createUserEntriesTasks(List<ApiInfo> apiInfoList, List<ApiKey> apiKeyList) throws InterruptedException;

    List<UserInfo> findUserInfoListUpdateYnIsN(Long apiKey, String updateYn);

    ApiResultDTO deleteAllByUpdateYn(String updateYn);

    UserInfo save(UserInfo userInfo);
}
