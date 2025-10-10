package com.app.ecomm.order.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private String Street;
    private String City;
    private String State;
    private String Country;
    private String ZipCode;
}
