package cn.cjx913.httpdiffy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.http.HttpHeaders;

import java.io.Serializable;

@Table("response")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Response implements Serializable {
    @Id
    private Long id;
    private Long requestId;
    private HttpHeaders headers;
    private Object body;
}
