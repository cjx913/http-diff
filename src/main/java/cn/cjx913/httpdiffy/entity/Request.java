package cn.cjx913.httpdiffy.entity;

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
public class Request implements Serializable {
    @Id
    private Long id;
    private Long diffId;
    private String type;
    private HttpMethod method;
    private String path;
    private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    private MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    private MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    private Object body;
}
