package com.incture.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.e_commerce.entity.OrderItem;

/*
 * Repository interface for performing CRUD operations on OrderItem entity.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	void deleteByOrderId(long orderId);
}
