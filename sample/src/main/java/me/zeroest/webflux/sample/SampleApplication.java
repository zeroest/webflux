package me.zeroest.webflux.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class SampleApplication {

    public static void main(String[] args) {
        BlockHound.install();
        SpringApplication.run(SampleApplication.class, args);
    }

}
