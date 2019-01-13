package com.tito.kafka.Controller;

import com.tito.kafka.Model.Item;
import com.tito.kafka.Model.User;
import com.tito.kafka.Repository.ItemRepository;
import com.tito.kafka.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class InitiateData implements ApplicationListener<ContextRefreshedEvent> {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public InitiateData(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        createNewUser("Susan");
        createNewUser("Manda");
        createNewItem("Apel", 5);
        createNewItem("Pepaya", 1);
        createNewItem("Mangga", 4);
    }

    private void createNewUser(String name){
        User user = new User();
        user.setName(name);
        userRepository.save(user);
    }

    private void createNewItem(String name, int amount){
        Item item = new Item();
        item.setName(name);
        item.setAmount(amount);
        itemRepository.save(item);
    }
}