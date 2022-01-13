package cn.cjx913.httpdiffy.content;

import cn.cjx913.httpdiffy.entity.HttpDiffResult;
import cn.cjx913.httpdiffy.exception.HttpDiffContentException;
import cn.cjx913.httpdiffy.server.HttpDiffResultService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@ToString
@EqualsAndHashCode
public class HttpDiffContent implements Serializable {
    @Autowired
    private HttpDiffResultService httpDiffResultService;

    /**
     * 匹配的url
     */
    @Getter
    private String key;

    @Getter
    @Builder.Default
    private String version = "";
    @Getter
    private HttpDiffRequestContent candidate;
    @Getter
    private Map<String, HttpDiffRequestContent> masters;

    @Getter
    private HttpMethod method;
    @Getter
    private HttpHeaders headers;
    /**
     * 请求路径
     */
    @Getter
    private String path;
    @Getter
    private LinkedMultiValueMap<String, String> queryParams;
    @Getter
    private LinkedMultiValueMap<String, String> formData;
    @Getter
    private Object body;

    /**
     * 降噪机器数
     */
    @Getter
    private int denoise;

    HttpDiffContent(String key, String version, WebClient candidateWebClient, Map<String, WebClient> masterWebClients,
                    HttpMethod method, String path, LinkedMultiValueMap<String, String> queryParams,
                    HttpHeaders headers, LinkedMultiValueMap<String, String> formData, Object body, Integer denoise) {
        if (!StringUtils.hasText(key)
                || !StringUtils.hasText(version)
                || candidateWebClient == null
                || CollectionUtils.isEmpty(masterWebClients)
                || method == null) {
            throw new HttpDiffContentException("初始化HttpDiffContent错误!无效参数");
        }
        this.key = key;
        this.version = version;
        this.method = method;
        this.headers = headers;
        this.path = StringUtils.hasText(path) ? path : "";
        this.queryParams = queryParams;
        this.formData = formData;
        this.body = body;

        //降噪机器数设置合理值
        int mastersSize = masterWebClients.size();
        if (denoise == null || (mastersSize > 1 && denoise < 2) || (denoise > mastersSize))
            denoise = mastersSize;
        this.denoise = denoise;

        this.candidate = ContentHelper.createHttpDiffRequestContent(this, "candidate", candidateWebClient);
        this.masters = new LinkedHashMap<>(masterWebClients.size());
        masterWebClients.forEach((name, webClient) -> {
            this.masters.put(name, ContentHelper.createHttpDiffRequestContent(this, name, webClient));
        });
    }

    /**
     * @return candidateHttpDiffResponseInfo
     */
    public Mono<HttpDiffResponseInfo> request() {
        return candidate.request()
                .doOnSuccess(candidateHttpDiffResponseInfo -> {
                    Mono.fromCallable(() -> HttpDiffResult.builder()
                            .version(this.version).key(this.key).denoise(denoise).createTime(LocalDateTime.now())
                            .candidate(candidateHttpDiffResponseInfo)
                            .build())
                            .flatMap(this::request)
                            .subscribe();
                });
    }

    private Mono<List<HttpDiffResponseInfo>> request(HttpDiffResult httpDiffResult) {
        List<Mono<HttpDiffResponseInfo>> list = new ArrayList<>(masters.size());
        masters.forEach((key, httpDiffRequestContent) -> {
            list.add(httpDiffRequestContent.request());
        });

        return Flux.merge(list)
                .collectList()
                .doOnSuccess(masterHttpDiffResponseInfos -> {
                    httpDiffResult.setMasters(masterHttpDiffResponseInfos);
                    boolean result = this.result(httpDiffResult);
                    log.info("httpDiffResult:{}", httpDiffResult);
                    log.info("result:{}", result);
                })
                ;
    }

    private boolean result(HttpDiffResult httpDiffResult) {
        HttpDiffResponseInfo candidateHttpDiffResponseInfo = httpDiffResult.getCandidate();
        List<HttpDiffResponseInfo> masterHttpDiffResponseInfos = httpDiffResult.getMasters();

        int masterHttpDiffResponseInfoSize = masterHttpDiffResponseInfos.size();
        int denoise = masterHttpDiffResponseInfoSize < this.denoise ? masterHttpDiffResponseInfoSize : this.denoise;
        httpDiffResult.setDenoise(denoise);

        Map<String, List<Object>> bodyAnalysisJsonPathValue = analysisJsonPathValue(masterHttpDiffResponseInfos.stream()
                .map(HttpDiffResponseInfo::getResponseBody)
                .collect(Collectors.toList()));
        Map<String, Object> expectJsonPathValue = expectJsonPathValue(bodyAnalysisJsonPathValue, denoise);
        httpDiffResult.setExpectJsonPathValue(expectJsonPathValue);

        EqualsJsonPathValueInfo equalsJsonPathValue = equalsJsonPathValue(candidateHttpDiffResponseInfo.getResponseBody(), expectJsonPathValue);
        httpDiffResult.setResult(equalsJsonPathValue.isEquals());

        return httpDiffResultService.save(httpDiffResult);
    }

