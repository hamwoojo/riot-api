package riot.api.data.engineer.serviceimpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.dto.ApiResultDTO;
import riot.api.data.engineer.dto.WebClientDTO;
import riot.api.data.engineer.entity.UserInfo;
import riot.api.data.engineer.entity.api.ApiInfo;
import riot.api.data.engineer.entity.api.ApiKey;
import riot.api.data.engineer.interfaces.ApiCallHelper;
import riot.api.data.engineer.params.WebClientParams;
import riot.api.data.engineer.repository.UserInfoQueryRepository;
import riot.api.data.engineer.repository.UserInfoRepository;
import riot.api.data.engineer.service.ApiInfoService;
import riot.api.data.engineer.service.ApiKeyService;
import riot.api.data.engineer.service.UserInfoService;
import riot.api.data.engineer.utils.UtilManager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {
    private final WebClient webClient;
    private final UserInfoRepository userInfoRepository;
    private final UserInfoQueryRepository userInfoQueryRepository;
    private final ApiInfoService apiInfoService;
    private final ApiKeyService apiKeyService;
    private final ApiCallHelper apiCallHelper;

    @Override
    public ResponseEntity<ApiResultDTO> getUserEntries() {

        log.info("===== getUserEntries Start =====");
        List<ApiInfo> apiInfoList = apiInfoService.findByName(new Exception().getStackTrace()[0].getMethodName());
        List<ApiKey> apiKeyList = apiKeyService.findList();

        int batchSize = apiKeyList.size();
        ExecutorService executorService = Executors.newFixedThreadPool(batchSize);
        List<Callable<Boolean>> tasks = createCallableTasks(apiInfoList,apiKeyList,batchSize);

        try{
            executorService.invokeAll(tasks);
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.OK, null);
            return new ResponseEntity<>(apiResultDTO,apiResultDTO.getHttpStatus());
        }
        catch (Exception e){
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return new ResponseEntity<>(apiResultDTO, apiResultDTO.getHttpStatus());
        }
        finally {
            executorService.shutdownNow();
            log.info("===== getUserEntries End =====");
        }

    }

    private List<Callable<Boolean>> createCallableTasks(List<ApiInfo> apiInfoList, List<ApiKey> apiKeyList,int batchSize) {
        List<Callable<Boolean>> tasks =
        apiInfoList.stream()
                .flatMap(apiInfo -> apiKeyList.stream()
                        .map(apiKey -> {
                            int page = apiKeyList.indexOf(apiKey) + 1;
                            return (Callable<Boolean>) () -> {
                                userEntriesApiCall(apiInfo, apiKey, page, batchSize);
                                return true;
                            };
                        })
                )
                .collect(Collectors.toList());

        return tasks;
    }

    public void userEntriesApiCall(ApiInfo apiInfo, ApiKey apiKey, int page, int batchSize){
        Gson gson = new Gson();
        int pageSum = page;

        try{

            WebClientDTO webClientDTO = new WebClientDTO(apiInfo);
            Map<String,String> paging = new HashMap<>();
            Type listType = new TypeToken<List<UserInfo>>() {}.getType();

            while(true){
                paging.put(UtilManager.PAGE,String.valueOf(pageSum));
                WebClientParams webClientParams = WebClientParams.builder().pagingMap(paging).build();
                webClientDTO.setWebClientParams(webClientDTO,webClientParams);

                String response = (String) apiCallHelper.apiCall(webClientDTO,webClient,apiKey,String.class);
                List<UserInfo> userInfoList = gson.fromJson(response,listType);

                if(CollectionUtils.isEmpty(userInfoList)){
                    break;
                }
                else{
                    for (UserInfo userInfo : userInfoList) {
                        saveWithUpdateYnAndApiKeyId(userInfo,apiKey);
                    }
                    pageSum += batchSize;
                }
                Thread.sleep(1200);
            }
        }
        catch (Exception e){
            log.error("=== userEntriesApiCall ERROR ===");
            log.error(e.getMessage());
        }
    }

    private void saveWithUpdateYnAndApiKeyId(UserInfo userInfo, ApiKey apiKey) {
        userInfo.setUpdateYn(UtilManager.N);
        userInfo.setApiKeyId(apiKey.getApiKeyId());
        userInfoRepository.saveAndFlush(userInfo);
    }

    @Override
    public List<UserInfo> findUserInfoListUpdateYnIsN(Long apiKey, String updateYn) {
        return userInfoQueryRepository.findListByApiKeyId(apiKey, updateYn);
    }

    @Override
    @Transactional
    public ApiResultDTO deleteAllByUpdateYn(String updateYn) {
        try{
            Optional<String> optionalUpdateYn = Optional.ofNullable(updateYn);
            if(optionalUpdateYn.isPresent()){
                if(optionalUpdateYn.get().equals(UtilManager.Y) || optionalUpdateYn.get().equals(UtilManager.N) ){
                    userInfoRepository.deleteUserInfosByUpdateYn(updateYn);
                    ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.OK, null);
                    return apiResultDTO;
                }
                else {
                    ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.BAD_REQUEST, "파라미터가 올바르지 않습니다.");
                    return apiResultDTO;
                }
            }
            else {
                userInfoRepository.deleteAllInBatch();
                ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.OK, null);
                return apiResultDTO;
            }

        }catch (Exception e){
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return apiResultDTO;
        }

    }

    @Override
    public UserInfo save(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }
}

