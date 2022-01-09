package cn.cjx913.httpdiffy.server.impl;

import cn.cjx913.httpdiffy.autoconfigure.HttpDiffyProperties;
import cn.cjx913.httpdiffy.entity.HttpDiffKey;
import cn.cjx913.httpdiffy.entity.HttpDiffResult;
import cn.cjx913.httpdiffy.mapper.HttpDiffResultMapper;
import cn.cjx913.httpdiffy.server.HttpDiffResultService;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HttpDiffResultServiceImpl extends ServiceImpl<HttpDiffResultMapper, HttpDiffResult> implements HttpDiffResultService {
    @Autowired
    private HttpDiffyProperties httpDiffyProperties;
    @Override
    public List<HttpDiffKey> getHttpDiffKey() {
        return getBaseMapper().selectHttpDiffKey(httpDiffyProperties.getVersion());
    }
}
