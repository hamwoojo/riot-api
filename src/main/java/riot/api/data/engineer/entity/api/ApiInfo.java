package riot.api.data.engineer.entity.api;


import lombok.Getter;
import lombok.NoArgsConstructor;
import riot.api.data.engineer.entity.KafkaInfo;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity(name = "api_info")
@NoArgsConstructor
public class ApiInfo {



    @Id
    @Column(name = "api_info_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long apiInfoId;
    /** api 명 **/
    @Column(name = "api_name")
    private String apiName;
    /** 호출 url **/
    @Column(name = "api_url")
    String apiUrl;

    @Column(name = "api_scheme")
    private String apiScheme;

    @Column(name = "api_host")
    private String apiHost;
    /** 설명 **/
    @Column(name = "description")
    String description;
    /** GET,POST,PUT,DELETE **/
    @Column(name = "http_method")
    String httpMethod;
    /** 호출 제한 **/
    @Column(name = "rate_limit")
    int rateLimit;
    /** 호출 제한 텀 **/
    @Column(name = "rate_limit_interval")
    int rateLimitInterval;
    @Column(name = "content_type")
    private String contentType;

    @OneToOne
    @JoinColumn(name = "api_info_id")
    private KafkaInfo kafkaInfo;

    @OneToMany(fetch = FetchType.EAGER, cascade={CascadeType.ALL}, mappedBy = "apiInfo", orphanRemoval=true)
    private List<ApiParams> apiParams;

    public ApiInfo(Long apiInfoId, String apiName, String apiUrl, String apiScheme, String apiHost) {
        this.apiInfoId = apiInfoId;
        this.apiName = apiName;
        this.apiUrl = apiUrl;
        this.apiScheme = apiScheme;
        this.apiHost = apiHost;
    }

}
