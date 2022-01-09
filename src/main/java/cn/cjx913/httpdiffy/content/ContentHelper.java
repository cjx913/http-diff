package cn.cjx913.httpdiffy.content;

import cn.cjx913.httpdiffy.autoconfigure.HttpDiffyProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class ContentHelper implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static HttpDiffyProperties httpDiffyProperties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        httpDiffyProperties = applicationContext.getBean(HttpDiffyProperties.class);
    }

    public static HttpDiffContent createHttpDiffContent(String key,
                                                        HttpMethod method, String path, LinkedMultiValueMap<String, String> queryParams,
                                                        HttpHeaders headers, LinkedMultiValueMap<String, String> formData, Object body) {
        if (applicationContext == null) return null;

        HttpDiffContent httpDiffContent = new HttpDiffContent(key,
                httpDiffyProperties.getVersion(),
                httpDiffyProperties.getCandidateWebClient(), httpDiffyProperties.getMasterWebClients(),
                method, path, queryParams, headers, formData, body, httpDiffyProperties.getDenoise());
        applicationContext.getAutowireCapableBeanFactory().autowireBean(httpDiffContent);
        return httpDiffContent;
    }

    static HttpDiffRequestContent createHttpDiffRequestContent(HttpDiffContent httpDiffContent, String name, WebClient webClient) {
        if (applicationContext == null) return null;
        HttpDiffRequestContent httpDiffRequestContent = new HttpDiffRequestContent(httpDiffContent, name, webClient);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(httpDiffRequestContent);
        return httpDiffRequestContent;
    }
}
