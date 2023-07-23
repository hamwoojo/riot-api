package riot.api.data.engineer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import riot.api.data.engineer.dto.ApiResultDTO;
import riot.api.data.engineer.entity.Version;
import riot.api.data.engineer.service.VersionService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/ddragon/version")
@RequiredArgsConstructor
public class VersionController {

    private final VersionService versionService;

    @GetMapping("")
    public ResponseEntity<ApiResultDTO> getVersions() {
        try {
            List<Version> versions = versionService.getVersions();
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.OK, versions);
            return new ResponseEntity<>(apiResultDTO,apiResultDTO.getHttpStatus());
        } catch (Exception e) {
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return new ResponseEntity<>(apiResultDTO, apiResultDTO.getHttpStatus());
        }
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResultDTO> getCurrentVersion() {
        try {
            Version version = versionService.findOneByCurrentVersion();
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.OK, version);
            return new ResponseEntity<>(apiResultDTO,apiResultDTO.getHttpStatus());
        } catch (Exception e) {
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return new ResponseEntity<>(apiResultDTO, apiResultDTO.getHttpStatus());
        }
    }


}
