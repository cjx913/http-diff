package cn.cjx913.httpdiffy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Slf4j
//@SpringBootTest
class HttpDiffyApplicationTests {

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
