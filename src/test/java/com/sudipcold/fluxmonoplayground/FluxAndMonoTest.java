package com.sudipcold.fluxmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    //@Test
    public void fluxTest(){
        Flux<String> stringFlux = Flux.just("spring", "spring boot", "reactive spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred !!")));
                //.concatWith(Flux.just("Sudipcold", "sudipcold2"));

        stringFlux
                .subscribe(
                        System.out :: println,
                        (e) -> System.err.println(e), //exception
                        () -> System.out.println("completed") //on-complete
                );

    }

    @Test
    public void fluxTestElementsWithoutError(){
        Flux<String> stringFlux = Flux.just("spring", "spring boot", "reactive spring").log();

//        StepVerifier.create(stringFlux)
//                .expectNext("spring")
//                .expectNext("spring boot")
//                .expectNext("reactive spring")
//                //.expectError(RuntimeException.class) //expecting an error, matches the exception class
//                //.expectErrorMessage("Exception occured") //matches occured exception's error message
//                //.verify() //start the flow
//                .verifyComplete(); //equivalent to subscribe this is needed to start the steam flow from flux

        //above can be also written as the below

        StepVerifier.create(stringFlux)
                .expectNext("spring", "spring boot", "reactive spring")
                //.expectError(RuntimeException.class) //expecting an error, matches the exception class
                //.expectErrorMessage("Exception occured") //matches occured exception's error message
                //.verify() //start the flow
                .verifyComplete(); //equivalent to subscribe this is needed to start the steam flow from flux

    }

    @Test
    public void fluxTestElementsCount(){
        Flux<String> stringFlux = Flux.just("spring", "spring boot", "reactive spring").log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3) //matches count elements from stream
                .verifyComplete();

    }

    @Test
    public void monoTest(){
        Mono<String> stringMono = Mono.just("Spring").log();

        StepVerifier.create(stringMono)
                .expectNext("Spring")
                .verifyComplete();

        StepVerifier.create(Mono.error(new RuntimeException("Exception occurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
