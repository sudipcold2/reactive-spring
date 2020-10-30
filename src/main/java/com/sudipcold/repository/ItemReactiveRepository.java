package com.sudipcold.repository;

import com.sudipcold.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {

    //custom
    Mono<Item> findByDescription(String description);
}
