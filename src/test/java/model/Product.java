package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {
    private Integer offerId;
    private Integer productId;
    private Integer amount;
}