package com.scaler.userauthservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDto {
    private String from;
    private String to;
    private String subject;
    private String body;
}
