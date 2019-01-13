package com.tito.kafka.DAO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionPost {
    private Integer userId;
    private Integer itemId;
    private Integer amount;
}
