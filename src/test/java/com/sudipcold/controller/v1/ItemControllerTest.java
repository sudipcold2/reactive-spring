package com.sudipcold.controller.v1;

import com.sudipcold.constants.ItemConstants;
import com.sudipcold.document.Item;
import com.sudipcold.repository.ItemReactiveRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
public class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    private List<Item> data() {
        return Arrays.asList(
                new Item(null, "Iphone 12", 1000.0),
                new Item(null, "SONY Bravia", 700.0),
                new Item(null, "Apple Watch", 500.0),
                new Item("ABC", "Beats Headphones", 199.9)
        );
    }

    @Before
    public void setUp(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item -> {
                    System.out.println("Inserted item is :: " + item);
                })).blockLast();

    }

    @Test
    public void getAllItems(){
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);

    }

    @Test
    public void getAllItemsApproach2(){
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    items.forEach(item -> {
                        Assert.assertTrue(item.getId() != null);
                    });
                });
    }

    @Test
    public void getAllItemsApproach3(){
        Flux<Item> itemsFlux = webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemsFlux)
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getOneItem(){
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"),"ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 199.9);
    }

    @Test
    public void getOneItemNotFound(){
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"),"DEF")
                .exchange()
                .expectStatus().isNotFound();
    }
    @Test
    public void createItemTest(){
        Item item = new Item(null, "Dell Laptop", 1200.0);

        webTestClient.post().uri(ItemConstants.ITEM_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.price").isEqualTo(1200.0);
    }


    @Test
    public void deleteItem(){
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"),"ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem(){
        double newPrice = 100.0;
        Item item = new Item(null, "Beats Headphones", newPrice);

        webTestClient.put().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 100.0);
    }

    @Test
    public void updateItemNotFound(){
        double newPrice = 100.0;
        Item item = new Item(null, "Beats Headphones", newPrice);

        webTestClient.put().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "DEF")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void runtimeException() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1 + "/runtimeException")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Runtime Exception");
    }
}
