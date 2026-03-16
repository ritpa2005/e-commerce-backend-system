package com.incture.e_commerce.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.incture.e_commerce.dto.ProductDto;
import com.incture.e_commerce.dto.ProductListDto;
import com.incture.e_commerce.dto.ProductRequestDto;
import com.incture.e_commerce.dto.ProductResponseDto;
import com.incture.e_commerce.entity.Product;
import com.incture.e_commerce.exception.IdNotFoundException;
import com.incture.e_commerce.repository.ProductRepository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

/**
 * Unit tests to test methods of ProductService
 */
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
	
	@Mock
    private ProductRepository productRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDto productDto;
    private ProductResponseDto productResponseDto;
    private ProductRequestDto productRequestDto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        
        productDto = new ProductDto();
        productDto.setName("Laptop");

        productResponseDto = new ProductResponseDto();
        productResponseDto.setName("Laptop");

        productRequestDto = new ProductRequestDto();
        productRequestDto.setProductId(1L);
    }
    
    
    @Test
    void addProduct_WhenReceiveProduct_ShouldAddProductSuccessfully() {

        when(modelMapper.map(productDto, Product.class)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(productResponseDto);

        ProductResponseDto result = productService.addProduct(productDto);

        assertNotNull(result);
        assertEquals(productResponseDto.getId(), result.getId());
        verify(productRepository).save(product);
        
    }
    
    
    @Test
    void getProducts_WhenInvoked_ShouldReturnProductsSuccessfully() {

        List<Product> productList = List.of(product);

        int page = 0;
        int size = 3;
        Page<Product> productPage = new PageImpl<>(productList, PageRequest.of(page, size), 5);
        
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(productResponseDto);

        ProductListDto result = productService.getProducts(page, size);

        assertNotNull(result);
        assertEquals(1, result.getProducts().size());
        assertEquals(page, result.getCurrentPage());
        assertEquals(productPage.getTotalPages(), result.getTotalPages());
        assertEquals(productPage.getTotalElements(), result.getTotalProducts());
        verify(productRepository).findAll(any(Pageable.class));
        
    }
    
    
    @Test
    void getProduct_WhenReceiveValidId_ShouldReturnProductSuccessfully() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(productResponseDto);

        ProductResponseDto result = productService.getProduct(productRequestDto);

        assertNotNull(result);
        assertEquals(productResponseDto.getId(), result.getId());
        verify(productRepository).findById(1L);
        
    }
    
    @Test
    void getProduct_WhenReceiveInvalidId_ShouldThrowIdNotFoundException() {

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        IdNotFoundException exception = assertThrows(IdNotFoundException.class, () ->
                productService.getProduct(productRequestDto));
        assertEquals("Product with id = 1 not found.", exception.getMessage());
        verify(productRepository).findById(1L);
        
    }
	
    
    @Test
    void updateProduct_WhenReceiveValidId_ShouldUpdateProductSuccessfully() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(modelMapper).map(productDto, product);
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(productResponseDto);

        ProductResponseDto result = productService.updateProduct(productRequestDto, productDto);

        assertNotNull(result);
        assertEquals(productResponseDto.getId(), result.getId());
        verify(productRepository).findById(1L);
        verify(productRepository).save(product);
    }
    
    @Test
    void updateProduct_WhenReceiveInvalidId_ShouldThrowIdNotFoundException() {

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        IdNotFoundException exception = assertThrows(IdNotFoundException.class, () ->
                productService.updateProduct(productRequestDto, productDto));
        
        assertEquals("Product with id = 1 not found.", exception.getMessage());
        verify(productRepository).findById(1L);
        
    }
    
    
    @Test
    void deleteProduct_WhenReceiveValidId_ShouldDeleteProductSuccessfully() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(productRequestDto);

        verify(productRepository).delete(product);
    }
    
    @Test
    void deleteProduct_WhenReceiveInvalidId_ShouldThrowIdNotFoundException() {

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        IdNotFoundException exception = assertThrows(IdNotFoundException.class, () ->
                productService.deleteProduct(productRequestDto));
        assertEquals("Product with id = 1 not found.", exception.getMessage());
        verify(productRepository).findById(1L);
        
    }
    
}





