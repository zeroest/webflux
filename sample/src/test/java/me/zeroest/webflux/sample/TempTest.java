package me.zeroest.webflux.sample;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
public class TempTest {

    @Test
    void test() {
        Flux.fromIterable(List.of(1, 2, 3, 4, 5))
            .subscribe(
                integer -> log.info("int: {}", integer),
                e -> log.error("e", e),
                () -> log.info("complete"),
                Context.empty()
            );
    }

    @Test
    void sub() {
        Flux.range(0, 100)
            .subscribe(new Subscriber<>() {
                Subscription s;
                Long l = 1L;

                @Override
                public void onSubscribe(Subscription s) {
                    this.s = s;
                    s.request(3);
//                    s.request(Long.MAX_VALUE); // unbounded request
                }

                @Override
                public void onNext(Integer integer) {
                    log.info("value: {}", integer);
                    l++;
                    if (l % 3 == 0) {
                        s.request(3);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log.error("error", t);
                }

                @Override
                public void onComplete() {
                    log.info("complete");
                }
            });
    }

    @Test
    void baseSubscriber() {
        BaseSubscriber<Integer> subscriber = new BaseSubscriber<>() {

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(1);
            }

            @Override
            protected void hookOnNext(Integer value) {
                log.info("value: {}", value);
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                log.error("error", throwable);
            }

            @Override
            protected void hookOnComplete() {
                log.info("complete");
            }
        };
        Flux.range(1, 101)
            .subscribe(subscriber);

        subscriber.request(5);
        subscriber.cancel();
    }

    @Test
    void baseSubscriber2() {
        BaseSubscriber<List<Integer>> subscriber = new BaseSubscriber<>() {

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(2);
            }

            @Override
            protected void hookOnNext(List<Integer> value) {
                log.info("value: {}", value);
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                log.error("error", throwable);
            }

            @Override
            protected void hookOnComplete() {
                log.info("complete");
            }
        };
        Flux.range(1, 101)
            .buffer(3)
            .subscribe(subscriber);

//        subscriber.request(5);
//        subscriber.cancel();
    }


    @Test
    void take() {
        BaseSubscriber<Integer> subscriber = new BaseSubscriber<>() {
            @Override
            protected void hookOnNext(Integer value) {
                log.info("value: {}", value);
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                log.error("error", throwable);
            }

            @Override
            protected void hookOnComplete() {
                log.info("complete");
            }
        };

        Flux.range(0, 100)
            .take(5, true)
            .subscribe(subscriber);
    }

    @Test
    void just() {
        Mono.just(1)
            .subscribe(value -> log.info("value: {}", value));

        Flux.just(1, 2, 3, 4, 5)
            .subscribe(value -> log.info("value: {}", value));
    }

    @Test
    void error() {
        Mono.error(new RuntimeException("mono error"))
            .subscribe(value -> log.info("value: {}", value),
                error -> log.error("error", error));

        Flux.error(new RuntimeException("flux error"))
            .subscribe(value -> log.info("value: {}", value),
                error -> log.error("error", error));
    }

    @Test
    void empty() {
        Mono.empty()
            .subscribe(value -> log.info("value: {}", value),
                null,
                () -> log.info("complete"));

        Flux.empty()
            .subscribe(value -> log.info("value: {}", value),
                null,
                () -> log.info("complete"));
    }

    @Test
    void from() {
        Mono.fromCallable(() -> {
            log.info("Callable");
            return 1;
        })
            .subscribe(value -> log.info("value: {}", value),
                error -> log.error("error", error),
                () -> log.info("complete"));

        Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
            log.info("Future");
            return 1;
        }))
            .subscribe(value -> log.info("value: {}", value),
                error -> log.error("error", error),
                () -> log.info("complete"));

        Mono.fromSupplier(() -> {
            log.info("Supplier");
            return 1;
        })
            .subscribe(value -> log.info("value: {}", value),
                error -> log.error("error", error),
                () -> log.info("complete"));

        Mono.fromRunnable(() -> log.info("Runnable"))
            .subscribe(value -> log.info("value: {}", value),
                error -> log.error("error", error),
                () -> log.info("complete"));
    }

    @Test
    void fluxFrom() {
        Flux.fromIterable(List.of(1, 2, 3))
            .subscribe(value -> log.info("value: {}", value),
                error -> log.error("error", error),
                () -> log.info("complete"));

        Flux.fromStream(IntStream.range(5, 8).boxed())
            .subscribe(value -> log.info("value: {}", value),
                error -> log.error("error", error),
                () -> log.info("complete"));

        Flux.fromArray(new Integer[]{10, 11})
            .subscribe(value -> log.info("value: {}", value),
                error -> log.error("error", error),
                () -> log.info("complete"));

        Flux.range(15, 5)
            .subscribe(value -> log.info("value: {}", value),
                error -> log.error("error", error),
                () -> log.info("complete"));
    }

    @Test
    void generate() {
        Flux.generate(
            () -> 0,
            (state, sink) -> {
                sink.next(state);
                if (state == 9) {
                    sink.complete();
                }
                return state + 1;
            }
        )
            .subscribe(value -> log.info("value: {}", value),
                error -> log.error("error", error),
                () -> log.info("complete"));
    }

    @Test
    void create() {
        Flux.create(fluxSink -> {
            CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
                for (int i = 0; i < 5; i++) {
                    fluxSink.next(i);
                }
            });
            CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
                for (int i = 5; i < 10; i++) {
                    fluxSink.next(i);
                }
            });

            CompletableFuture.allOf(task1, task2)
                .thenRun(fluxSink::complete);
        })
            .subscribe(value -> log.info("value: {}", value),
                error -> log.error("error", error),
                () -> log.info("complete"));
    }

    @Test
    void handle() {
        Flux.fromStream(IntStream.range(0, 10).boxed())
            .handle((value, sink) -> {
                if (value % 2 == 0) {
                    sink.next(value);
                }
            })
            .subscribe(value -> log.info("value: {}", value),
                error -> log.error("error", error),
                () -> log.info("complete"));
    }

    @Test
    void defaultThread() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(5);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            executor.submit(() -> {
                Flux.create(sink -> {
                    for (int i = 1 ; i <= 5 ; i++) {
                        log.info("next: {}", i);
                        sink.next(i);
                    }
                })
//                    .subscribeOn(Schedulers.newBoundedElastic())
                    .subscribe(value -> {
                    log.info("value: {}", value);
                    lock.countDown();
                });
            });
        } finally {
            executor.shutdown();
        }

        lock.await();
    }

    @Test
    void fromExec() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(100);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (int i = 0 ; i < 100 ; i++) {
            final int idx = i;
            Flux.create(sink -> {
                log.info("next: {}", idx);
                sink.next(idx);
            })
                .subscribeOn(Schedulers.fromExecutorService(executor))
                .subscribe(value -> {
                    log.info("value: {}", value);
                    lock.countDown();
                });
        }

        lock.await();
        executor.shutdown();
    }
}
