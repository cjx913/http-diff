package cn.cjx913.httpdiffy.server.impl;

import cn.cjx913.httpdiffy.dao.DiffRepository;
import cn.cjx913.httpdiffy.dao.RequestRepository;
import cn.cjx913.httpdiffy.dao.ResponseRepository;
import cn.cjx913.httpdiffy.entity.HttpDiffResult;
import cn.cjx913.httpdiffy.entity.po.DiffPO;
import cn.cjx913.httpdiffy.entity.po.RequestPO;
import cn.cjx913.httpdiffy.entity.po.ResponsePO;
import cn.cjx913.httpdiffy.server.HttpDiffyService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class HttpDiffyServiceImpl implements HttpDiffyService {
    @Autowired
    private DiffRepository diffRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private ResponseRepository responseRepository;

    @Override
    @Transactional
    public Mono<HttpDiffResult> save(HttpDiffResult httpDiffResult) {
        return saveDiff(DiffPO.builder()
                .mapping(httpDiffResult.getKey()).diff(0)
                .createTime(httpDiffResult.getStartTime()).endTime(httpDiffResult.getEndTime())
                .build())
                .map(diff -> {
                    httpDiffResult.setId(diff.getId());
                    return httpDiffResult;
                })
                .doOnSuccess(diff -> Flux.just(httpDiffResult.getCandidate(), httpDiffResult.getPrimary(), httpDiffResult.getSecondary())
                        .flatMap(responseInfo -> saveRequest(RequestPO.builder()
                                .diffId(diff.getId()).type(responseInfo.getName())
                                .method(JSON.toJSONString(responseInfo.getMethod())).headers(JSON.toJSONString(responseInfo.getRequestHeaders()))
                                .path(responseInfo.getPath()).queryParams(JSON.toJSONString(responseInfo.getQueryParams()))
                                .formData(JSON.toJSONString(responseInfo.getFormData())).body(JSON.toJSONString(responseInfo.getRequestBody()))
                                .build())
                                .doOnSuccess(request -> saveResponse(ResponsePO.builder()
                                        .requestId(request.getId())
                                        .headers(JSON.toJSONString(responseInfo.getResponseHeaders())).body(JSON.toJSONString(responseInfo.getResponseBody()))
                                        .build())
                                        .subscribe()))
                        .subscribe())
                ;
    }

    @Override
    @Transactional
    public Mono<DiffPO> saveDiff(DiffPO diff) {
        return diffRepository.save(diff);
    }

    @Override
    @Transactional
    public Mono<RequestPO> saveRequest(RequestPO request) {
        return requestRepository.save(request);
    }

    @Override
    @Transactional
    public Mono<ResponsePO> saveResponse(ResponsePO response) {
        return responseRepository.save(response);
    }
}
