package riot.api.data.engineer.serviceimpl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.dto.ApiResultDTO;
import riot.api.data.engineer.dto.WebClientDTO;
import riot.api.data.engineer.entity.*;
import riot.api.data.engineer.dto.api.ApiInfo;
import riot.api.data.engineer.dto.api.ApiKey;
import riot.api.data.engineer.dto.api.ApiParams;
import riot.api.data.engineer.dto.matchdetail.Info;
import riot.api.data.engineer.dto.matchdetail.MatchDetail;
import riot.api.data.engineer.dto.matchdetail.MetaData;
import riot.api.data.engineer.interfaces.ApiCallHelper;
import riot.api.data.engineer.params.WebClientParams;
import riot.api.data.engineer.repository.MatchInfoQueryRepository;
import riot.api.data.engineer.repository.MatchInfoRepository;
import riot.api.data.engineer.service.*;
import riot.api.data.engineer.utils.EpochTimestampConverter;
import riot.api.data.engineer.utils.UtilManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchInfoServiceImpl implements MatchInfoService {
    private final MatchInfoRepository matchInfoRepository;
    private final UserInfoDetailService userInfoDetailService;
    private final ApiInfoService apiInfoService;
    private final ApiKeyService apiKeyService;
    private final KafkaInfoService kafkaInfoService;
    private final MyProducer myProducer;
    private final MatchInfoQueryRepository matchInfoQueryRepository;
    private final WebClient webClient;
    private final ApiCallHelper apiCallHelper;


    @Override
    public ResponseEntity<ApiResultDTO> createMatchInfoTasks(String method, String startDate, String endDate) {
        log.info("===== createMatchInfoTasks Start =====");
        List<Callable<Integer>> tasks = new ArrayList<>();
        List<ApiKey> apiKeyList = apiKeyService.findList();
        ExecutorService executorService = Executors.newFixedThreadPool(apiKeyList.size());
        try {
            for (ApiKey apiKey : apiKeyList) {
                Callable<Integer> task = () -> {
                    matchListApiCall(apiKey, method, startDate, endDate);
                    return 200;
                };
                tasks.add(task);
            }
            executorService.invokeAll(tasks);
            executorService.shutdown();
            log.info("===== createMatchInfoTasks End =====");
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.OK, null);
            return new ResponseEntity<>(apiResultDTO,apiResultDTO.getHttpStatus());
        }
        catch (Exception e) {
            log.error(e.getMessage());
            executorService.shutdownNow();
            log.error("===== createMatchInfoTasks End =====");
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return new ResponseEntity<>(apiResultDTO, apiResultDTO.getHttpStatus());
        }
    }
    @Override
    public void matchListApiCall(ApiKey apiKey, String apiName, String startDate, String endDate) {

        try {
            List<UserInfoDetail> userInfoDetailList = userInfoDetailService.findUserInfoDetailListByApiKey(apiKey.getApiKeyId());
            ApiInfo apiInfo = apiInfoService.findOneByName(apiName);
            Map<String, String> queryParams = setParams(apiInfo.getApiParams(), startDate, endDate);

            for (UserInfoDetail userInfoDetail : userInfoDetailList) {

                List<String> pathVariableList = new ArrayList<>();
                pathVariableList.add(userInfoDetail.getPuuid());

                WebClientDTO webClientDTO = new WebClientDTO(apiInfo);
                WebClientParams webClientParams = WebClientParams.builder().pathVariables(pathVariableList).build();
                webClientDTO.setWebClientParams(webClientDTO,webClientParams);

                /*** 매치리스트 API 호출  ***/
                List<String> response = (List<String>) apiCallHelper.apiCall(webClientDTO,webClient,apiKey,List.class);
                /*** String to MatchInfo ***/
                List<MatchInfo> matchInfoList = responseToEntity(response, apiKey.getApiKeyId());
                /*** matchInfo 저장 ***/
                for (MatchInfo matchInfo : matchInfoList) {
                    matchInfoRepository.save(matchInfo);
                }
                Thread.sleep(1200);
            }
        } catch (Exception e) {
            log.error(" === ERROR === ");
            log.error(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResultDTO> createMatchInfoDetailTasks(ApiInfo apiInfo, List<ApiKey> apiKeyList) {
        log.info("===== createMatchInfoDetailTasks Start =====");

        int apiKeyCount = apiKeyList.size();
        ExecutorService executorService = Executors.newFixedThreadPool(apiKeyCount);
        try {

            /*** 전체 데이터 find ***/
            List<List<MatchInfo>> partionMatchInfoList = partitionList(matchInfoQueryRepository.findMatchInfoByCollectCompleteYnIsFalse(),apiKeyCount);

            List<Callable<Integer>> tasks = new ArrayList<>();

            for (int i = 0; i < apiKeyCount; i++) {
                List<MatchInfo> matchInfoPartition = partionMatchInfoList.get(i);
                ApiKey apiKey = apiKeyList.get(i);
                Callable<Integer> task = () -> {
                    matchDetailApiCall(apiInfo, apiKey, matchInfoPartition, apiInfo.getKafkaInfo());
                    return 200;
                };
                tasks.add(task);
            }
            executorService.invokeAll(tasks);
            executorService.shutdown();

            log.info("===== MatchInfoDetailTasks End =====");
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.OK, null);
            return new ResponseEntity<>(apiResultDTO,apiResultDTO.getHttpStatus());

        } catch (Exception e) {
            executorService.shutdownNow();
            log.error(e.getMessage());
            log.error("===== MatchInfoDetailTasks End =====");
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return new ResponseEntity<>(apiResultDTO, apiResultDTO.getHttpStatus());
        }
    }

    @Override
    @Transactional
    public ApiResultDTO deleteAllByCollectCompleteYn(String collectCompleteYn) {
        try{
            Optional<String> optionalCollectCompleteYn = Optional.ofNullable(collectCompleteYn);
            if(optionalCollectCompleteYn.isPresent()){
                if(optionalCollectCompleteYn.get().equals("true")){
                    matchInfoRepository.deleteMatchInfosByCollectCompleteYn(true);
                    ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.OK, null);
                    return apiResultDTO;
                } else if (optionalCollectCompleteYn.get().equals("false")) {
                    matchInfoRepository.deleteMatchInfosByCollectCompleteYn(false);
                    ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.OK, null);
                    return apiResultDTO;
                } else {
                    ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.BAD_REQUEST, null);
                    return apiResultDTO;
                }
            }
            else {
                matchInfoRepository.deleteAllInBatch();
                ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.OK, null);
                return apiResultDTO;

            }
        }catch (Exception e){
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return apiResultDTO;
        }

    }

    protected void matchDetailApiCall(ApiInfo apiInfo, ApiKey apiKey, List<MatchInfo> matchInfos, KafkaInfo kafkaInfo) {
        Gson gson = new Gson();

        for (MatchInfo matchInfo : matchInfos) {
            try {
                List<String> pathVariableList = new ArrayList<>();
                pathVariableList.add(matchInfo.getId());

                WebClientDTO webClientDTO = new WebClientDTO(apiInfo);
                WebClientParams webClientParams = WebClientParams.builder().pathVariables(pathVariableList).build();
                webClientDTO.setWebClientParams(webClientDTO,webClientParams);

                String response = (String) apiCallHelper.apiCall(webClientDTO,webClient,apiKey,String.class);

                if (StringUtils.isEmpty(response)) {
                    continue;
                } else {
                    MatchDetail matchDetail = setMatchInfoDetail(response);
                    EpochTimestampConverter epochTimestampConverter = new EpochTimestampConverter(matchDetail.getInfo().getGameStartTimestamp());
                    matchDetail.setCollectDate(epochTimestampConverter.convertToDateString());
                    String processedResponse = gson.toJson(matchDetail);

                    myProducer.sendMessage(kafkaInfo, processedResponse);
                    matchInfo.setCollectCompleteYn(true);
                    matchInfoSave(matchInfo);
                }
                Thread.sleep(1200);
            } catch (Exception e) {
                log.error("=== ERROR ===");
                log.error(" ApiKey : " + matchInfo.getApiKeyId());
                log.error("ID : " + matchInfo.getId());
            }
        }
    }

    private MatchDetail setMatchInfoDetail(String response) {
        Gson gson = new Gson();
        JsonObject jsonObject = new UtilManager().StringToJsonObject(response);
        MatchDetail matchDetail = new MatchDetail();

        Info infoData = gson.fromJson(jsonObject.getAsJsonObject("info"), Info.class);
        matchDetail.setInfo(infoData);

        MetaData metaData = gson.fromJson(jsonObject.getAsJsonObject("metadata"), MetaData.class);
        matchDetail.setMetadata(metaData);

        return matchDetail;
    }

    private static <T> List<List<T>> partitionList(List<T> list, int apiKeyCount) {
        List<List<T>> partitions = new ArrayList<>();
        int size = list.size();
        int partitionSize = size / apiKeyCount;
        int remainder = size % apiKeyCount;
        int startIndex = 0;

        for (int i = 0; i < apiKeyCount; i++) {
            int endIndex = startIndex + partitionSize;
            if (remainder > 0) {
                endIndex++;
                remainder--;
            }
            endIndex = Math.min(endIndex, size);
            partitions.add(list.subList(startIndex, endIndex));
            startIndex = endIndex;
        }

        return partitions;
    }

    public List<MatchInfo> responseToEntity(List<String> response, Long apiKeyId) {
        List<MatchInfo> matchInfoList = new ArrayList<>();
        for (String id : response) {
            MatchInfo matchInfo = new MatchInfo(id, apiKeyId);
            matchInfoList.add(matchInfo);
        }
        return matchInfoList;
    }

    public Map<String, String> setParams(List<ApiParams> apiParamsList, String startDate, String endDate) {
        Map<String, String> map = new HashMap<>();
        for (ApiParams apiParams : apiParamsList) {
            if (apiParams.getIsRequired()) {
                map.put(apiParams.getApiKey(), apiParams.getApiValue());
                if (apiParams.getDateParamRequired() && "startTime".equals(apiParams.getApiKey())) {
                    map.put(apiParams.getApiKey(), setStartDate(startDate));
                } else if (apiParams.getDateParamRequired() && "endTime".equals(apiParams.getApiKey())) {
                    map.put(apiParams.getApiKey(), setEndDate(endDate));
                }
            }
        }
        return map;
    }

    public String setStartDate(String startDate) {
        return String.valueOf(LocalDateTime.of(LocalDate.parse(startDate), LocalTime.of(0, 0, 0)).atZone(ZoneId.of("Asia/Seoul")).toEpochSecond());
    }

    public String setEndDate(String endDate) {
        return String.valueOf(
                LocalDateTime.of(LocalDate.parse(endDate),
                LocalTime.of(23, 59, 59)).atZone(ZoneId.of("Asia/Seoul")).toEpochSecond());
    }

    @Override
    public List<MatchInfo> findMatchInfoList() {
        return matchInfoRepository.findAll();
    }

    @Override
    public MatchInfo matchInfoSave(MatchInfo matchInfo) {
        return matchInfoRepository.save(matchInfo);
    }
}
