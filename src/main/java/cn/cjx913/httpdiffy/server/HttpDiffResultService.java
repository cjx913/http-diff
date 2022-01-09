package cn.cjx913.httpdiffy.server;

import cn.cjx913.httpdiffy.entity.HttpDiffKey;
import cn.cjx913.httpdiffy.entity.HttpDiffResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface HttpDiffResultService extends IService<HttpDiffResult> {
    List<HttpDiffKey> getHttpDiffKey();
}
