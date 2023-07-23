package riot.api.data.engineer.service;

import org.springframework.http.ResponseEntity;
import riot.api.data.engineer.dto.ApiResultDTO;
import riot.api.data.engineer.entity.UserInfoDetail;
import riot.api.data.engineer.dto.api.ApiKey;

import java.util.List;


public interface UserInfoDetailService {

    UserInfoDetail userInfoDetailSave(UserInfoDetail userInfoDetail);

    void userInfoDetailApiCall(ApiKey apiKey, String apiName);

    UserInfoDetail jsonToEntity(String response, Long apiKeyId);

    List<UserInfoDetail> findUserInfoDetailList();

    List<UserInfoDetail> findUserInfoDetailListByApiKey(Long apiKey);

    ApiResultDTO deleteAll();

    ResponseEntity<ApiResultDTO> getUserInfoDetail(String methodName);
}
