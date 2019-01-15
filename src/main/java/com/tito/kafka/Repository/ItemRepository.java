package com.tito.kafka.Repository;

import com.tito.kafka.Model.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Integer> {
    List<Item> findByIdIn(List<Integer> itemIds);
}
