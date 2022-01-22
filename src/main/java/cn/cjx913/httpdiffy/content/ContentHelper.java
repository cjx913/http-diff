package cn.cjx913.httpdiffy.content;

import cn.cjx913.httpdiffy.autoconfigure.HttpDiffyProperties;
import cn.cjx913.httpdiffy.exception.HttpDiffyException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ContentHelper implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static HttpDiffyProperties httpDiffyProperties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        httpDiffyProperties = applicationContext.getBean(HttpDiffyProperties.class);
    }

    public static HttpDiffContent createHttpDiffContent(String key, HttpMethod method, String path, HttpHeaders headers,
                                                        LinkedMultiValueMap<String, String> queryParams,
                                                        LinkedMultiValueMap<String, String> formData, Object body) {
        HttpDiffContent httpDiffContent = null;
        if (!CollectionUtils.isEmpty(httpDiffyProperties.getMasterWebClients())) {
            httpDiffContent = new MasterHttpDiffContent(
                    httpDiffyProperties.getMasterWebClients(), httpDiffyProperties.getDenoise(),
                    key, httpDiffyProperties.getVersion(), httpDiffyProperties.getCandidateWebClient(),
                    method, path, headers, queryParams, formData, body);
        } else if (httpDiffyProperties.getPrimaryWebClient() != null && httpDiffyProperties.getSecondaryWebClient() != null) {
            httpDiffContent = new PrimaryHttpDiffContent(
                    httpDiffyProperties.getPrimaryWebClient(), httpDiffyProperties.getSecondaryWebClient(),
                    key, httpDiffyProperties.getVersion(),
                    httpDiffyProperties.getCandidateWebClient(),
                    method, path, headers, queryParams, formData, body);
        }
        if (httpDiffContent == null) {
            throw new HttpDiffyException("创建HttpDiffContent对象错误!无效参数");
        }
        applicationContext.getAutowireCapableBeanFactory().autowireBean(httpDiffContent);
        try {
            httpDiffContent.afterPropertiesSet();
        } catch (Exception e) {
            throw new HttpDiffyException("创建HttpDiffContent对象错误!");
        }
        return httpDiffContent;
    }


    static HttpDiffRequestContent createHttpDiffRequestContent(HttpDiffContent httpDiffContent, String name, WebClient webClient) {
        HttpDiffRequestContent httpDiffRequestContent = new HttpDiffRequestContent(httpDiffContent, name, webClient);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(httpDiffRequestContent);
        return httpDiffRequestContent;
    }
}
