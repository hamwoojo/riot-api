package riot.api.data.engineer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import riot.api.data.engineer.dto.ApiResultDTO;
import riot.api.data.engineer.dto.items.Item;
import riot.api.data.engineer.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/ddragon/items")
@RequiredArgsConstructor
public class ItemsController {
    private final ItemService itemService;

    @GetMapping("")
    public ResponseEntity<ApiResultDTO> getItems() {
        try {

            List<Item> itemList = itemService.getItems();

            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.OK, itemList);
            return new ResponseEntity<>(apiResultDTO,apiResultDTO.getHttpStatus());
        } catch (Exception e) {
            ApiResultDTO apiResultDTO = new ApiResultDTO(ApiResultDTO.ApiStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return new ResponseEntity<>(apiResultDTO, apiResultDTO.getHttpStatus());
        }
    }

}
