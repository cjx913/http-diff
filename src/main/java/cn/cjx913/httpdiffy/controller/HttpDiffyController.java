package cn.cjx913.httpdiffy.controller;

import io.netty.handler.codec.http.HttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
public class HttpDiffyController {

//    @ResponseBody
//    @RequestMapping(value = "/**")
//    public Mono<Object> path(@RequestHeader(required = false) HttpHeaders headers,
//                             @RequestParam(required = false) MultiValueMap<String,Object> queryParams,
//                             @RequestBody(required = false) Object body,
//                             ServerWebExchange exchange) {
//        ServerHttpRequest request = exchange.getRequest();
//        String requestId = request.getId();
//        RequestPath path = request.getPath();
//
//        return Mono.just(body);
//    }

//    @ResponseBody
//    @RequestMapping(value = "/httpdiff/**")
//    public Mono<Object> path(ServerWebExchange exchange) {
//        ServerHttpRequest request = exchange.getRequest();
//
//
//        return Mono.just("");
//    }
}
