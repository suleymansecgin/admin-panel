package com.suleymansecgin.admin_panel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    
    @NotBlank(message = "Ürün adı boş olamaz")
    private String productName;
    
    @NotNull(message = "Maliyet fiyatı boş olamaz")
    @Positive(message = "Maliyet fiyatı pozitif olmalıdır")
    private BigDecimal costPrice;
    
    @NotNull(message = "Satış fiyatı boş olamaz")
    @Positive(message = "Satış fiyatı pozitif olmalıdır")
    private BigDecimal sellPrice;
    
    @NotNull(message = "Stok miktarı boş olamaz")
    @Positive(message = "Stok miktarı pozitif olmalıdır")
    private Integer stockQuantity;
}

