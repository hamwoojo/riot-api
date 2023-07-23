package riot.api.data.engineer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import riot.api.data.engineer.dto.ApiResultDTO;
import riot.api.data.engineer.dto.runes.Rune;
import riot.api.data.engineer.service.RuneService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/ddragon/runes")
@RequiredArgsConstructor
public class RunesController {
    private final RuneService runeService;

    @GetMapping("")
    public ResponseEntity<ApiResultDTO> getRunes() {
        try {
            List<Rune> runes = runeService.getRunes();

            return new ResponseEntity<>(new ApiResultDTO(ApiResultDTO.ApiStatus.OK.getStatusCode(), ApiResultDTO.ApiStatus.OK.getStatus(), runes), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResultDTO(ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR.getStatusCode(), ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR.getStatus(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
