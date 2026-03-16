package com.incture.e_commerce.dto;

import java.util.List;

public class CartResponseDto {

	private long id;
	private long userId;
	private double totalPrice;
	private List<CartItemDto> cartItems;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public List<CartItemDto> getCartItems() {
		return cartItems;
	}
	public void setCartItems(List<CartItemDto> cartItems) {
		this.cartItems = cartItems;
	}
	
}
