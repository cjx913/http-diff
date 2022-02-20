package cn.cjx913.httpdiffy.entity;

import cn.cjx913.httpdiffy.content.HttpDiffResponseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class HttpDiffResult implements Serializable {
    private Long id;
    private String key;
    private HttpDiffResponseInfo candidate;
    private HttpDiffResponseInfo primary;
    private HttpDiffResponseInfo secondary;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
