package me.zeroest.webflux.sample.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class BlockHoundTestRouter {

    @Bean
    public RouterFunction<ServerResponse> blockHoundTestRouter() {
        return RouterFunctions.route()
            .GET("/block-hound", request -> {
                Mono<String> stringMono = Mono.fromCallable(() -> {
                    Thread.sleep(100);
                    return "block";
                }); //.subscribeOn(Schedulers.boundedElastic());
                // subscribeOn 으로 별도 쓰레드 처리시 blockhound가 blocking call 로 판단하지 않음
                return ServerResponse.ok().body(stringMono, String.class);
            })
            .build();
    }

}

/*
reactor.blockhound.BlockingOperationError: Blocking call! java.lang.Thread.sleep
	at java.base/java.lang.Thread.sleep(Thread.java) ~[na:na]
	Suppressed: reactor.core.publisher.FluxOnAssembly$OnAssemblyException:
Error has been observed at the following site(s):
	*__checkpoint ⇢ Handler me.zeroest.webflux.sample.router.TestRouter$$Lambda$590/0x00000008004ec7e0@74b32578 [DispatcherHandler]
	*__checkpoint ⇢ HTTP GET "/block-hound" [ExceptionHandlingWebHandler]
Original Stack Trace:
		at java.base/java.lang.Thread.sleep(Thread.java) ~[na:na]
		at me.zeroest.webflux.sample.router.TestRouter.lambda$blockHoundTestRouter$0(TestRouter.java:19) ~[classes/:na]
		at reactor.core.publisher.MonoCallable$MonoCallableSubscription.request(MonoCallable.java:137) ~[reactor-core-3.5.7.jar:3.5.7]
*/