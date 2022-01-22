package cn.cjx913.httpdiffy.content;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Mono;

public interface HttpDiffContent extends InitializingBean {


    HttpMethod getMethod();

    HttpHeaders getHeaders();

    String getPath();

    LinkedMultiValueMap<String, String> getQueryParams();

    LinkedMultiValueMap<String, String> getFormData();

    Object getBody();

    String getVersion();

    /**
     * @return candidateHttpDiffResponseInfo
     */
    Mono<HttpDiffResponseInfo> request();
}
