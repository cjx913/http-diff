package cn.cjx913.httpdiffy.server;

import cn.cjx913.httpdiffy.content.HttpDiffRequestContent;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public interface HttpDiffRequestService {
    default HttpHeaders getHttpHeaders(HttpDiffRequestContent httpDiffRequestContent) {
        return httpDiffRequestContent.getHeaders();
    }

    default Object getBody(HttpDiffRequestContent httpDiffRequestContent) {
        return httpDiffRequestContent.getBody();
    }

    default String getPath(HttpDiffRequestContent httpDiffRequestContent) {
        return httpDiffRequestContent.getPath();
    }

    default LinkedMultiValueMap<String, String> getQueryParams(HttpDiffRequestContent httpDiffRequestContent) {
        return httpDiffRequestContent.getQueryParams();
    }

    default LinkedMultiValueMap<String, String> getFormData(HttpDiffRequestContent httpDiffRequestContent) {
        return httpDiffRequestContent.getFormData();
    }
}
