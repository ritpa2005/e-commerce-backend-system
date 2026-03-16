package com.incture.e_commerce.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.incture.e_commerce.dto.ProductDto;
import com.incture.e_commerce.dto.ProductListDto;
import com.incture.e_commerce.dto.ProductRequestDto;
import com.incture.e_commerce.dto.ProductResponseDto;
import com.incture.e_commerce.service.ProductService;

/**
 * REST Controller for Product related APIs
 * Provides endpoints for product creation, retrieval, update and deletion.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {
	
	// Logger instance for logging API activity
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
	@Autowired
	private ProductService productService;
	
	/**
     * API to add a new product
     *
     * @param productDto - product details from request body
     * @return ProductResponseDto containing saved product details
     */
	@PostMapping("/")
	public ResponseEntity<ProductResponseDto> addProduct(@RequestBody ProductDto productDto){
		
		// Adding the product to database
		ProductResponseDto productResponseDto = productService.addProduct(productDto);
		
		logger.info("Product created successfully with ID: {}", productResponseDto.getId());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDto);
	}
	
	
	/**
     * API to fetch all products
     *
     * @return ProductListDto containing list of all products
     */
	@GetMapping("/")
	public ResponseEntity<ProductListDto> getProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
		
		// Fetching products
		ProductListDto productListDto = productService.getProducts(page, size);
		
		logger.info("Total products fetched: {}", productListDto.getProducts().size());
		logger.debug("Products fetched are: {}", productListDto.getProducts());
		
		return ResponseEntity.status(HttpStatus.OK).body(productListDto);
	}
	
	
	/**
     * API to fetch product details by product ID
     *
     * @param productId - product ID
     * @return ProductResponseDto containing product details
     */
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponseDto> getProducts(@PathVariable("id") Long productId){
		
		// Fetching product from database
		ProductRequestDto productRequestDto = new ProductRequestDto();
		productRequestDto.setProductId(productId);
		
		ProductResponseDto productResponseDto = productService.getProduct(productRequestDto);
		
		logger.info("Product fetched successfully for ID: {}", productId);
		
		return ResponseEntity.status(HttpStatus.OK).body(productResponseDto);
	}
	
	
	/**
     * API to update product details
     *
     * @param productId - product ID
     * @param productDto - updated product details
     * @return updated ProductResponseDto
     */
	@PutMapping("/{id}")
	public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable("id") Long productId, @RequestBody ProductDto productDto){
		
		// Updating product in database
		ProductRequestDto productRequestDto = new ProductRequestDto();
		productRequestDto.setProductId(productId);
		
		ProductResponseDto productResponseDto = productService.updateProduct(productRequestDto, productDto);
		
		logger.info("Product updated successfully with ID: {}", productId);
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(productResponseDto);
	}
	
	
	/**
     * API to delete a product by ID
     *
     * @param productId - product ID
     * @return success message
     */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable("id") Long productId){
		
		// Deleting product from the database
		ProductRequestDto productRequestDto = new ProductRequestDto();
		productRequestDto.setProductId(productId);
		
		productService.deleteProduct(productRequestDto);
		
		logger.warn("Product deleted successfully with ID: {}", productId);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Product with id = " + productId + " deleted successfully.");
	}
	
}
