package com.incture.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.e_commerce.entity.Cart;

/*
 * Repository interface for performing CRUD operations on Cart entity.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
	Optional<Cart> findByUserId(long userId);
}
