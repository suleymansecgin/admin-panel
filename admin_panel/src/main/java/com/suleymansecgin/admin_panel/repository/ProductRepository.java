package com.suleymansecgin.admin_panel.repository;

import com.suleymansecgin.admin_panel.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}

