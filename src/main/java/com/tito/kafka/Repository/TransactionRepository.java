package com.tito.kafka.Repository;

import com.tito.kafka.Model.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

}
