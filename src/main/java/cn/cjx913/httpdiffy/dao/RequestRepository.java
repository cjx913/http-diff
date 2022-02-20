package cn.cjx913.httpdiffy.dao;

import cn.cjx913.httpdiffy.entity.po.RequestPO;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface RequestRepository extends R2dbcRepository<RequestPO, Long> {
}
