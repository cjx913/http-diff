package cn.cjx913.httpdiffy.server;

import cn.cjx913.httpdiffy.entity.HttpDiffResult;
import cn.cjx913.httpdiffy.entity.po.DiffPO;
import cn.cjx913.httpdiffy.entity.po.RequestPO;
import cn.cjx913.httpdiffy.entity.po.ResponsePO;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

public interface HttpDiffyService {
    @Transactional
    Mono<HttpDiffResult> save(HttpDiffResult httpDiffResult);

    @Transactional
    Mono<DiffPO> saveDiff(DiffPO diff);

    @Transactional
    Mono<RequestPO> saveRequest(RequestPO request);

    @Transactional
    Mono<ResponsePO> saveResponse(ResponsePO response);


}
