package com.sudipcold.fluxmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTest {

    //emits the value from beginning
    @Test
    public void coldPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F").delayElements(Duration.ofSeconds(1));

        stringFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));

        Thread.sleep(2000);

        stringFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));

        Thread.sleep(4000);
    }

    //doesn't emmit the values from beginning
    @Test
    public void hotPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F").delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectibleFlux = stringFlux.publish();
        connectibleFlux.connect();

        connectibleFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));

        Thread.sleep(3000);

        connectibleFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));

        Thread.sleep(4000);
    }
}
