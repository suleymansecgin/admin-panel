package com.suleymansecgin.admin_panel.service;

import com.suleymansecgin.admin_panel.dto.ProductRequest;
import com.suleymansecgin.admin_panel.dto.ProductResponse;
import com.suleymansecgin.admin_panel.exception.BaseException;
import com.suleymansecgin.admin_panel.exception.ErrorMessage;
import com.suleymansecgin.admin_panel.exception.MessageType;
import com.suleymansecgin.admin_panel.model.Product;
import com.suleymansecgin.admin_panel.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        try {
            Product product = new Product();
            product.setProductName(request.getProductName());
            product.setCostPrice(request.getCostPrice());
            product.setSellPrice(request.getSellPrice());
            product.setStockQuantity(request.getStockQuantity());
            
            product = productRepository.save(product);
            
            return convertToResponse(product);
        } catch (Exception e) {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, 
                    "Ürün kaydedilirken hata oluştu: " + e.getMessage()));
        }
    }
    
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, "ID: " + id)));
        return convertToResponse(product);
    }
    
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, "ID: " + id)));
        
        product.setProductName(request.getProductName());
        product.setCostPrice(request.getCostPrice());
        product.setSellPrice(request.getSellPrice());
        product.setStockQuantity(request.getStockQuantity());
        
        product = productRepository.save(product);
        
        return convertToResponse(product);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, "ID: " + id));
        }
        productRepository.deleteById(id);
    }
    
    private ProductResponse convertToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getProductName(),
                product.getCostPrice(),
                product.getSellPrice(),
                product.getStockQuantity(),
                product.getCreateTime()
        );
    }
}

