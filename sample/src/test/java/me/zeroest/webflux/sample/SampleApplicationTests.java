package me.zeroest.webflux.sample;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.blockhound.BlockHound;

@SpringBootTest
class SampleApplicationTests {

    @Test
    void contextLoads() {
        BlockHound.install();
    }

}
