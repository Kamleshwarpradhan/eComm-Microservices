package com.app.ecomm.user.Model;


import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Address {
    @Id
    private String Id;

    private String Street;
    private String City;
    private String State;
    private String Country;
    private String ZipCode;
}
