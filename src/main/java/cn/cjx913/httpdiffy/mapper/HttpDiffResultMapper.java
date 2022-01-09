package cn.cjx913.httpdiffy.mapper;

import cn.cjx913.httpdiffy.entity.HttpDiffKey;
import cn.cjx913.httpdiffy.entity.HttpDiffResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HttpDiffResultMapper extends BaseMapper<HttpDiffResult> {

    @Select({"select DISTINCT t.key, count(t.key) totalCount, sum(case when t.result = 1 then 1 else 0 end) passCount",
            "from http_diff.http_diff_result t", "where t.version=#{version}", "group by t.key"})
    List<HttpDiffKey> selectHttpDiffKey(@Param("version") String version);
}
