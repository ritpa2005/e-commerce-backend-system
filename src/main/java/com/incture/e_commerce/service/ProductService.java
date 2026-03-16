package com.incture.e_commerce.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.incture.e_commerce.dto.ProductDto;
import com.incture.e_commerce.dto.ProductListDto;
import com.incture.e_commerce.dto.ProductRequestDto;
import com.incture.e_commerce.dto.ProductResponseDto;
import com.incture.e_commerce.entity.Product;
import com.incture.e_commerce.exception.IdNotFoundException;
import com.incture.e_commerce.repository.ProductRepository;

/*
 * Service layer responsible for handling product-related business logic.
 */
@Service
public class ProductService {
		
	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	

	/**
     * Adds a new product to the database
     * 
     * @param productDto contains product details
     * @return ProductResponseDto containing saved product details
     */
	public ProductResponseDto addProduct(ProductDto productDto) {
				
		logger.info("Adding new product");
		Product product = modelMapper.map(productDto, Product.class);
		
		Product savedProduct = productRepository.save(product);
		
		logger.debug("Product saved with id {}", savedProduct.getId());
		
		ProductResponseDto productResponseDto = modelMapper.map(savedProduct, ProductResponseDto.class);
		return productResponseDto;
	}
	
	
	/**
     * Fetches products from database through pagination
     * 
     * @param page contains the page to fetch
     * @param size contains the number of products to fetch
     * @return ProductListDto containing list of products
     */
	public ProductListDto getProducts(int page, int size) {
		
		logger.info("Fetching {} products in page {}", size, page);
		
		Pageable pageable = PageRequest.of(page, size);
		Page<Product> productPage = productRepository.findAll(pageable);
				
		List<ProductResponseDto> productResponseDtos = productPage.getContent()
				.stream()
				.map(p -> modelMapper.map(p, ProductResponseDto.class))
				.toList();

		ProductListDto productListDto = new ProductListDto();
		productListDto.setProducts(productResponseDtos);
		productListDto.setCurrentPage(productPage.getNumber());
	    productListDto.setTotalPages(productPage.getTotalPages());
	    productListDto.setTotalProducts(productPage.getTotalElements());
		
	    logger.debug("Fetched {} products from page {}", productResponseDtos.size(), page);
		
		return productListDto;
	}
	
	
	/**
     * Fetch a single product by id
     * 
     * @param productRequestDto contains product id
     * @return ProductResponseDto
     */
	public ProductResponseDto getProduct(ProductRequestDto productRequestDto) {
		
		long id = productRequestDto.getProductId();
		
		logger.info("Fetching product with id {}", id);
		
		Product product = productRepository.findById(id)
				.orElseThrow(() -> {
                    logger.error("Product not found with id {}", id);
                    return new IdNotFoundException("Product with id = " + id + " not found.");
                });
		
		ProductResponseDto productResponseDto = modelMapper.map(product, ProductResponseDto.class);
		return productResponseDto;
	}
	
	
	/**
     * Updates an existing product
     * 
     * @param productRequestDto contains product id
     * @param productDto contains updated product details
     * @return updated product response
     */
	public ProductResponseDto updateProduct(ProductRequestDto productRequestDto, ProductDto productDto) {
		
		long id = productRequestDto.getProductId();
		
		logger.info("Updating product with id {}", id);
		
		Product product = productRepository.findById(id)
				.orElseThrow(() -> {
                    logger.error("Update failed. Product not found with id {}", id);
                    return new IdNotFoundException("Product with id = " + id + " not found.");
                });
		
		modelMapper.map(productDto, product);
		
		productRepository.save(product);
		
		logger.debug("Product updated successfully with id {}", id);
		
		ProductResponseDto productResponseDto = modelMapper.map(product, ProductResponseDto.class);
		return productResponseDto;
	}
	
	
	/**
     * Deletes product by id
     * 
     * @param productRequestDto contains product id
     */
	public void deleteProduct(ProductRequestDto productRequestDto) {
		
		long id = productRequestDto.getProductId();
		
		logger.warn("Deleting product with id {}", id);
		
		Product product = productRepository.findById(id)
				.orElseThrow(() -> {
                    logger.error("Delete failed. Product not found with id {}", id);
                    return new IdNotFoundException("Product with id = " + id + " not found.");
                });
		
		productRepository.delete(product);
		
		logger.debug("Product deleted successfully with id {}", id);
		
	}
}








