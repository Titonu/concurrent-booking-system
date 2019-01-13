package com.tito.kafka.Model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class TransactionItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    private Integer amount;

    @ManyToOne
    Item item;
    @ManyToOne
    Transaction transaction;

}
