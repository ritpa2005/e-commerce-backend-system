package com.incture.e_commerce.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.incture.e_commerce.enums.OrderStatus;
import com.incture.e_commerce.enums.PaymentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 * Entity representing a customer's order.
 * An order is created when a user successfully places a purchase.
 */
@Entity
public class Orders {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	private double total_amount;
	private LocalDateTime order_date;
	
	@Enumerated(EnumType.STRING)
	private PaymentStatus payment_status;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus order_status;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
	
	public long getId() {
		return id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public double getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(double total_amount) {
		this.total_amount = total_amount;
	}
	public LocalDateTime getOrder_date() {
		return order_date;
	}
	public void setOrder_date(LocalDateTime order_date) {
		this.order_date = order_date;
	}
	public PaymentStatus getPayment_status() {
		return payment_status;
	}
	public void setPayment_status(PaymentStatus payment_status) {
		this.payment_status = payment_status;
	}
	public OrderStatus getOrder_status() {
		return order_status;
	}
	public void setOrder_status(OrderStatus order_status) {
		this.order_status = order_status;
	}
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}	
}
