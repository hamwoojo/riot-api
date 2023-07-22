package riot.api.data.engineer.service;

import org.springframework.http.ResponseEntity;
import riot.api.data.engineer.dto.ApiResultDTO;
import riot.api.data.engineer.entity.UserInfo;

import java.util.List;

public interface UserInfoService {


    ResponseEntity<ApiResultDTO> getUserEntries() throws InterruptedException;

    List<UserInfo> findUserInfoListUpdateYnIsN(Long apiKey, String updateYn);

    ApiResultDTO deleteAllByUpdateYn(String updateYn);

    UserInfo save(UserInfo userInfo);
}
