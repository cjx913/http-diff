package cn.cjx913.httpdiffy.autoconfigure;

import cn.cjx913.httpdiffy.exception.HttpDiffyPropertiesException;
import io.netty.channel.ChannelOption;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@ConfigurationProperties(prefix = HttpDiffyProperties.PREFIX)
public class HttpDiffyProperties implements InitializingBean {
    public final static String PREFIX = "http.diffy";
    private AntPathMatcher antPathMatcher;

    @Setter
    @Getter
    @Builder.Default
    public String version = "";

    @Setter
    public String candidate;
    @Getter
    public WebClient candidateWebClient;

    @Setter
    @Builder.Default
    public Map<String, String> masters = new LinkedHashMap<>();
    @Getter
    @Builder.Default
    public Map<String, WebClient> masterWebClients = new LinkedHashMap<>();

    /**
     * 降噪机器数
     */
    @Getter
    @Setter
    private Integer denoise;

    @Setter
    @Getter
    private Set<String> keys = new HashSet<>();
    @Setter
    @Getter
    private String swaggerUrl;

    @Setter
    @Getter
    private String h2WebServerPort = "8082";
    @Setter
    @Getter
    private String h2TcpServerPort = "9092";

    {
        antPathMatcher = new AntPathMatcher();
        antPathMatcher.setCachePatterns(true);
        antPathMatcher.setCaseSensitive(true);

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (!StringUtils.hasText(candidate)) {
            throw new HttpDiffyPropertiesException("请配置" + PREFIX + ".candidate");
        }
        if (CollectionUtils.isEmpty(masters)) {
            throw new HttpDiffyPropertiesException("请配置" + PREFIX + ".masters");
        }

        candidateWebClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(10))
                        .compress(true)
                        .keepAlive(true)
                        .followRedirect(true)))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .baseUrl(this.candidate)
                .build();
        masters.forEach((key, url) -> {
            masterWebClients.put(key, WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                            .responseTimeout(Duration.ofSeconds(10))
                            .compress(true)
                            .keepAlive(true)
                            .followRedirect(true)))
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .baseUrl(url)
                    .build());
        });

        //降噪机器数设置合理值
        int mastersSize = masters.size();
        if (denoise == null || (mastersSize > 1 && denoise < 2) || (denoise > mastersSize))
            denoise = mastersSize;

        if (StringUtils.hasText(swaggerUrl)) {
            WebClient webClient = WebClient.builder().build();
            WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                            .followRedirect(true)))
                    .build().get().uri(swaggerUrl)
                    .exchangeToMono(clientResponse -> {
                        return clientResponse.bodyToMono(LinkedHashMap.class);
                    }).subscribe(map -> {
                String basePath = (String) map.getOrDefault("basePath", "");
                LinkedHashMap<String, LinkedHashMap<String, Object>> paths = (LinkedHashMap<String, LinkedHashMap<String, Object>>) map.getOrDefault("paths", new LinkedHashMap<>(0));
                paths.forEach((path, methodMap) -> {
                    methodMap.keySet().forEach(method -> {
                        keys.add(method.toUpperCase(Locale.ROOT) + " " + basePath + path);
                    });
                });
            });

        }
    }

    public String matchKey(String s) {
        List<String> matchs = new ArrayList<>();
        for (String key : keys) {
            if (key.equals(s)) return key;
            if (antPathMatcher.match(key, s)) {
                matchs.add(key);
            }
        }
        if (matchs.size() == 1) return matchs.get(0);
        if (matchs.size() > 1) {
            if (matchs.contains(s)) return s;
            else return matchs.get(0);
        }
        if (CollectionUtils.isEmpty(matchs)) return s;
        return s;
    }

}
