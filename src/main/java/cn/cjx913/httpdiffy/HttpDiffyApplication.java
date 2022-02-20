package cn.cjx913.httpdiffy;

import cn.cjx913.httpdiffy.autoconfigure.HttpDiffyProperties;
import cn.cjx913.httpdiffy.content.ContentHelper;
import cn.cjx913.httpdiffy.content.HttpDiffContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.*;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({HttpDiffyProperties.class})
public class HttpDiffyApplication {
    @Autowired
    private HttpDiffyProperties properties;

    public static void main(String[] args) {
        SpringApplication.run(HttpDiffyApplication.class, args);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WebServer httpdiffyWebServer(@Autowired HttpHandler httpHandler, @Autowired HttpDiffyProperties properties) {
        NettyReactiveWebServerFactory nettyReactiveWebServerFactory = new NettyReactiveWebServerFactory(properties.getPort());
        WebServer webServer = nettyReactiveWebServerFactory.getWebServer(httpHandler);
        return webServer;
    }

    @Bean
    public RouterFunction<ServerResponse> httpdiffRouter(@Autowired HttpDiffyProperties properties) {
        return route(new RequestPredicate() {
                    @Override
                    public boolean test(ServerRequest request) {
                        Optional<InetSocketAddress> optional = request.localAddress();
                        if (optional.isPresent() && properties.getPort() == optional.get().getPort()) {
                            return true;
                        }
                        return false;
                    }
                }
                        .and(path(properties.getPath()))
                        .and(methods(properties.getMethods())),
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
        String path = request.path();
        String methodName = request.methodName();
        String s = methodName.toUpperCase() + " " + path;
        String matchKey = properties.matchKey(s);
        if (!StringUtils.hasText(matchKey)) {
            matchKey = "";
        }

        return Mono.just(ContentHelper.createHttpDiffContent(matchKey, request.method(), path,
                request.headers().asHttpHeaders(), new LinkedMultiValueMap<>(request.queryParams()),
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
