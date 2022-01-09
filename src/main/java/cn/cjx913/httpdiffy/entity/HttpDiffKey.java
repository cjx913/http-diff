package cn.cjx913.httpdiffy.entity;

import cn.cjx913.httpdiffy.content.HttpDiffResponseInfo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class HttpDiffKey implements Serializable {
    @Builder.Default
    private String key = "";
    @Builder.Default
    private long totalCount = 0L;
    @Builder.Default
    private long passCount = 0L;

    public double getPassRate() {
        if (totalCount == 0L || passCount == 0L) return 0.00D;

        return passCount * 1.00D / totalCount;
    }


}
