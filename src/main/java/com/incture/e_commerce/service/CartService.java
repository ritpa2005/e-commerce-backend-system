package com.incture.e_commerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.incture.e_commerce.dto.CartItemDto;
import com.incture.e_commerce.dto.CartRequestDto;
import com.incture.e_commerce.dto.CartResponseDto;
import com.incture.e_commerce.dto.CartUpdateDto;
import com.incture.e_commerce.dto.ProductResponseDto;
import com.incture.e_commerce.entity.Cart;
import com.incture.e_commerce.entity.CartItem;
import com.incture.e_commerce.entity.Product;
import com.incture.e_commerce.entity.User;
import com.incture.e_commerce.exception.IdNotFoundException;
import com.incture.e_commerce.repository.CartItemRepository;
import com.incture.e_commerce.repository.CartRepository;
import com.incture.e_commerce.repository.ProductRepository;
import com.incture.e_commerce.repository.UserRepository;

/*
 * Service layer responsible for handling cart-related business logic.
 */
@Service
public class CartService {

	private static final Logger logger = LoggerFactory.getLogger(CartService.class);
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	/**
	 * Adds a product to the user's cart.
	 * @param cartRequestDto contains userId and productId
	 * If cart doesn't exist, create one.
	 * If product already exists in cart, increase quantity; else add new cart item.
	 */
	public void addproductToCart(CartRequestDto cartRequestDto) {
		
		long userId = cartRequestDto.getUserId();
		long productId = cartRequestDto.getProductId();
		
		logger.info("Adding product {} to cart for user {}", productId, userId);
		
		// Fetching user from database
		User user = userRepository.findById(userId)
				.orElseThrow(() -> {
					logger.error("User not found with id {}", userId);
					return new IdNotFoundException("User with id = " + userId + " not found.");
				});
		
		// Fetch cart if exists, otherwise create a new cart
		Cart cart = cartRepository.findByUserId(userId)
				.orElseGet(() -> {
					logger.info("Cart not found for user {}. Creating new cart.", userId);
					
					Cart newCart = new Cart();
					newCart.setUser(user);
					newCart.setTotal_price(0.0);
					return cartRepository.save(newCart);
				});
		
		// Fetching product from database
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> {
					logger.error("Product not found with id {}", productId);
					return new IdNotFoundException("Product with id = " + productId + " not found.");
				});
		
		// Check if product already exists in cart
		Optional<CartItem> existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
	    if(existing.isPresent()){
	    	logger.debug("Product already exists in cart. Increasing quantity.");
	    	
	        CartItem item = existing.get();
	        item.setQuantity(item.getQuantity() + 1);
	        
	        cartItemRepository.save(item);
	    }
	    else{
	    	logger.debug("Product not in cart. Creating new cart item.");
	    	
	        CartItem item = new CartItem();
	        item.setCart(cart);
	        item.setProduct(product);
	        item.setQuantity(1);
	        
	        cartItemRepository.save(item);
	    }
	}
	
	
	/**
	 * Updates quantity of a specific product in the cart.
	 * 
	 * @param cartRequestDto contains userId and productId
	 * @param cartUpdateDto contains quantity of product
	 */
	public void updateProductInCart(CartRequestDto cartRequestDto, CartUpdateDto cartUpdateDto) {
		
		long userId = cartRequestDto.getUserId();
		long productId = cartRequestDto.getProductId();
		long quantity = cartUpdateDto.getQuantity();
		
		// Fetching cart
		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> {
					logger.error("Cart not found for user {}", userId);
					return new IdNotFoundException("Cart does not exist.");
				});
		
		// Fetching cart item
		CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
				.orElseThrow(() -> {
					logger.error("Cart item not found for product {} in cart {}", productId, cart.getId());
					return new IdNotFoundException("Cart item does not exist.");
				});
		
		// Updating quantity
		item.setQuantity(quantity);
		cartItemRepository.save(item);
		
		logger.info("Cart item updated successfully");
		
	}
	
	
	/**
	 * Removes a product from the user's cart.
	 * 
	 * @param cartRequestDto contains the userId and productId
	 */
	public void removeProductFromCart(CartRequestDto cartRequestDto) {
		
		long userId = cartRequestDto.getUserId();
		long productId = cartRequestDto.getProductId();
		
		logger.warn("Removing product {} from cart for user {}", productId, userId);
		
		// Fetching cart
		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> {
					logger.error("Cart not found for user {}", userId);
					return new IdNotFoundException("Cart does not exist.");
				});
		
		// Fetching cart item
		CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
				.orElseThrow(() -> {
					logger.error("Cart item not found for product {}", productId);
					return new IdNotFoundException("Cart item does not exist.");
				});
		
		// Deleting item from cart
		cartItemRepository.delete(item);
		
		logger.warn("Product {} removed from cart successfully", productId);
		
	}
	
	
	/**
	 * Fetches the cart details for a user.
	 * 
	 * @param cartRequestDto contains the userId
	 * @return CartResponseDto containing cart items and total price
	 */
	public CartResponseDto getCart(CartRequestDto cartRequestDto) {
		
		long userId = cartRequestDto.getUserId();
		
		logger.info("Fetching cart for user {}", userId);
		
		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> {
					logger.error("Cart not found for user {}", userId);
					return new IdNotFoundException("Cart does not exist.");
				});
		
		CartResponseDto cartResponseDto = modelMapper.map(cart, CartResponseDto.class);
		
		List<CartItem> cartItems = cart.getCartItems();
		List<CartItemDto> itemDtos = new ArrayList<CartItemDto>();
		double totalPrice = 0.0;
		
		for (CartItem item : cartItems) {
			
			// Mapping each product in cart to product response DTO
			ProductResponseDto product = modelMapper.map(item.getProduct(), ProductResponseDto.class);
			
			CartItemDto itemDto = modelMapper.map(item, CartItemDto.class);
			itemDto.setProduct(product);
			
			itemDtos.add(itemDto);
			
			totalPrice += item.getQuantity() * item.getProduct().getPrice();
			
		}
		
		cartResponseDto.setUserId(userId);
		cartResponseDto.setCartItems(itemDtos);
		cartResponseDto.setTotalPrice(totalPrice);
		
		logger.info("Cart fetched successfully for user {}", userId);
		
		return cartResponseDto;
		
	}
	
}
