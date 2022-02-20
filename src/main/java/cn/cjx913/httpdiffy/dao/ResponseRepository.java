package cn.cjx913.httpdiffy.dao;

import cn.cjx913.httpdiffy.entity.po.ResponsePO;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ResponseRepository extends R2dbcRepository<ResponsePO, Long> {
}
