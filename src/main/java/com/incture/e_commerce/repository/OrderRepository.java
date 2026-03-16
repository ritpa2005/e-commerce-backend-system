package com.incture.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.e_commerce.entity.Orders;

/*
 * Repository interface for performing CRUD operations on Orders entity.
 */
@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

	List<Orders> findByUserId(long userId);
}
