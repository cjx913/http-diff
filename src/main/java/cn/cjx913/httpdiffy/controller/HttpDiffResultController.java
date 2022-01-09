package cn.cjx913.httpdiffy.controller;

import cn.cjx913.httpdiffy.autoconfigure.HttpDiffyProperties;
import cn.cjx913.httpdiffy.entity.HttpDiffKey;
import cn.cjx913.httpdiffy.entity.HttpDiffResult;
import cn.cjx913.httpdiffy.server.HttpDiffResultService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/httpDiffResult")
public class HttpDiffResultController {
    @Autowired
    private HttpDiffyProperties httpDiffyProperties;
    @Autowired
    private HttpDiffResultService httpDiffResultService;

    @GetMapping("/keys")
    public Flux<HttpDiffKey> keys() {
        return Flux.fromIterable(httpDiffResultService.getHttpDiffKey());
    }

    @GetMapping
    public Mono<IPage<HttpDiffResult>> get(Page<HttpDiffResult> page, HttpDiffResult httpDiffResult) {
        if (!StringUtils.hasText(httpDiffResult.getVersion()))
            httpDiffResult.setVersion(httpDiffyProperties.getVersion());
        return Mono.just(httpDiffResultService.page(page, Wrappers.lambdaQuery(httpDiffResult)
                .orderByDesc(HttpDiffResult::getId)));
    }
}
