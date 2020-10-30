package com.sudipcold.fluxmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> names = Arrays.asList("sudipcold", "rimicold", "sumitacold", "priyankacold");

    @Test
    public void filterTest(){
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.startsWith("s"))
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("sudipcold")
                .expectNext("sumitacold")
                .verifyComplete();


    }
}
