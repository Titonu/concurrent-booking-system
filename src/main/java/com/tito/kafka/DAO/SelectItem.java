package com.tito.kafka.DAO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class SelectItem {
    private Integer userId;
    private List<ItemDAO> items;
}
