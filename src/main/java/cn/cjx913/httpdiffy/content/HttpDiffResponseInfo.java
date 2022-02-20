package cn.cjx913.httpdiffy.content;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class HttpDiffResponseInfo implements Serializable {
    private String name;
    private HttpMethod method;
    private String path;
    private LinkedMultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<>();
    private LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    private LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    private Object requestBody;

    private HttpStatus httpStatus;
    private HttpHeaders responseHeaders;
    private Object responseBody;

}
