package cn.cjx913.httpdiffy;

import cn.cjx913.httpdiffy.autoconfigure.HttpDiffyProperties;
import cn.cjx913.httpdiffy.content.ContentHelper;
import cn.cjx913.httpdiffy.content.HttpDiffContent;
import cn.cjx913.httpdiffy.content.HttpDiffResponseInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({HttpDiffyProperties.class})
@MapperScan(basePackages = {"cn.cjx913.httpdiffy.mapper"})
public class HttpDiffyApplication {
    @Autowired
    private HttpDiffyProperties properties;

    public static void main(String[] args) {
        SpringApplication.run(HttpDiffyApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> httpdiffRouter() {
        return route(
                path("/httpdiff/**").and(methods(GET, POST, PUT, DELETE)),
                this::handle
        );
    }

    private Mono<ServerResponse> handle(ServerRequest request) {
        Optional<MediaType> mediaType = request.headers().contentType();
        if (!mediaType.isPresent()
                || mediaTypeEquals(APPLICATION_JSON, mediaType.get())) {

            return request.bodyToMono(Object.class)
                    .cache()
                    .defaultIfEmpty("__NULL__")
                    .flatMap(body -> handle(request, "__NULL__".equals(body) ? null : body, new LinkedMultiValueMap<>(0)));
        }
        if (mediaTypeEquals(APPLICATION_FORM_URLENCODED, mediaType.get())) {
            return request.formData()
                    .cache()
                    .defaultIfEmpty(new LinkedMultiValueMap<String, String>(0))
                    .flatMap(formData -> handle(request, null, formData));
        }

        return ServerResponse.ok().body(Mono.just(new LinkedHashMap(0)), LinkedHashMap.class);
    }

    private Mono<ServerResponse> handle(ServerRequest request, Object body, MultiValueMap<String, String> formData) {
        String path = request.path().substring(9);
        String methodName = request.methodName();
        String s = methodName.toUpperCase() + " " + path;
        String matchKey = properties.matchKey(s);
        if (!StringUtils.hasText(matchKey)) {
            matchKey = "";
        }

        return Mono.just(ContentHelper.createHttpDiffContent(matchKey, request.method(), path,
                new LinkedMultiValueMap<>(request.queryParams()), request.headers().asHttpHeaders(),
                new LinkedMultiValueMap<>(formData), body))
                .flatMap(HttpDiffContent::request)
                .flatMap(candidateHttpDiffResponseInfo -> {
                    Object responseBody = candidateHttpDiffResponseInfo.getResponseBody();
                    Class<?> clazz = Void.class;
                    if (responseBody != null) {
                        clazz = responseBody.getClass();
                    }
                    return ServerResponse.status(candidateHttpDiffResponseInfo.getHttpStatus())
                            .headers(headers -> headers.addAll(candidateHttpDiffResponseInfo.getResponseHeaders()))
                            .body(Mono.justOrEmpty(responseBody), clazz);
                });
    }

    private boolean mediaTypeEquals(MediaType mediaType1, MediaType mediaType2) {
        if (mediaType1 != null && mediaType2 != null) {
            return mediaType1.getType().equals(mediaType2.getType())
                    && mediaType1.getSubtype().equals(mediaType2.getSubtype());
        }
        return false;
    }
}
