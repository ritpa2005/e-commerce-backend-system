package com.incture.e_commerce.dto;

import java.util.List;

public class ProductListDto {

	private List<ProductResponseDto> products;
	private int currentPage;
	private int totalPages;
	private long totalProducts;

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public long getTotalProducts() {
		return totalProducts;
	}

	public void setTotalProducts(long totalProducts) {
		this.totalProducts = totalProducts;
	}

	public List<ProductResponseDto> getProducts() {
		return products;
	}

	public void setProducts(List<ProductResponseDto> products) {
		this.products = products;
	}
}
