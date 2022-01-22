package cn.cjx913.httpdiffy.content;

import cn.cjx913.httpdiffy.exception.HttpDiffContentException;
import cn.cjx913.httpdiffy.server.HttpDiffResultService;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@ToString
@EqualsAndHashCode
public abstract class DefaultHttpDiffContent implements HttpDiffContent {
    @Autowired
    protected HttpDiffResultService httpDiffResultService;

    /**
     * 匹配的url
     */
    @Getter
    protected String key;

    @Getter
    @Builder.Default
    protected String version = "";
    @Getter
    protected HttpDiffRequestContent candidate;

    @Getter
    protected HttpMethod method;
    @Getter
    protected HttpHeaders headers;
    /**
     * 请求路径
     */
    @Getter
    protected String path;
    @Getter
    protected LinkedMultiValueMap<String, String> queryParams;
    @Getter
    protected LinkedMultiValueMap<String, String> formData;
    @Getter
    protected Object body;

    protected WebClient candidateWebClient;

    DefaultHttpDiffContent(String key, String version, WebClient candidateWebClient, HttpMethod method,
                           String path, HttpHeaders headers, LinkedMultiValueMap<String, String> queryParams,
                           LinkedMultiValueMap<String, String> formData, Object body) {
        if (!StringUtils.hasText(key)
                || !StringUtils.hasText(version)
                || candidateWebClient == null) {
            throw new HttpDiffContentException("初始化HttpDiffContent错误!无效参数");
        }
        this.key = key;
        this.version = version;
        this.path = StringUtils.hasText(path) ? path : "";
        this.method = method == null ? HttpMethod.GET : method;
        this.headers = headers == null ? new HttpHeaders() : headers;
        this.queryParams = queryParams == null ? new LinkedMultiValueMap<>(0) : queryParams;
        this.formData = formData == null ? new LinkedMultiValueMap<>(0) : formData;
        this.body = body;

        this.candidateWebClient = candidateWebClient;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        this.candidate = ContentHelper.createHttpDiffRequestContent(this, "candidate", candidateWebClient);
    }



}
