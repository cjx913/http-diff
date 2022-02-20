package cn.cjx913.httpdiffy.content;

import cn.cjx913.httpdiffy.entity.HttpDiffResult;
import cn.cjx913.httpdiffy.exception.HttpDiffyException;
import cn.cjx913.httpdiffy.jsondiff.JsonPath;
import cn.cjx913.httpdiffy.server.HttpDiffyService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@ToString(callSuper = true)
public class PrimaryHttpDiffContent extends DefaultHttpDiffContent {
    @Getter
    private HttpDiffRequestContent primary;
    @Getter
    private HttpDiffRequestContent secondary;

    @Setter
    @Autowired
    private HttpDiffyService httpDiffyService;

    PrimaryHttpDiffContent(WebClient primaryWebClient, WebClient secondaryWebClient,
                           String key, String version, WebClient candidateWebClient, HttpMethod method, String path,
                           HttpHeaders headers, LinkedMultiValueMap<String, String> queryParams,
                           LinkedMultiValueMap<String, String> formData, Object body) {
        super(key, version, candidateWebClient, method, path, headers, queryParams, formData, body);
        if (primaryWebClient == null || secondaryWebClient == null) {
            throw new HttpDiffyException("primaryWebClient和secondaryWebClient!");
        }
        this.primary = ContentHelper.createHttpDiffRequestContent(this, "primary", primaryWebClient);
        this.secondary = ContentHelper.createHttpDiffRequestContent(this, "secondary", secondaryWebClient);
    }

    @Override
    public Mono<HttpDiffResponseInfo> request() {
        return candidate.request()
                .doOnSuccess(candidateHttpDiffResponseInfo -> this.request(HttpDiffResult.builder()
                        .key(key).candidate(candidateHttpDiffResponseInfo)
                        .startTime(LocalDateTime.now())
                        .build())
                        .doOnSuccess(httpDiffResult -> this.save(httpDiffResult).subscribe())
                        .doOnSuccess(httpDiffResult -> this.compare(httpDiffResult).subscribe())
                        .subscribe());
    }

    private Mono<HttpDiffResult> request(HttpDiffResult httpDiffResult) {
        return Flux.just(this.primary, this.secondary)
                .delayElements(Duration.ofSeconds(1))
                .flatMap(HttpDiffRequestContent::request)
                .map(httpDiffResponseInfo -> {
                    String name = httpDiffResponseInfo.getName();
                    switch (name) {
                        case "primary":
                            httpDiffResult.setPrimary(httpDiffResponseInfo);
                            break;
                        case "secondary":
                            httpDiffResult.setSecondary(httpDiffResponseInfo);
                            break;
                    }
                    return httpDiffResponseInfo;
                })
                .then(Mono.just(httpDiffResult))
                ;
    }

    private Mono<HttpDiffResult> save(HttpDiffResult httpDiffResult) {
        httpDiffResult.setEndTime(LocalDateTime.now());
        return this.httpDiffyService.save(httpDiffResult);
    }

    private Mono<HttpDiffResult> compare(HttpDiffResult httpDiffResult) {
//        throw new HttpDiffyException("比对数据错误!");
        return Mono.empty();
    }

    private boolean result(HttpDiffResult httpDiffResult) {

        HttpDiffResponseInfo candidateHttpDiffResponseInfo = httpDiffResult.getCandidate();
        HttpDiffResponseInfo primaryHttpDiffResponseInfo = httpDiffResult.getPrimary();
        HttpDiffResponseInfo secondaryHttpDiffResponseInfo = httpDiffResult.getSecondary();

        Map<String, Object> primaryPaths = JsonPath.paths(primaryHttpDiffResponseInfo);
        Map<String, Object> secondaryPaths = JsonPath.paths(secondaryHttpDiffResponseInfo);

        Map<String, Object> expectPathValues = new LinkedHashMap<>();
        LinkedMultiValueMap<String, Object> ignorePathValues = new LinkedMultiValueMap<>();

        Set<Map.Entry<String, Object>> primaryEntries = primaryPaths.entrySet();
        for (Map.Entry<String, Object> entry : primaryEntries) {
            Object value = entry.getValue();
            if (!(value instanceof CharSequence || value instanceof Number || value instanceof Boolean)) {
                continue;
            }
            if (value instanceof Number) {
                value = new BigDecimal(value.toString());
            }
            String key = entry.getKey();
            //primary-secondary
            if (!secondaryPaths.containsKey(key)) {
                ignorePathValues.put(key, Arrays.asList(value));
            } else {
                Object secondaryValue = secondaryPaths.get(key);
                if (secondaryValue instanceof Number) {
                    secondaryValue = new BigDecimal(secondaryValue.toString());
                }
                if ((value == null && secondaryValue != null)
                        || (value != null && !value.equals(secondaryValue))) {
                    ignorePathValues.put(key, Arrays.asList(value, secondaryValue));
                } else {
                    expectPathValues.put(key, value);
                }
            }
        }

        boolean result = true;
        Map<String, Object> actualPathValues = new LinkedHashMap<>();

        Map<String, Object> candidatePaths = JsonPath.paths(candidateHttpDiffResponseInfo);
        Set<String> keySet = candidatePaths.keySet();
        for (String key : keySet) {
            if ((key == null || !key.startsWith("$['response"))
                    || ignorePathValues.containsKey(key)) continue;

            Object candidateValue = candidatePaths.get(key);
            actualPathValues.put(key, candidateValue);

            if (result && (!expectPathValues.containsKey(key)
                    || (candidateValue == null && expectPathValues.get(key) != null)
                    || (candidateValue != null && !candidateValue.equals(expectPathValues.get(key))))) {
                result = false;
            }
        }

        return false;
    }


}
