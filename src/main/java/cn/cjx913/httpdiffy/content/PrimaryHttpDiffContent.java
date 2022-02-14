package cn.cjx913.httpdiffy.content;

import cn.cjx913.httpdiffy.entity.HttpDiffResult;
import cn.cjx913.httpdiffy.exception.HttpDiffyException;
import cn.cjx913.httpdiffy.jsondiff.JsonPath;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@ToString(callSuper = true)
public class PrimaryHttpDiffContent extends DefaultHttpDiffContent {
    @Getter
    private HttpDiffRequestContent primary;
    @Getter
    private HttpDiffRequestContent secondary;

    PrimaryHttpDiffContent(WebClient primaryWebClient, WebClient secondaryWebClient,
                           String key, String version, WebClient candidateWebClient, HttpMethod method, String path,
                           HttpHeaders headers, LinkedMultiValueMap<String, String> queryParams,
                           LinkedMultiValueMap<String, String> formData, Object body) {
        super(key, version, candidateWebClient, method, path, headers, queryParams, formData, body);
        if (primaryWebClient == null || secondaryWebClient == null) {
            throw new HttpDiffyException("primaryWebClient和!secondaryWebClient");
        }
        this.primary = ContentHelper.createHttpDiffRequestContent(this, "primary", primaryWebClient);
        this.secondary = ContentHelper.createHttpDiffRequestContent(this, "secondary", secondaryWebClient);
    }

    @Override
    public Mono<HttpDiffResponseInfo> request() {
        return candidate.request()
                .doOnSuccess(candidateHttpDiffResponseInfo -> {
                    Mono.fromCallable(() -> HttpDiffResult.builder()
                            .version(this.version).key(this.key).createTime(LocalDateTime.now())
                            .candidate(candidateHttpDiffResponseInfo)
                            .build())
                            .flatMap(this::request)
                            .subscribe();
                });
    }

    private Mono<List<HttpDiffResponseInfo>> request(HttpDiffResult httpDiffResult) {
        return Flux.merge(Arrays.asList(primary.request(), secondary.request()))
                .collectList()
                .doOnSuccess(masterHttpDiffResponseInfos -> {
                    httpDiffResult.setPrimary(masterHttpDiffResponseInfos.get(0));
                    httpDiffResult.setSecondary(masterHttpDiffResponseInfos.get(1));
                    boolean result = this.result(httpDiffResult);
                    log.info("result:{}", result);
                })
                ;
    }

    private boolean result(HttpDiffResult httpDiffResult) {

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
            if(value instanceof Number){
                value = new BigDecimal(value.toString());
            }
            String key = entry.getKey();
            //primary-secondary
            if (!secondaryPaths.containsKey(key)) {
                ignorePathValues.put(key, Arrays.asList(value));
            } else {
                Object secondaryValue = secondaryPaths.get(key);
                if(secondaryValue instanceof Number){
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
        HttpDiffResponseInfo candidateHttpDiffResponseInfo = httpDiffResult.getCandidate();
        Map<String, Object> candidatePaths = JsonPath.paths(candidateHttpDiffResponseInfo);
        Set<String> keySet = candidatePaths.keySet();
        for (String key : keySet) {
            if ((key == null || key.indexOf("$['response") != 0)
                    || ignorePathValues.containsKey(key)) continue;

            Object candidateValue = candidatePaths.get(key);
            actualPathValues.put(key, candidateValue);

            if (result && (!expectPathValues.containsKey(key)
                    || (candidateValue == null && expectPathValues.get(key) != null)
                    || (candidateValue != null && !candidateValue.equals(expectPathValues.get(key))))) {
                result = false;
            }
        }

        httpDiffResult.setExpectJsonPathValue(expectPathValues);
        httpDiffResult.setIgnoreJsonPathValue(ignorePathValues);
        httpDiffResult.setActualJsonPathValue(actualPathValues);
        httpDiffResult.setResult(result);

        return httpDiffResultService.save(httpDiffResult);
    }


}
