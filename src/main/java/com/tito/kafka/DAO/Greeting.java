package com.tito.kafka.DAO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class Greeting {
    private String name;
    private String message;
}
