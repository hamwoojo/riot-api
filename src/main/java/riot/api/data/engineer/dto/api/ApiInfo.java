package riot.api.data.engineer.dto.api;


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

    @Column(name = "api_name")
    private String apiName;

    @Column(name = "api_url")
    String apiUrl;

    @Column(name = "api_scheme")
    private String apiScheme;

    @Column(name = "api_host")
    private String apiHost;

    @Column(name = "description")
    String description;

    @Column(name = "http_method")
    String httpMethod;

    @Column(name = "rate_limit")
    int rateLimit;

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
