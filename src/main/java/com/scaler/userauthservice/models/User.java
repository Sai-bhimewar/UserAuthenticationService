package com.scaler.userauthservice.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class User extends BaseModel {
    private String name;
    private String email;
    private String password;

    public User(){
        this.setCreatedAt(new Date());
        this.setLastUpdatedAt(new Date());
        this.setState(State.Active);
    }
}
