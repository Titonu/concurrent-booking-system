package com.tito.kafka.Repository;

import com.tito.kafka.Model.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Integer> {

}
