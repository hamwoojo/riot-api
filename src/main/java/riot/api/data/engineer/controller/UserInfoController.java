package riot.api.data.engineer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import riot.api.data.engineer.dto.ApiResultDTO;
import riot.api.data.engineer.service.UserInfoService;


@Slf4j
@RestController
@RequestMapping(value = "/userinfo")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping("/user/entries")
    public ResponseEntity<ApiResultDTO> getUserEntries() throws InterruptedException {
        return userInfoService.getUserEntries();
    }

    @DeleteMapping("/user/entries")
    public ResponseEntity<ApiResultDTO> userEntriesDeleteByUpdateYn(@RequestParam(required = false, name = "updateYn") String updateYn) {
        try {
            ApiResultDTO apiResult = userInfoService.deleteAllByUpdateYn(updateYn);
            return new ResponseEntity<>(apiResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResultDTO(500, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

