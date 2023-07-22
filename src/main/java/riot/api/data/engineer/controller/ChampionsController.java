package riot.api.data.engineer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import riot.api.data.engineer.dto.ApiResultDTO;
import riot.api.data.engineer.dto.champions.Data;
import riot.api.data.engineer.service.ChampionsService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/ddragon/champions")
@RequiredArgsConstructor
public class ChampionsController {

    private final ChampionsService championsService;


    @GetMapping("")
    public ResponseEntity<ApiResultDTO> getChampions() {
        try {
            List<Data> dataList = championsService.getChampions();

            return new ResponseEntity<>(new ApiResultDTO(ApiResultDTO.ApiStatus.OK.getStatusCode(), ApiResultDTO.ApiStatus.OK.getStatus(), dataList), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResultDTO(ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR.getStatusCode(), ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR.getStatus(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
