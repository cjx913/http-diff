package cn.cjx913.httpdiffy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping
    public Mono<Map<String, Object>> request(@RequestParam(required = true) String username,
                                             @RequestBody(required = false) Object body) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("username", username);
        Random random = new Random();
        map.put("now", LocalDateTime.now().plusMinutes(random.nextInt()));
        map.put("arr", Arrays.asList("a", "b", "c", random.nextInt(), random.nextBoolean(), random.nextDouble()));
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("aa", "ss");
        hashMap.put("nextDouble", random.nextDouble());
        hashMap.put("nextFloat", random.nextFloat());
        hashMap.put("nextBoolean", random.nextBoolean());
        map.put("data", hashMap);
        map.put("body", body);
        log.info("{}", map);
        if (!"cjx913".equals(username)) {
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return Mono.just(map)
//                .doOnSuccess(s -> {
//                    new Thread(()->{
//                        try {
//                            TimeUnit.SECONDS.sleep(8);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        log.info("--------------");
//                    }).start();
//                })
                ;
    }

    @RequestMapping("/arr")
    public Mono<JSONArray> testArray(@RequestParam(required = true) String username,
                               @RequestBody(required = false) Object body) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(body);
        jsonArray.add("_cjx913_");
        jsonArray.add(LocalDateTime.now());
        jsonArray.add(username);
        jsonArray.add(1);
        jsonArray.add(true);


        return Mono.justOrEmpty(jsonArray);
    }
}
