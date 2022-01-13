package cn.cjx913.httpdiffy.entity;

import cn.cjx913.httpdiffy.content.HttpDiffResponseInfo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.GsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
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
@TableName(schema = "HTTP_DIFF",value = "http_diff_result", autoResultMap = true)
public class HttpDiffResult implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String version;
    @TableField("`key`")
    private String key;
    private Integer denoise;

    @TableField(typeHandler = FastjsonTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private HttpDiffResponseInfo candidate;
    @TableField(typeHandler = FastjsonTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private List<HttpDiffResponseInfo> masters;

    private Boolean result;
    @TableField(typeHandler = FastjsonTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private Map<String, Object> expectJsonPathValue;


}
