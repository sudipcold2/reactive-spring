package com.sudipcold.initialize;

import com.sudipcold.document.Item;
import com.sudipcold.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Override
    public void run(String... args) throws Exception {
        //initialDataSetup();
    }

    private void initialDataSetup() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                    .flatMap(itemReactiveRepository::save)
                    .thenMany(itemReactiveRepository.findAll())
                    .subscribe(item -> {
                        System.out.println("item inserted from commandlineRunner : " + item);
                    });
    }

    private List<Item> data() {
        return Arrays.asList(
                new Item(null, "Iphone 12", 1000.0),
                new Item(null, "SONY Bravia", 700.0),
                new Item(null, "Apple Watch", 500.0),
                new Item("ABC", "Beats Headphones", 199.9)
        );
    }
}
