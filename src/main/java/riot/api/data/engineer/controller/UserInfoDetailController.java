package riot.api.data.engineer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import riot.api.data.engineer.dto.ApiResultDTO;
import riot.api.data.engineer.service.UserInfoDetailService;
@Slf4j
@RestController
@RequestMapping(value = "/userinfo/detail")
@RequiredArgsConstructor
public class UserInfoDetailController {
    private final UserInfoDetailService userInfoDetailService;

    @GetMapping("")
    public ResponseEntity<ApiResultDTO> getUserInfoDetail(){
        return userInfoDetailService.getUserInfoDetail(new Exception().getStackTrace()[0].getMethodName());
    }

    @DeleteMapping("")
    public ResponseEntity<ApiResultDTO> userDetailDeleteAll() {
            ApiResultDTO apiResultDTO = userInfoDetailService.deleteAll();
            return new ResponseEntity<>(apiResultDTO,apiResultDTO.getHttpStatus());
    }
}
