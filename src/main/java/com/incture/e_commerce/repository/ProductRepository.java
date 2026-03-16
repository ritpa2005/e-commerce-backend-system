package com.incture.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.e_commerce.entity.Product;

/*
 * Repository interface for performing CRUD operations on Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
