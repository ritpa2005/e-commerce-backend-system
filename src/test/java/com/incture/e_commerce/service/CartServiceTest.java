package com.incture.e_commerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.incture.e_commerce.dto.CartItemDto;
import com.incture.e_commerce.dto.CartRequestDto;
import com.incture.e_commerce.dto.CartResponseDto;
import com.incture.e_commerce.dto.CartUpdateDto;
import com.incture.e_commerce.dto.ProductResponseDto;
import com.incture.e_commerce.entity.*;
import com.incture.e_commerce.exception.IdNotFoundException;
import com.incture.e_commerce.repository.*;


/**
 * Unit tests to test methods of CartService
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

	@Mock
	private ProductRepository productRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private CartRepository cartRepository;
	@Mock
	private CartItemRepository cartItemRepository;
	@Mock
	private ModelMapper modelMapper;
	@InjectMocks
	private CartService cartService;

	private User user;
	private Product product;
	private Cart cart;
	private CartItem cartItem;
	private CartRequestDto request;

	@BeforeEach
	void setup() {
		user = new User();
		user.setId(1L);

		product = new Product();
		product.setId(1L);
		product.setPrice(100.0);

		cart = new Cart();
		cart.setId(1L);
		cart.setUser(user);

		cartItem = new CartItem();
		cartItem.setCart(cart);
		cartItem.setProduct(product);
		cartItem.setQuantity(1);

		request = new CartRequestDto();
		request.setUserId(1L);
		request.setProductId(1L);
	}

	
	@Test
	void addProductToCart_WhenReceiveValidUserIdAndProductId_ShouldAddProductToCartSuccessfully() {

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(cartItemRepository.findByCartIdAndProductId(1L,1L)).thenReturn(Optional.empty());

		cartService.addproductToCart(request);

		verify(cartItemRepository).save(any(CartItem.class));
	
	}

	@Test
	void addProductToCart_WhenReceiveUserIdWithNoCart_ShouldThrowIdNotFoundException() {

		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(IdNotFoundException.class,
				() -> cartService.addproductToCart(request));
		
		verify(cartItemRepository, never()).save(any(CartItem.class));
		
	}

	@Test
	void addProductToCart_WhenReceiveInvalidProductId_ShouldThrowIdNotFoundException() {

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(IdNotFoundException.class,
				() -> cartService.addproductToCart(request));
		
		verify(cartItemRepository, never()).save(any(CartItem.class));
	
	}

	
	@Test
	void updateProductInCart_WhenReceiveValidUserIdAndProductId_ShouldUpdateProductInCartSuccessfully() {

		CartUpdateDto updateDto = new CartUpdateDto();
		updateDto.setQuantity(5);

		when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
		when(cartItemRepository.findByCartIdAndProductId(1L,1L)).thenReturn(Optional.of(cartItem));

		cartService.updateProductInCart(request, updateDto);

		verify(cartItemRepository).save(cartItem);
	
	}

	@Test
	void updateProductInCart_WhenCartIsNotFound_ShouldThrowIdNotFoundException() {

		CartUpdateDto updateDto = new CartUpdateDto();
		updateDto.setQuantity(5);
		
		when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

		assertThrows(IdNotFoundException.class,
				() -> cartService.updateProductInCart(request, updateDto));
	
		verify(cartItemRepository, never()).save(any(CartItem.class));
		
	}
	
	@Test
	void updateProductInCart_WhenCartItemIsNotFound_ShouldThrowIdNotFoundException() {

		CartUpdateDto updateDto = new CartUpdateDto();
		updateDto.setQuantity(5);
		
		when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
		when(cartItemRepository.findByCartIdAndProductId(1L,1L)).thenReturn(Optional.empty());

		assertThrows(IdNotFoundException.class,
				() -> cartService.updateProductInCart(request, updateDto));
		
		verify(cartItemRepository, never()).save(any(CartItem.class));
		
	}


	@Test
	void removeProductFromCart_WhenReceiveValidUserIdAndProductId_ShouldRemoveProductFromCartSuccessfully() {

		when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
		when(cartItemRepository.findByCartIdAndProductId(1L,1L)).thenReturn(Optional.of(cartItem));

		cartService.removeProductFromCart(request);

		verify(cartItemRepository).delete(cartItem);
	
	}
	
	@Test
	void removeProductFromCart_WhenCartIsNotFound_ShouldThrowIdNotFoundException() {

		when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

		assertThrows(IdNotFoundException.class,
				() -> cartService.removeProductFromCart(request));
	
		verify(cartItemRepository, never()).delete(cartItem);
	
	}

	@Test
	void removeProductFromCart_WhenCartItemIsNotFound_ShouldThrowIdNotFoundException() {

		when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
		when(cartItemRepository.findByCartIdAndProductId(1L,1L)).thenReturn(Optional.empty());

		assertThrows(IdNotFoundException.class,
				() -> cartService.removeProductFromCart(request));
	
		verify(cartItemRepository, never()).delete(cartItem);
		
	}

	
	@Test
	void getCart_WhenReceiveValidUserId_ShouldReturnCartSuccessfully() {

		cart.setCartItems(List.of(cartItem));

		CartItemDto cartItemDto = new CartItemDto();
		ProductResponseDto productResponseDto = new ProductResponseDto();
		CartResponseDto response = new CartResponseDto();
		response.setUserId(1L);

		when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
		when(modelMapper.map(cart, CartResponseDto.class)).thenReturn(response);
		when(modelMapper.map(cartItem, CartItemDto.class)).thenReturn(cartItemDto);
		when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(productResponseDto);

		CartResponseDto result = cartService.getCart(request);

		assertNotNull(result);
		assertEquals(response.getUserId(), result.getUserId());
		
	}

	// GET CART NOT FOUND
	@Test
	void getCart_WhenCartIsNotFound_ShouldThrowIdNotFoundException() {

		when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

		assertThrows(IdNotFoundException.class,
				() -> cartService.getCart(request));
	
	}
}
