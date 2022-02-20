package cn.cjx913.httpdiffy.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Table("diff")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DiffPO implements Serializable {
    @Id
    private Long id;
    private String mapping;
    private Integer diff;
    private LocalDateTime createTime;
    private LocalDateTime endTime;
}
