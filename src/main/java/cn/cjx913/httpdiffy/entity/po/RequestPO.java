package cn.cjx913.httpdiffy.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;

@Table("request")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RequestPO implements Serializable {
    @Id
    private Long id;
    private Long diffId;
    private String type;
    private String method;
    private String path;
    private String headers;
    private String queryParams;
    private String formData;
    private String body;
}
