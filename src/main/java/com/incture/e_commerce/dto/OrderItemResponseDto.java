package com.incture.e_commerce.dto;

public class OrderItemResponseDto {
	
	private long orderItemId;
	private ProductResponseDto product;
	private long quantity;
	private double price;
	
	
	public long getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(long orderItemId) {
		this.orderItemId = orderItemId;
	}
	public ProductResponseDto getProduct() {
		return product;
	}
	public void setProduct(ProductResponseDto product) {
		this.product = product;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	
}
