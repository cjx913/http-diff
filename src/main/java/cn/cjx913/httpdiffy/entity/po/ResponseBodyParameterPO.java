package cn.cjx913.httpdiffy.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.http.HttpHeaders;

import java.io.Serializable;

@Table("response_body_parameter")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBodyParameterPO implements Serializable {
    @Id
    private Long id;
    private String mapping;
    private String path;
    private String parameter;
    private Integer diff;
}
