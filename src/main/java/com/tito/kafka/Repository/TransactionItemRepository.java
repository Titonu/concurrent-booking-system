package com.tito.kafka.Repository;

import com.tito.kafka.Model.TransactionItem;
import org.springframework.data.repository.CrudRepository;

public interface TransactionItemRepository extends CrudRepository<TransactionItem, Integer> {

}
