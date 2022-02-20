package cn.cjx913.httpdiffy.content;

import cn.cjx913.httpdiffy.server.HttpDiffRequestService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.Serializable;

@Slf4j
@ToString(exclude = {"httpDiffRequestService", "webClient", "httpDiffContent"})
@EqualsAndHashCode
public class HttpDiffRequestContent implements Serializable {

    @Autowired
    private HttpDiffRequestService httpDiffRequestService;

    private WebClient webClient;

    @Getter
    private String name;
    @Getter
    private HttpDiffContent httpDiffContent;

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

    public String getVersion() {
        return httpDiffContent.getVersion();
    }

    HttpDiffRequestContent(HttpDiffContent httpDiffContent, String name, WebClient webClient) {
        this.webClient = webClient;
        this.name = name;
        this.httpDiffContent = httpDiffContent;
        this.method = httpDiffContent.getMethod();
        this.headers = httpDiffContent.getHeaders();
        this.path = httpDiffContent.getPath();
        this.queryParams = httpDiffContent.getQueryParams();
        this.formData = httpDiffContent.getFormData();
        this.body = httpDiffContent.getBody();
    }

    private void requestBefore() {
        this.path = httpDiffRequestService.getPath(this);
        this.headers = httpDiffRequestService.getHttpHeaders(this);
        this.queryParams = httpDiffRequestService.getQueryParams(this);
        this.body = httpDiffRequestService.getBody(this);
        this.formData = httpDiffRequestService.getFormData(this);
    }

    public Mono<HttpDiffResponseInfo> request() {
        requestBefore();
        log.debug("请求参数：{}", this);

        WebClient.RequestBodySpec spec = this.webClient
                .method(this.getMethod())
                .uri(builder -> builder.path(this.path)
                        .queryParams(this.queryParams)
                        .build())
                .headers(headers -> {
                    if (this.headers != null && !this.headers.isEmpty()) {
                        headers.clear();
                        headers.addAll(this.headers);
                    }
                });
        if (this.body != null) {
            spec.bodyValue(this.body);
        } else if (this.formData != null) {
            spec.bodyValue(this.formData);
        }
        return spec
                .retrieve()
                .toEntity(Object.class)
                .onErrorResume(throwable -> {
                    log.error("请求异常!" + throwable.getMessage(), throwable);
                    return Mono.just(new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR));
                })
                .map(responseEntity -> {
                    log.debug("响应体：{}", responseEntity);
                    return HttpDiffResponseInfo.builder()
                            .name(this.name)
                            .method(this.method).path(this.path)
                            .queryParams(this.queryParams).formData(this.formData)
                            .requestHeaders(new LinkedMultiValueMap<>(this.headers))
                            .requestBody(this.body)
                            .httpStatus(responseEntity.getStatusCode())
                            .responseHeaders(responseEntity.getHeaders())
                            .responseBody(responseEntity.getBody())
                            .build();
                })
                ;
    }


}
