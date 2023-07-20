package riot.api.data.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ApiResultDTO {

    int statusCode;
    String message;
    Object data;

    public enum ApiStatus{
        OK(200,"success"),
        CREATED(201,"success"),
        BAD_REQUEST(400, "fail"),
        UNAUTHORIZED(401, "fail"),
        NOT_FOUND(401, "fail"),
        INTERNAL_SERVER_ERROR(500,"fail"),
        SERVICE_UNAVAILABLE(503,"fail");
        Integer statusCode;
        String status;
        ApiStatus(int statusCode, String status) {
            this.status = status;
            this.statusCode = statusCode;
        }
        public Integer getStatusCode() {
            return statusCode;
        }
        public String getStatus(){
            return status;
        }
    }

}
