package riot.api.data.engineer.service;

import riot.api.data.engineer.dto.api.ApiParams;

import java.util.List;

public interface ApiParamsService {

    List<ApiParams> getApiParamsList(Long apiInfoId);
}