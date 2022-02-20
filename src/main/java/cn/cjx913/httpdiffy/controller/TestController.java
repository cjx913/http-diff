package cn.cjx913.httpdiffy.controller;

import cn.cjx913.httpdiffy.dao.UserRepository;
import cn.cjx913.httpdiffy.entity.User;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getUser")
    public Mono<User> getUser(@RequestParam String username) {
       userRepository.saveAll(Arrays.asList(User.builder()
                .username(UUID.randomUUID().toString().substring(0, 26).replace("-", ""))
                .build()))
               .subscribe();
        return userRepository.findOne(Example.of(User.builder().username(username).build()));
    }
    @GetMapping("/getUsers")
    public Flux<User> getUsers() {
        log.debug("查询所有用户!");
        return userRepository.findAll();
    }

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
        jsonArray.add("test");
        jsonArray.add(LocalDateTime.now());
        jsonArray.add(username);
        jsonArray.add(1);
        jsonArray.add(true);
        Random random = new Random();
        jsonArray.add(random.nextInt());
        jsonArray.add(random.nextLong());
        jsonArray.add(random.nextDouble());
        jsonArray.add(random.nextBoolean());
        jsonArray.add(UUID.randomUUID());
        jsonArray.add(Arrays.asList("a", "b", "c",
                random.nextInt(), random.nextBoolean(), random.nextDouble(), UUID.randomUUID()));

        return Mono.justOrEmpty(jsonArray);
    }
}
