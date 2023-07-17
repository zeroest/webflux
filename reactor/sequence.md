
#Sequence

```java
    Mono.just(1)
        .subscribe(value -> log.info("value: {}", value));

    Flux.just(1,2,3,4,5)
        .subscribe(value -> log.info("value: {}", value));
```
just
- Mono.just 혹은 Flux.just 를 통해서 주어진 객체를 subscriber에게 전달

```java
    Mono.error(new RuntimeException("mono error"))
        .subscribe(value -> log.info("value: {}", value),
            error -> log.error("error", error));

    01:25:46.818 [Test worker] ERROR me.zeroest.webflux.sample.TempTest -- error
    
    Flux.error(new RuntimeException("flux error"))
        .subscribe(value -> log.info("value: {}", value),
            error -> log.error("error", error));

    01:25:46.847 [Test worker] ERROR me.zeroest.webflux.sample.TempTest -- error
```
error
- Mono.error 혹은 Flux.error 를 통해서 subscriber에게 onError 이벤트만 전달

```java
    Mono.empty()
        .subscribe(value -> log.info("value: {}", value),
            null,
            () -> log.info("complete"));

    01:29:41.373 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- complete
    
    Flux.empty()
        .subscribe(value -> log.info("value: {}", value),
            null,
            () -> log.info("complete"));
    
    01:29:41.398 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- complete
```
empty
- Mono.empty 혹은 Flux.empty 를 통해서 subscriber에게 onComplete 이벤트만 전달

```java
    Mono.fromCallable(() -> {
        log.info("Callable");
        return 1;
    })
        .subscribe(value -> log.info("value: {}", value),
            error -> log.error("error", error),
            () -> log.info("complete"));

    01:37:08.827 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- Callable
    01:37:08.829 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 1
    01:37:08.830 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- complete

    Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
        log.info("Future");
        return 1;
    }))
        .subscribe(value -> log.info("value: {}", value),
            error -> log.error("error", error),
            () -> log.info("complete"));

    01:37:08.831 [ForkJoinPool.commonPool-worker-1] INFO me.zeroest.webflux.sample.TempTest -- Future
    01:37:08.832 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 1
    01:37:08.832 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- complete
    
    Mono.fromSupplier(() -> {
        log.info("Supplier");
        return 1;
    })
        .subscribe(value -> log.info("value: {}", value),
            error -> log.error("error", error),
            () -> log.info("complete"));

    01:37:08.832 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- Supplier
    01:37:08.832 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 1
    01:37:08.832 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- complete
    
    Mono.fromRunnable(() -> log.info("Runnable"))
        .subscribe(value -> log.info("value: {}", value),
            error -> log.error("error", error),
            () -> log.info("complete"));

    01:37:08.833 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- Runnable
    01:37:08.833 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- complete
```
Mono from
- fromCallable: Callable 함수형 인터페이스를 실행하고 반환값을 onNext로 전달
- fromFuture: Future를 받아서 done 상태가 되면 반환값을 onNext로 전달
- fromSupplier: Supplier 함수형 인터페이스를 실행하고 반환값을 onNext로 전달
- fromRunnable: Runnable 함수형 인터페이스를 실행하고 끝난 후 onComplete 전달

```java
    Flux.fromIterable(List.of(1,2,3))
        .subscribe(value -> log.info("value: {}", value),
            error -> log.error("error", error),
            () -> log.info("complete"));

    01:53:29.219 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 1
    01:53:29.221 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 2
    01:53:29.221 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 3
    01:53:29.222 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- complete

    Flux.fromStream(IntStream.range(5, 8).boxed())
        .subscribe(value -> log.info("value: {}", value),
            error -> log.error("error", error),
            () -> log.info("complete"));

    01:53:29.223 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 5
    01:53:29.223 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 6
    01:53:29.223 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 7
    01:53:29.223 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- complete
    
    Flux.fromArray(new Integer[]{10, 11})
        .subscribe(value -> log.info("value: {}", value),
            error -> log.error("error", error),
            () -> log.info("complete"));

    01:53:29.223 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 10
    01:53:29.223 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 11
    01:53:29.223 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- complete
    
    Flux.range(15, 18)
        .subscribe(value -> log.info("value: {}", value),
            error -> log.error("error", error),
            () -> log.info("complete"));

    01:54:58.738 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 15
    01:54:58.738 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 16
    01:54:58.738 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 17
    01:54:58.738 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 18
    01:54:58.738 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 19
    01:54:58.738 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- complete
```
Flux from
- fromIterable: Iterable을 받아서 각각의 item을 onNext로 전달
- fromStream: Stream을 받아서 각각의 item을 onNext로 전달
- fromArray: Array를 받아서 각각의 item을 onNext로 전달
- range(start, n): start부터 시작해서 한개씩 커진 값을 n개만큼 onNext로 전달

