package cn.cjx913.httpdiffy.dao;

import cn.cjx913.httpdiffy.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface UserRepository extends R2dbcRepository<User, Long> {
}
