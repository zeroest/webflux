
# Scheduler

Subscriber의 쓰레드 설정

아무 설정 하지 않으면, publisher는 subscribe를 호출한 caller의 쓰레드에서 실행  
subscribe에 제공된 lambda 혹은 Scheduler 또한 caller의 쓰레드에서 실행  

```
    CountDownLatch lock = new CountDownLatch(5);
    ExecutorService executor = Executors.newSingleThreadExecutor();

    try {
        executor.submit(() -> {
            Flux.create(sink -> {
                for (int i = 1 ; i <= 5 ; i++) {
                    log.info("next: {}", i);
                    sink.next(i);
                }
            }).subscribe(value -> {
                log.info("value: {}", value);
                lock.countDown();
            });
        });
    } finally {
        executor.shutdown();
    }

    lock.await();
    
    02:44:56.717 [pool-1-thread-1] INFO me.zeroest.webflux.sample.TempTest -- next: 1
    02:44:56.721 [pool-1-thread-1] INFO me.zeroest.webflux.sample.TempTest -- value: 1
    02:44:56.721 [pool-1-thread-1] INFO me.zeroest.webflux.sample.TempTest -- next: 2
    02:44:56.721 [pool-1-thread-1] INFO me.zeroest.webflux.sample.TempTest -- value: 2
    02:44:56.721 [pool-1-thread-1] INFO me.zeroest.webflux.sample.TempTest -- next: 3
    02:44:56.721 [pool-1-thread-1] INFO me.zeroest.webflux.sample.TempTest -- value: 3
    02:44:56.721 [pool-1-thread-1] INFO me.zeroest.webflux.sample.TempTest -- next: 4
    02:44:56.721 [pool-1-thread-1] INFO me.zeroest.webflux.sample.TempTest -- value: 4
    02:44:56.721 [pool-1-thread-1] INFO me.zeroest.webflux.sample.TempTest -- next: 5
    02:44:56.721 [pool-1-thread-1] INFO me.zeroest.webflux.sample.TempTest -- value: 5
```

## Schedulers

publish 혹은 subscribe에 어떤 Scheduler가 적용되었는가에 따라서 task를 실행하는 쓰레드풀이 결정  
즉시 task를 실행하거나 delay를 두고 실행

Cached thread pool

- ImmediateScheduler
  - Schedulers.immediate()
  - subscribe를 호출한 caller 쓰레드에서 즉시 실행
  - 별도 Schedulers 지정하지 않을시 기본 설정
- SingleScheduler
  - Schedulers.single()
  - 캐싱된 1개 크기의 쓰레드풀을 제공
  - 모든 publish, subscribe가 하나의 쓰레드에서 실행
- ParallelScheduler
  - Schedulers.parallel()
  - 캐싱된 n개 크기의 쓰레드풀 제공
  - 기본적으로 CPU 코어 수만큼의 크기를 갖는다
- BoundedElasticScheduler
  - Schedulers.boundedElastic()
  - 캐싱된 고정되지 않은 크기의 쓰레드풀을 제공
  - 재사용할 수 있는 쓰레드가 있다면 사용, 없다면 생성
  - 특정 시간(default 60s) 사용하지 않으면 제거
  - 생성 가능한 쓰레드 수 제한 (default CPU x 10)
  - I/O blocking 작업을 수행할때 적합

newXXX

새로운 쓰레드풀을 만들어서 제공  
새로운 쓰레드풀을 생성했기 때문에 반드시 dispose()로 쓰레드풀 해제할것

- public static Scheduler newSingle(String name, boolean daemon)
- public static Scheduler newParallel(String name, int parallelism, boolean daemon)
- public static Scheduler newBoundedElastic(int threadCap, int queuedTaskCap, String name, int ttlSeconds, boolean daemon)
  
fromExecutorService

이미 존재하는 ExecutorService로 Scheduler 생성

```
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
```

## PublishOn <> SubscribeOn

- publishOn 위치 중요 <> subscribeOn 위치 중요X
- subscribeOn: source가 실행되는 쓰레드를 설정
- publishOn: 중간에서 이후 체이닝된 연산자의 실행 쓰레드를 변경
