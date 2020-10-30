package com.sudipcold.repository;

import com.sudipcold.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

//Tests are running on embedded mongo db
@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(
            new Item(null, "Samsung TV", 400.0),
            new Item(null, "SONY TV", 600.0),
            new Item(null, "iphone", 300.0),
            new Item("ABC", "Bose Headphones", 350.0)
    );

    @Before
    public void setUp(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item) -> {
                    System.out.println("Inserted item is :: " + item);
                })
                .blockLast(); // makes sure data is there before test starts
    }

    @Test
    public void getAllItems(){
        Flux<Item> allItems = itemReactiveRepository.findAll();

        StepVerifier.create(allItems.log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getItemById(){
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Bose Headphones"))
                .verifyComplete();
    }

    @Test
    public void getItemByDescription(){
        StepVerifier.create(itemReactiveRepository.findByDescription("SONY TV"))
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 600.0)
                .verifyComplete();
    }

    @Test
    public void saveItemTest(){

        Item item = new Item("DEF", "Amazon alexa speaker", 100.0);
        Mono<Item> insertedItem = itemReactiveRepository.save(item);

        StepVerifier.create(insertedItem)
                .expectSubscription()
                .expectNextMatches(item1 ->
                        item1.getId() != null && item1.getDescription().equals(item.getDescription()))
        .verifyComplete();
    }

    @Test
    public void updateItem(){

        double newprice = 900.0;
        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("SONY TV")
                .map(item -> {
                    item.setPrice(newprice);
                    return item;
                })
                .flatMap(item -> {
                    return itemReactiveRepository.save(item);
                });

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == newprice);
    }

    @Test
    public void deleteItemById(){
        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")
                .map(Item::getId)
                .flatMap(id -> {
                    return itemReactiveRepository.deleteById(id);
                });

        StepVerifier.create(deletedItem)
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void deleteItem(){
        Mono<Void> deletedItem = itemReactiveRepository.findByDescription("SONY TV")
                .flatMap(item -> {
                    return itemReactiveRepository.delete(item);
                });

        StepVerifier.create(deletedItem)
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

}
