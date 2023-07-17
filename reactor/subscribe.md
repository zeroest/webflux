
# Subscribe

```java
public final Disposable subscribe()
```
Consumer 를 넘기지 않는 subscribe
- 별도로 consume을 하지 않고 최대한으로 요청

```java
public final Disposable subscribe(
    @Nullable Consumer<? super T> consumer,
    @Nullable Consumer<? super Throwable> errorConsumer,
    @Nullable Runnable completeConsumer,
    @Nullable Context initialContext)
    
Flux.fromIterable(List.of(1, 2, 3, 4, 5))
    .subscribe(
        integer -> log.info("int: {}", integer),
        e -> log.error("e", e),
        () -> log.info("complete"),
        Context.empty()
    );
```
함수형 인터페이스 기반의 subscribe  
- Disposable을 반환하고 disposable을 통해서 언제든지 연결 종료 가능
- initialContext: upstream에 전달할 context

```java
public final void subscribe(Subscriber<? super T> actual)

Flux.range(0, 100)
    .subscribe(new Subscriber<>() {
        Subscription s;
        Long l = 1L;
        @Override
        public void onSubscribe(Subscription s) {
            this.s = s;
            s.request(3);
//            s.request(Long.MAX_VALUE); // unbounded request
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
```
Subscriber 기반의 subscribe
- Subscriber는 subscription을 받기 때문에 request와 cancel을 통해서 backpressure 조절과 연결 종료 가능

```java
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
```
BaseSubscriber
- onNext,     onComplete,     onError,     onSubscribe     를 직접 호출하는 대신
- hookOnNext, hookOnComplete, hookOnError, hookOnSubscribe 를 구현
- subscriber 외부에서 request와 cancel을 호출 가능
- 기본적으로 unbounded request

BackPressure 비활성화 조건
- 아무것도 넘기지 않는 lambda 기반의 subscribe()
- BaseSubscriber의 hookOnSubscribe를 그대로 사용
- block(), blockFirst(), blockLast() 등의 blocking 연산자
- toIterable(), toStream() 등의 toCollect 연산자

```java
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

    value: [1, 2, 3]
    value: [4, 5, 6]
```
Buffer
- buffer(N) 호출시 N개 만큼 모아서 List로 전달
- buffer(3) 호출 후 request(2)를 하는 경우, 3개가 담긴 List 2개가 Subscriber에게 전달

```java
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
```
Take(n, limitRequest)
- subscriber 외부에서 연산자를 통해서 최대 개수를 제한
- limitRequest가 true인 경우, 정확히 n개 만큼 요청 후 complete 이벤트를 전달
- BaseSubscriber의 기본 전략이 unbounded request이지만 take(5, true)로 인해서 5개 전달 후 complete 이벤트

