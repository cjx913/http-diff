package cn.cjx913.httpdiffy.dao;

import cn.cjx913.httpdiffy.entity.po.ResponseBodyParameterPO;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ResponseBodyParameterRepository extends R2dbcRepository<ResponseBodyParameterPO, Long> {
}
