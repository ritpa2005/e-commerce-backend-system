package com.incture.e_commerce.dto;

public class CartItemDto {

	private long id;
	private ProductResponseDto product;
	private long quantity;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	
}