    /**
     * 对象与jsonpath期望值对比
     *
     * @param object
     * @param jsonPathValue
     * @return
     */
    private EqualsJsonPathValueInfo equalsJsonPathValue(Object object, Map<String, Object> jsonPathValue) {
        if (object == null) {
            if (jsonPathValue == null || jsonPathValue.isEmpty()) {
                return new EqualsJsonPathValueInfo(true);
            } else {
                return new EqualsJsonPathValueInfo(false);
            }
        }

        if (jsonPathValue == null) {
            return new EqualsJsonPathValueInfo(false);
        }

//        EqualsJsonPathValueInfo equalsJsonPathValueInfo = new EqualsJsonPathValueInfo();
//        boolean equals = true;
//        Map<String, Object> paths = JSONPath.paths(object);
//        Set<Map.Entry<String, Object>> entries = paths.entrySet();
//        for (Map.Entry<String, Object> entry : entries) {
//            String path = entry.getKey();
//            Object value = entry.getValue();
//            JsonPathValueInfo jsonPathValueInfo = new JsonPathValueInfo(path, value);
//            if (!jsonPathValue.containsKey(path)) {
//                jsonPathValueInfo.setContainsKey(false);
//            } else {
//                Object o = jsonPathValue.get(path);
//                if (!InvalidObject.invalidObject.equals(o)) {
//                    jsonPathValueInfo.setExpectValue(o);
//                }
//            }
//            if (equals && !jsonPathValueInfo.isEquals()) {
//                equals = false;
//            }
//            equalsJsonPathValueInfo.getJsonPathValueInfos().add(jsonPathValueInfo);
//        }
//        equalsJsonPathValueInfo.setEquals(equals);
//        return equalsJsonPathValueInfo;

        String jsonString = object == null ? null : object instanceof String ? (String) object : JSON.toJSONString(object);
        Set<Map.Entry<String, Object>> entries = jsonPathValue.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String path = entry.getKey();
            Object read = JSONPath.read(jsonString, path);
            if(read instanceof Number){
                read = new BigDecimal(read.toString());
            }
            Object value = entry.getValue();
            if(value instanceof Number){
                value = new BigDecimal(value.toString());
            }
            if ((value == null && read == null) || (value != null && value.equals(read))) continue;
            return new EqualsJsonPathValueInfo(false);
        }
        return new EqualsJsonPathValueInfo(true);
    }

    @Setter
    @Getter
    public class EqualsJsonPathValueInfo implements Serializable {
        private boolean equals;
        @Builder.Default
        private List<JsonPathValueInfo> jsonPathValueInfos = new ArrayList<>();

        public EqualsJsonPathValueInfo() {
        }

        public EqualsJsonPathValueInfo(boolean equals) {
            this.equals = equals;
        }
    }

    @Setter
    @Getter
    public class JsonPathValueInfo implements Serializable {
        private String path;
        private Object value;
        private Object expectValue;
        @Builder.Default
        private boolean containsKey = true;

        private String message;

        public JsonPathValueInfo(String path, Object value) {
            this.path = path;
            this.value = value;
        }

        public boolean isEquals() {
            if (value == null) {
                if (expectValue == null) return true;
                else return false;
            }
            return value.equals(expectValue);
        }
    }

    /**
     * 每个jsonpath期望的值
     *
     * @param jsonPathValueMap
     * @param minExpectSameCount
     * @return
     */
    private Map<String, Object> expectJsonPathValue(@NonNull Map<String, List<Object>> jsonPathValueMap, int minExpectSameCount) {
        final int finalMinExpectSameCount = minExpectSameCount < 1 ? 1 : minExpectSameCount;
        Map<String, Object> map = new LinkedHashMap<>(jsonPathValueMap.size());
        jsonPathValueMap.forEach((path, values) -> {
            if (CollectionUtils.isEmpty(values) || values.size() < finalMinExpectSameCount) return;
            Map<Object, Long> collect = values.stream()
                    .collect(Collectors.groupingBy(o -> o, Collectors.counting()));
            Set<Map.Entry<Object, Long>> entries = collect.entrySet();
            Optional<Map.Entry<Object, Long>> max = entries.stream()
                    .max((o1, o2) -> o1.getValue() > o2.getValue() ? 1 : -1);
            if (max.isPresent()) {
                Map.Entry<Object, Long> objectLongEntry = max.get();
                if (objectLongEntry.getValue() < finalMinExpectSameCount) {
//                    map.put(path, InvalidObject.invalidObject);
                } else {
                    map.put(path, objectLongEntry.getKey());
                }
            } else {
//                map.put(path, InvalidObject.invalidObject);
            }
        });
        return map;
    }

    static class InvalidObject {
        private final static InvalidObject invalidObject = new InvalidObject();

        private InvalidObject() {
        }
    }

    /**
     * 分析jsonpath和值
     *
     * @param objects
     * @return
     */
    private Map<String, List<Object>> analysisJsonPathValue(List<Object> objects) {
        if (CollectionUtils.isEmpty(objects)) return new HashMap<>(0);
        Map<String, List<Object>> map = new LinkedHashMap<>();
        for (int i = 0; i < objects.size(); i++) {
            Object object = objects.get(i);
            if (object == null) continue;

            Map<String, Object> paths = JSONPath.paths(object);
            Set<Map.Entry<String, Object>> entries = paths.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                String path = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String
                        || value instanceof Number
                        || value instanceof Boolean) {
                    if (!map.containsKey(path)) {
                        List<Object> list = new ArrayList<>();
                        for (int j = 0; j < i; j++) {
                            list.add(null);
                        }
                        list.add(value);
                        map.put(path, list);
                    } else {
                        map.get(path).add(value);
                    }
                }
            }
        }
        return map;
    }

}