```
	public static <T, S> Flux<T> generate(Callable<S> stateSupplier, BiFunction<S, SynchronousSink<T>, S> generator) 
    public static <T, S> Flux<T> generate(Callable<S> stateSupplier, BiFunction<S, SynchronousSink<T>, S> generator, Consumer<? super S> stateConsumer)
    
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
		
	02:07:26.992 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 0
    02:07:26.994 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 1
    02:07:26.994 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 2
    02:07:26.995 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 3
    02:07:26.995 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 4
    02:07:26.995 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 5
    02:07:26.995 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 6
    02:07:26.995 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 7
    02:07:26.995 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 8
    02:07:26.995 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 9
    02:07:26.995 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- complete
```
generate
- 동기적으로 Flux를 생성
- stateSupplier: 초기값을 제공하는 Callable
- generator
  - 첫 번째 인자로 state를 제공, 변경된 state를 반환, 이 state로 종료 조건을 지정
  - 두 번째 인자로 SynchronousSink를 제공, 명시적으로 next, error, complete 호출 가능
  - 한 번의 generator에서 최대 한 번만 next 호출 가능

```java
	public static <T> Flux<T> create(Consumer<? super FluxSink<T>> emitter, OverflowStrategy backpressure) 
	
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
	
	02:18:04.148 [ForkJoinPool.commonPool-worker-1] INFO me.zeroest.webflux.sample.TempTest -- complete
    02:18:04.144 [ForkJoinPool.commonPool-worker-1] INFO me.zeroest.webflux.sample.TempTest -- value: 0
    02:18:04.147 [ForkJoinPool.commonPool-worker-1] INFO me.zeroest.webflux.sample.TempTest -- value: 5
    02:18:04.147 [ForkJoinPool.commonPool-worker-1] INFO me.zeroest.webflux.sample.TempTest -- value: 6
    02:18:04.147 [ForkJoinPool.commonPool-worker-1] INFO me.zeroest.webflux.sample.TempTest -- value: 7
    02:18:04.147 [ForkJoinPool.commonPool-worker-1] INFO me.zeroest.webflux.sample.TempTest -- value: 8
    02:18:04.147 [ForkJoinPool.commonPool-worker-1] INFO me.zeroest.webflux.sample.TempTest -- value: 9
    02:18:04.147 [ForkJoinPool.commonPool-worker-1] INFO me.zeroest.webflux.sample.TempTest -- value: 1
    02:18:04.147 [ForkJoinPool.commonPool-worker-1] INFO me.zeroest.webflux.sample.TempTest -- value: 2
    02:18:04.147 [ForkJoinPool.commonPool-worker-1] INFO me.zeroest.webflux.sample.TempTest -- value: 3
    02:18:04.147 [ForkJoinPool.commonPool-worker-1] INFO me.zeroest.webflux.sample.TempTest -- value: 4
```
create
- 비동기적으로 Flux를 생성
- FluxSink를 노출
  - 명시적으로 next, error, complete 호출 가능
  - SynchronousSink와 다르게 여러번 next 가능
  - 여러 thread에서 동시에 호출 가능
  
```java
	public final <R> Flux<R> handle(BiConsumer<? super T, SynchronousSink<R>> handler)
	
    Flux.fromStream(IntStream.range(0, 10).boxed())
        .handle((value, sink) -> {
            if (value % 2 == 0) {
                sink.next(value);
            }
        })
        .subscribe(value -> log.info("value: {}", value),
            error -> log.error("error", error),
            () -> log.info("complete"));
    
    02:29:01.571 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 0
    02:29:01.573 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 2
    02:29:01.573 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 4
    02:29:01.573 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 6
    02:29:01.573 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- value: 8
    02:29:01.573 [Test worker] INFO me.zeroest.webflux.sample.TempTest -- complete
```
handle
- 독립적으로 sequence를 생성할 수는 없고 존재하는 source에 연결
- handler
  - 첫 번째 인자로 source의 item이 제공
  - 두 번째 인자로 SynchronousSink를 제공
  - sink의 next를 이용해서 현재 주어진 item을 전달 할지 말지를 결정
- 일종의 interceptor로 source의 item을 필터 하거나 변경할 수 있다

