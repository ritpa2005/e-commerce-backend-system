package com.incture.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.e_commerce.entity.CartItem;

/*
 * Repository interface for performing CRUD operations on CartItem entity.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	Optional<CartItem> findByCartIdAndProductId(long cartId, long productId);
	
	void deleteByCartId(long cartId);
}
