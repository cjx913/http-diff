package cn.cjx913.httpdiffy.server.impl;

import cn.cjx913.httpdiffy.content.HttpDiffRequestContent;
import cn.cjx913.httpdiffy.server.HttpDiffRequestService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Service
public class HttpDiffRequestServiceImpl implements HttpDiffRequestService {
    @Override
    public LinkedMultiValueMap<String, String> getQueryParams(HttpDiffRequestContent httpDiffRequestContent) {

        if (!"candidate".equals(httpDiffRequestContent.getName())) {
            LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(1);
            queryParams.set("username", "cjx91311");
            return queryParams;
        }
        return httpDiffRequestContent.getQueryParams();
    }
}
