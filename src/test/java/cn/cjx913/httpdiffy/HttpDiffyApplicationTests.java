package cn.cjx913.httpdiffy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPatch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Slf4j
//@SpringBootTest
class HttpDiffyApplicationTests {

    @Test
    public void test() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json1 = "{\"a\":0,\"b\":[1,2]}";
        String json2 = "{\"b\": [1,2,0]} ";
        JsonNode patch = JsonDiff.asJson(mapper.readTree(json1), mapper.readTree(json2));
        System.out.println(patch.toString());
        System.out.println(patch.toPrettyString());

    }

    @Test
    void contextLoads() {
        Object block = Mono.justOrEmpty("aaaaaaa")
                .doOnSuccess(o -> {
                    System.out.println("-------" + o);
                }).block();
        System.out.println("-------" + block);
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
