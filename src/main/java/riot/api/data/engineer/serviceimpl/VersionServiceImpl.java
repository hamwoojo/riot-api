package riot.api.data.engineer.serviceimpl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import riot.api.data.engineer.dto.WebClientDTO;
import riot.api.data.engineer.entity.api.ApiKey;
import riot.api.data.engineer.entity.Version;
import riot.api.data.engineer.entity.api.ApiInfo;
import riot.api.data.engineer.interfaces.ApiCallHelper;
import riot.api.data.engineer.repository.VersionQueryRepository;
import riot.api.data.engineer.repository.VersionRepository;
import riot.api.data.engineer.service.ApiInfoService;
import riot.api.data.engineer.service.VersionService;
import riot.api.data.engineer.utils.UtilManager;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VersionServiceImpl implements VersionService {
    private final VersionQueryRepository versionQueryRepository;
    private final VersionRepository versionRepository;
    private final ApiInfoService apiInfoService;
    private final WebClient webClient;
    private final ApiCallHelper apiCallHelper;

    @Override
    public Version findOneByCurrentVersion() {
        return versionQueryRepository.findOneByCurrentVersion();
    }

    @Override
    public void save(List<Version> versionList) {
        versionRepository.saveAll(versionList);
    }

    @Override
    public List<Version> getVersions() {
        ApiInfo apiInfo = apiInfoService.findOneByName(new Exception().getStackTrace()[0].getMethodName());

        WebClientDTO webClientDTO = new WebClientDTO(apiInfo);
        ApiKey apiKey = new ApiKey().getEmptyApiKey();

        String response = (String) apiCallHelper.apiCall(webClientDTO,webClient,apiKey,String.class);
        List<Version> versions = getVersionList(response);
        save(versions);

        return versions;
    }

    private List<Version> getVersionList(String response) {
        List<Version> versionList = new ArrayList<>();
        JsonArray jsonArrayResponse = UtilManager.StringToJsonArray(response);

        for (JsonElement jsonElement : jsonArrayResponse) {
            String versionString = jsonElement.getAsString();
            boolean currentVersionYn = versionString.equals(jsonArrayResponse.get(0).getAsString());
            Version version = new Version(versionString, currentVersionYn);
            versionList.add(version);
        }
        return versionList;
    }

}
