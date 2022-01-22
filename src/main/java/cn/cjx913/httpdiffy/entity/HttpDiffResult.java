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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName(schema = "http_diff", value = "http_diff_result", autoResultMap = true)
public class HttpDiffResult implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String version;
    @TableField("`key`")
    private String key;
    private Integer denoise;

    @TableField(value = "`candidate`",typeHandler = FastjsonTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private HttpDiffResponseInfo candidate;
    @TableField(value = "`primary`",typeHandler = FastjsonTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private HttpDiffResponseInfo primary;
    @TableField(value = "`secondary`",typeHandler = FastjsonTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private HttpDiffResponseInfo secondary;
    @TableField(typeHandler = FastjsonTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private List<HttpDiffResponseInfo> masters;

    private Boolean result;
    @TableField(typeHandler = FastjsonTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private Map<String, Object> expectJsonPathValue;
    @TableField(typeHandler = FastjsonTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private LinkedMultiValueMap<String, Object> ignoreJsonPathValue ;
    @TableField(typeHandler = FastjsonTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private Map<String, Object> actualJsonPathValue ;
    private LocalDateTime createTime;


}
