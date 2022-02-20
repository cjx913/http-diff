package cn.cjx913.httpdiffy.dao;

import cn.cjx913.httpdiffy.entity.po.DiffPO;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface DiffRepository extends R2dbcRepository<DiffPO, Long> {
}
