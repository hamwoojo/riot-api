package riot.api.data.engineer.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
@Slf4j
public class WebClientConfig {

    private static final int CONNECT_TIMEOUT_MILLISECONDS = 3000;
    private static final int READ_TIMEOUT_SECONDS = 5;
    private static final int MAX_IN_MEMORY_SIZE = 2 * 1024 * 1024;
    private static final int WRITE_TIMEOUT_SECONDS = 60;


    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(
                        client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLISECONDS)
                                .doOnConnected(
                                        conn -> conn.addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_SECONDS))
                                                .addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT_SECONDS))
                                )
                );

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE))
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(
                        (req, next) -> next.exchange(
                                ClientRequest.from(req).header("from", "webclient").build()
                        )
                )
                .filter(
                        ExchangeFilterFunction.ofRequestProcessor(
                                clientRequest -> {
//                                    log.info(">>>>>>>>>> REQUEST <<<<<<<<<<");
//                                    log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
//                                    clientRequest.headers().forEach(
//                                            (name, values) -> values.forEach(value -> log.info("{} : {}", name, value))
//                                    );
                                    return Mono.just(clientRequest);
                                }
                        )
                )
                .filter(
                        ExchangeFilterFunction.ofResponseProcessor(
                                clientResponse -> {
//                                    log.info(">>>>>>>>>> RESPONSE <<<<<<<<<<");
//                                    log.info("Status Code : " + clientResponse.statusCode());
                                    if(clientResponse.statusCode().is4xxClientError() || clientResponse.statusCode().is5xxServerError()){
                                        log.error("--- ERROR LOG ---");
                                        clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.info("{} : {}", name, value)));
                                    }
                                    return Mono.just(clientResponse);
                                }
                        )
                )
                .exchangeStrategies(exchangeStrategies)
                .defaultHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.3")
                .defaultCookie("httpclient-type", "webclient")
                .build();
    }
}
