package com.tito.kafka.Controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GenericException extends RuntimeException {
    private int code;
    private String message;
}
