package riot.api.data.engineer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
@AllArgsConstructor
public class ApiResultDTO {

    int statusCode;
    String message;
    HttpStatus httpStatus;
    Object data;


    public enum ApiStatus{
        OK(200,"success",HttpStatus.OK),
        CREATED(201,"success",HttpStatus.CREATED),
        BAD_REQUEST(400, "fail",HttpStatus.BAD_REQUEST),
        UNAUTHORIZED(401, "fail",HttpStatus.UNAUTHORIZED),
        NOT_FOUND(401, "fail",HttpStatus.NOT_FOUND),
        INTERNAL_SERVER_ERROR(500,"fail",HttpStatus.INTERNAL_SERVER_ERROR),
        SERVICE_UNAVAILABLE(503,"fail",HttpStatus.SERVICE_UNAVAILABLE);
        Integer statusCode;
        String status;
        HttpStatus httpStatus;
        ApiStatus(int statusCode, String status,HttpStatus httpStatus) {
            this.status = status;
            this.statusCode = statusCode;
            this.httpStatus = httpStatus;
        }
        public Integer getStatusCode() {
            return statusCode;
        }
        public String getStatus(){
            return status;
        }
        public HttpStatus getHttpStatus(){return httpStatus;}

    }
    public ApiResultDTO(ApiStatus apiStatus, Object data) {
        this.statusCode = apiStatus.getStatusCode();
        this.message = apiStatus.getStatus();
        this.httpStatus = apiStatus.getHttpStatus();
        this.data = data;
    }

}
