package cn.cjx913.httpdiffy.content;

import cn.cjx913.httpdiffy.entity.HttpDiffResult;
import cn.cjx913.httpdiffy.exception.HttpDiffyException;
import cn.cjx913.httpdiffy.server.HttpDiffyService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode
public class MasterHttpDiffContent extends DefaultHttpDiffContent implements Serializable {

    @Getter
    private Map<String, HttpDiffRequestContent> masters;
    /**
     * 降噪机器数
     */
    @Getter
    private int denoise;

    @Setter
    @Autowired
    private HttpDiffyService httpDiffyService;

    MasterHttpDiffContent(Map<String, WebClient> masterWebClients, Integer denoise, String key, String version, WebClient candidateWebClient,
                          HttpMethod method, String path, HttpHeaders headers, LinkedMultiValueMap<String, String> queryParams,
                          LinkedMultiValueMap<String, String> formData, Object body) {
        super(key, version, candidateWebClient, method, path, headers, queryParams, formData, body);

        if (CollectionUtils.isEmpty(masterWebClients)) {
            throw new HttpDiffyException("无效masterWebClients!");
        }

        this.masters = new LinkedHashMap<>(masterWebClients.size());
        masterWebClients.forEach((name, webClient) -> {
            this.masters.put(name, ContentHelper.createHttpDiffRequestContent(this, name, webClient));
        });
        //降噪机器数设置合理值
        int mastersSize = masterWebClients.size();
        if (denoise == null || (mastersSize > 1 && denoise < 2) || (denoise > mastersSize))
            denoise = mastersSize;
        this.denoise = denoise;
    }

    public Mono<HttpDiffResponseInfo> request() {
        return candidate.request()
                .doOnSuccess(candidateHttpDiffResponseInfo -> {
                    Mono.fromCallable(() -> HttpDiffResult.builder()
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
                    boolean result = this.result(httpDiffResult);
                    log.info("httpDiffResult:{}", httpDiffResult);
                    log.info("result:{}", result);
                })
                ;
    }

    private boolean result(HttpDiffResult httpDiffResult) {
        HttpDiffResponseInfo candidateHttpDiffResponseInfo = null;
        List<HttpDiffResponseInfo> masterHttpDiffResponseInfos = null;

        int masterHttpDiffResponseInfoSize = masterHttpDiffResponseInfos.size();
        int denoise = masterHttpDiffResponseInfoSize < this.denoise ? masterHttpDiffResponseInfoSize : this.denoise;

        Map<String, List<Object>> bodyAnalysisJsonPathValue = analysisJsonPathValue(masterHttpDiffResponseInfos.stream()
                .map(HttpDiffResponseInfo::getResponseBody)
                .collect(Collectors.toList()));
        Map<String, Object> expectJsonPathValue = expectJsonPathValue(bodyAnalysisJsonPathValue, denoise);

        EqualsJsonPathValueInfo equalsJsonPathValue = equalsJsonPathValue(candidateHttpDiffResponseInfo.getResponseBody(), expectJsonPathValue);

        return false;
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
            if (read instanceof Number) {
                read = new BigDecimal(read.toString());
            }
            Object value = entry.getValue();
            if (value instanceof Number) {
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
