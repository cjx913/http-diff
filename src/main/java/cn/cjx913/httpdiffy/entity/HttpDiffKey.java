package cn.cjx913.httpdiffy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

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
