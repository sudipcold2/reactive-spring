package com.sudipcold.fluxmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("sudipcold", "rimicold", "sumitacold", "priyankacold");

    @Test
    public void fluxUsingIterable(){


        Flux<String> namesFlux = Flux.fromIterable(names).log();

        StepVerifier.create(namesFlux)
                .expectNext("sudipcold", "rimicold", "sumitacold", "priyankacold")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray(){
        String[] names = new String[] {"sudipcold", "rimicold", "sumitacold", "priyankacold"};
        Flux<String> namesFlux = Flux.fromArray(names);

        StepVerifier.create(namesFlux)
                .expectNext("sudipcold", "rimicold", "sumitacold", "priyankacold")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream(){

        Flux<String> namesFlux = Flux.fromStream(names.stream()).log();

        StepVerifier.create(namesFlux)
                .expectNext("sudipcold", "rimicold", "sumitacold", "priyankacold")
                .verifyComplete();

    }

    @Test
    public void monoUsingJustOrEmpty(){
        Mono<Object> emptyMono = Mono.justOrEmpty(null);//Mono.Empty();

        StepVerifier.create(emptyMono.log())
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier(){
        Supplier<String> stringSupplier = () -> "sudipcold";

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

        StepVerifier.create(stringMono.log())
                .expectNext("sudipcold")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange(){
        Flux<Integer> range = Flux.range(1, 5);
        StepVerifier.create(range.log())
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }

}
