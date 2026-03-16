package com.incture.e_commerce.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.incture.e_commerce.enums.OrderStatus;
import com.incture.e_commerce.enums.PaymentStatus;

public class OrderResponseDto {

	private long orderId;
	private long userId;
	private double total_amount;
	private LocalDateTime order_date;
	private PaymentStatus payment_status;
	private OrderStatus order_status;
    private List<OrderItemResponseDto> orderItems;
    
	
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
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
	public List<OrderItemResponseDto> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItemResponseDto> orderItems) {
		this.orderItems = orderItems;
	}
    
    
}
