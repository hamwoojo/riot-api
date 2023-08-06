package riot.api.data.engineer.serviceimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.dto.ApiResultDTO;
import riot.api.data.engineer.dto.WebClientDTO;
import riot.api.data.engineer.entity.UserInfo;
import riot.api.data.engineer.entity.UserInfoDetail;
import riot.api.data.engineer.entity.api.ApiInfo;
import riot.api.data.engineer.entity.api.ApiKey;
import riot.api.data.engineer.interfaces.ApiCallHelper;
import riot.api.data.engineer.params.WebClientParams;
import riot.api.data.engineer.repository.UserInfoDetailQueryRepository;
import riot.api.data.engineer.repository.UserInfoDetailRepository;
import riot.api.data.engineer.service.ApiInfoService;
import riot.api.data.engineer.service.ApiKeyService;
import riot.api.data.engineer.service.UserInfoDetailService;
import riot.api.data.engineer.service.UserInfoService;
import riot.api.data.engineer.utils.UtilManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserInfoDetailServiceImpl implements UserInfoDetailService {
    private final UserInfoDetailRepository userInfoDetailRepository ;
    private final UserInfoService userInfoService ;
    private final ApiKeyService apiKeyService;
    private final ApiInfoService apiInfoService;
    private final UserInfoDetailQueryRepository userInfoDetailQueryRepository;
    private final WebClient webClient;
    private final ApiCallHelper apiCallHelper;

    @Override
    public ResponseEntity<ApiResultDTO> getUserInfoDetail(String apiName){
        log.info("===== getUserInfoDetail Start =====");
        List<ApiKey> apiKeyList = apiKeyService.findList();
        int batchSize = apiKeyList.size();
        ExecutorService executorService = Executors.newFixedThreadPool(batchSize);
        List<Callable<Boolean>> tasks = createCallableTasks(apiKeyList,apiName);

        try{
            executorService.invokeAll(tasks);
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.OK, null);
            return new ResponseEntity<>(apiResultDTO,apiResultDTO.getHttpStatus());
        }
        catch (Exception e){
            log.error("=== getUserInfoDetail ERROR ===");
            log.error(e.getMessage());
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return new ResponseEntity<>(apiResultDTO, apiResultDTO.getHttpStatus());
        }
        finally {
            executorService.shutdownNow();
            log.info("===== getUserInfoDetail End =====");

        }
    }

    private List<Callable<Boolean>> createCallableTasks(List<ApiKey> apiKeyList,String apiName) {
        List<Callable<Boolean>> tasks = apiKeyList.stream().map(apiKey ->
                (Callable<Boolean>) () -> {
                    userInfoDetailApiCall(apiKey,apiName);
                    return true;
                })
                .collect(Collectors.toList());
        return tasks;
    }

    @Override
    public void userInfoDetailApiCall(ApiKey apiKey, String apiName) {
        try{
            List<UserInfo> userInfoList = userInfoService.findUserInfoListUpdateYnIsN(apiKey.getApiKeyId(), "N");
            ApiInfo apiInfo = apiInfoService.findOneByName(apiName);

            WebClientDTO webClientDTO = new WebClientDTO(apiInfo);

            for (UserInfo userInfo : userInfoList) {
                List<String> pathVariableList = new ArrayList<>();
                pathVariableList.add(userInfo.getSummonerId());

                WebClientParams webClientParams = WebClientParams.builder().pathVariables(pathVariableList).build();
                webClientDTO.setWebClientParams(webClientDTO,webClientParams);

                String response = (String) apiCallHelper.apiCall(webClientDTO,webClient,apiKey,String.class);

                userInfoDetailSave(jsonToEntity(response, apiKey.getApiKeyId()));
                userInfo.setUpdateYn(UtilManager.Y);
                userInfoService.save(userInfo);

                Thread.sleep(1200);

            }
        }catch (InterruptedException ie){
            log.error(" === ERROR === ");
            log.error(ie.getMessage());
            Thread.currentThread().interrupt();
        }
        catch (Exception e){
            log.error(" === ERROR === ");
            log.error(e.getMessage());
        }
    }

    @Override
    public List<UserInfoDetail> findAllUserInfoDetail() {
        return userInfoDetailRepository.findAll();
    }

    @Override
    public List<UserInfoDetail> findUserInfoDetailListByApiKey(Long apiKey) {
        return userInfoDetailQueryRepository.findListByApiKeyId(apiKey);
    }

    @Override
    public UserInfoDetail userInfoDetailSave(UserInfoDetail userInfoDetail) {
        return userInfoDetailRepository.save(userInfoDetail);
    }

    @Override
    public UserInfoDetail jsonToEntity(String response, Long apiKeyId){
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            UserInfoDetail userInfoDetail = objectMapper.readValue(response, UserInfoDetail.class);
            userInfoDetail.setApiKeyId(apiKeyId);
            return userInfoDetail;
        } catch (JsonMappingException e) {
            log.info("jsonMapping Exception : {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            log.info("jsonProcessing Exception : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public ApiResultDTO deleteAll() {
        try{
            userInfoDetailRepository.deleteAllInBatch();
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.OK, null);
            return apiResultDTO;
        }catch (Exception e){
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return apiResultDTO;
        }
    }

}
