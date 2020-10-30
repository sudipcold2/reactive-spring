package com.sudipcold.fluxmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("sudipcold", "rimicold", "sumitacold");

    @Test
    public void transformUsingMap(){
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.startsWith("s"))
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("SUDIPCOLD", "SUMITACOLD")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap(){
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s)); //db-call or external service call that returns a flux
                })
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapParallel(){
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2) //(A,B) -> Flux<Flux<String>>
                .flatMap((s) -> s.map(this::convertToList).subscribeOn(Schedulers.parallel()))
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        // .subscribesOn(Schedulers.parallel) is the main attraction

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapParallelMaintainOrder(){
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2) //(A,B) -> Flux<Flux<String>>
//                .concatMap((s) -> s.map(this::convertToList).subscribeOn(Schedulers.parallel()))
                .flatMapSequential((s) -> s.map(this::convertToList).subscribeOn(Schedulers.parallel()))
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue_" + s);
    }
}
