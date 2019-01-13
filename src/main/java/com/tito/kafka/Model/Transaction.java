package com.tito.kafka.Model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    private Integer amount;

    @ManyToOne
    User user;
    @OneToMany(mappedBy = "transaction")
    List<TransactionItem> transactionItems;

}
