package com.incture.e_commerce.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.incture.e_commerce.dto.OrderItemResponseDto;
import com.incture.e_commerce.dto.OrderRequestDto;
import com.incture.e_commerce.dto.OrderResponseDto;
import com.incture.e_commerce.dto.ProductResponseDto;
import com.incture.e_commerce.dto.UserRequestDto;
import com.incture.e_commerce.entity.*;
import com.incture.e_commerce.enums.OrderStatus;
import com.incture.e_commerce.enums.PaymentStatus;
import com.incture.e_commerce.exception.EmptyCartException;
import com.incture.e_commerce.exception.IdNotFoundException;
import com.incture.e_commerce.repository.*;
import com.incture.e_commerce.utils.PaymentSimulator;

/**
 * Service responsible for handling order related operations.
 */
@Service
public class OrderService {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private PaymentSimulator paymentSimulator;
	
	@Autowired
    private EmailService emailService;
	
	/**
	 * Performs checkout for a user and creates a new order.
	 * Validates cart items and convert them to order items
	 * Process payment simulation and save order accordingly
	 *
	 * @param userRequestDto contains userId
	 * @return Orders entity containing order details
	 */
	public OrderResponseDto checkout(UserRequestDto userRequestDto) {
		
		long userId = userRequestDto.getUserId();
		logger.info("Initiating checkout for user {}", userId);
		
		// Fetching cart for user
		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> {
					logger.error("Cart not found for user {}", userId);
					return new IdNotFoundException("Cart does not exist.");
				});
		
		// Check if cart is empty
		List<CartItem> cartItems = cart.getCartItems();
		if(cartItems.isEmpty()) {
			logger.error("Checkout failed. Cart is empty for user {}", userId);
			throw new EmptyCartException("Cart is empty.");
		}
		
		User user = userRepository.findById(userId).get();
		
		// Creating a new order
		Orders order = new Orders();
		order.setUser(user);
		order.setOrder_date(LocalDateTime.now());
		order.setOrder_status(OrderStatus.PLACED);
		order.setPayment_status(PaymentStatus.PENDING);
		
		order = orderRepository.save(order);
		
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		double total = 0;
		boolean isOrderInvalid = false;
		
		logger.debug("Processing {} cart items for order", cartItems.size());
		
		// Check if order is valid or not
		for(CartItem cartItem: cartItems) {
			// Check if stock is available
			if(cartItem.getQuantity() > cartItem.getProduct().getStock()) {
				logger.error("Insufficient stock for product {}", cartItem.getProduct().getId());
			
				isOrderInvalid = true;
				break;
			}
		}
		
		// If order invalid due to stock issue
		if(isOrderInvalid) {
			logger.error("Order cancelled due to insufficient stock");
			
			order.setOrder_status(OrderStatus.CANCELLED);
			order.setPayment_status(PaymentStatus.CANCELLED);
			order.setTotal_amount(0);
						
			order = orderRepository.save(order);
			
			OrderResponseDto orderDto = modelMapper.map(order, OrderResponseDto.class);
			orderDto.setOrderId(order.getId());
			orderDto.setUserId(userId);
			return orderDto;
		}
		
		// Convert cart items into order items
		for(CartItem cartItem: cartItems) {
			
			OrderItem orderItem = modelMapper.map(cartItem, OrderItem.class);
			orderItem.setOrder(order);
			orderItem.setPrice(cartItem.getProduct().getPrice());
			
			OrderItem savedOrderItem = orderItemRepository.save(orderItem);			
			total += savedOrderItem.getPrice() * savedOrderItem.getQuantity();

			orderItems.add(savedOrderItem);
		}
		
		order.setTotal_amount(total);
		order.setOrderItems(orderItems);
		
		// Simulation of payment result
		boolean isPaymentDone = paymentSimulator.isPaymentSuccessful();
		logger.debug("Payment simulation result: {}", isPaymentDone);
		
		if(!isPaymentDone) {
			logger.error("Payment failed. Cancelling order");
			
			order.setOrder_status(OrderStatus.CANCELLED);
			order.setPayment_status(PaymentStatus.UNSUCCESSFUL);
		}
		else {
			logger.info("Payment successful for order");
			
			order.setPayment_status(PaymentStatus.SUCCESSFUL);
		}
		
		Orders savedOrder = orderRepository.save(order);
		
		// Clear cart if payment successful
		if(isPaymentDone) {		
			
			logger.debug("Updating inventory stock of products after successful payment.");
			for(CartItem cartItem: cartItems) {
				Product product = productRepository.findById(cartItem.getProduct().getId()).get();
				product.setStock(product.getStock() - cartItem.getQuantity());
				
				productRepository.save(product);
			}

			logger.info("Clearing cart for user {}", userId);
			cartItemRepository.deleteByCartId(cart.getId());
			
			cart.setTotal_price(0);
			cartRepository.save(cart);
			
			emailService.sendOrderConfirmation(user.getEmail(), order.getId());
		}
		
		logger.info("Checkout completed for user {}. Order ID {}", userId, savedOrder.getId());
		
		OrderResponseDto orderDto = modelMapper.map(savedOrder, OrderResponseDto.class);
		orderDto.setOrderId(savedOrder.getId());
		orderDto.setUserId(userId);
		
		List<OrderItemResponseDto> itemListDto = new ArrayList<OrderItemResponseDto>();
		for(OrderItem item: savedOrder.getOrderItems()) {
			ProductResponseDto product = modelMapper.map(item.getProduct(), ProductResponseDto.class);
			OrderItemResponseDto itemDto = modelMapper.map(item, OrderItemResponseDto.class);
			itemDto.setOrderItemId(item.getId());
			itemDto.setProduct(product);
			
			itemListDto.add(itemDto);
		}
		
		orderDto.setOrderItems(itemListDto);
		
		return orderDto;
		
	}
	
	
	/**
	 * Fetches all orders for a specific user.
	 *
	 * @param userRequestDto contains userId
	 * @return list of Orders
	 */
	public List<OrderResponseDto> getOrders(UserRequestDto userRequestDto) {
		
		long userId = userRequestDto.getUserId();
		logger.info("Fetching orders for user {}", userId);
		
		if(userRepository.findById(userId).isEmpty()) {
			logger.error("User not found with id {}", userId);
			throw new IdNotFoundException("User with id= " + userId + " not found.");
		}
		
		List<Orders> orders = orderRepository.findByUserId(userId);
		logger.debug("Found {} orders for user {}", orders.size(), userId);
		
		List<OrderResponseDto> orderListDto = new ArrayList<OrderResponseDto>();
		
		for(Orders order: orders) {
			OrderResponseDto orderDto = modelMapper.map(order, OrderResponseDto.class);
			orderDto.setOrderId(order.getId());
			orderDto.setUserId(userId);
			
			List<OrderItemResponseDto> itemListDto = new ArrayList<OrderItemResponseDto>();
			for(OrderItem item: order.getOrderItems()) {
				ProductResponseDto product = modelMapper.map(item.getProduct(), ProductResponseDto.class);
				OrderItemResponseDto itemDto = modelMapper.map(item, OrderItemResponseDto.class);
				itemDto.setOrderItemId(item.getId());
				itemDto.setProduct(product);
				
				itemListDto.add(itemDto);
			}
			
			orderDto.setOrderItems(itemListDto);
			orderListDto.add(orderDto);
		}
		
		return orderListDto;
		
	}
	
	
	/**
	 * Fetch a specific order by ID.
	 *
	 * @param orderRequestDto contains orderId
	 * @return Orders entity
	 */
	public OrderResponseDto getOrder(OrderRequestDto orderRequestDto) {
		
		long orderId = orderRequestDto.getOrderId();
		logger.info("Fetching order with id {}", orderId);
		
		Orders order = orderRepository.findById(orderId)
				.orElseThrow(() -> {
					logger.error("Order not found with id {}", orderId);

					return new IdNotFoundException("Order with id = " + orderId + " not found.");
				});
		
		OrderResponseDto orderDto = modelMapper.map(order, OrderResponseDto.class);
		orderDto.setOrderId(order.getId());
		orderDto.setUserId(order.getUser().getId());
		
		List<OrderItemResponseDto> itemListDto = new ArrayList<OrderItemResponseDto>();
		for(OrderItem item: order.getOrderItems()) {
			ProductResponseDto product = modelMapper.map(item.getProduct(), ProductResponseDto.class);
			OrderItemResponseDto itemDto = modelMapper.map(item, OrderItemResponseDto.class);
			itemDto.setOrderItemId(item.getId());
			itemDto.setProduct(product);
			
			itemListDto.add(itemDto);
		}
		
		orderDto.setOrderItems(itemListDto);
		
		return orderDto;
		
	}
	
	
	/**
	 * Updates the order status to the next stage.
	 * Status Flow: PLACED -> SHIPPED -> DELIVERED
	 *
	 * @param orderRequestDto contains orderId
	 * @return updated Orders entity
	 */
	public OrderResponseDto updateOrderStatus(OrderRequestDto orderRequestDto) {
		
		long orderId = orderRequestDto.getOrderId();
		
		Orders order = orderRepository.findById(orderId)
				.orElseThrow(() -> {

					logger.error("Order not found with id {}", orderId);

					return new IdNotFoundException("Order with id = " + orderId + " not found.");
				});
		
		OrderStatus current = order.getOrder_status();
		OrderStatus next = OrderStatus.DELIVERED;
		
		// Determine next status
		if(current == OrderStatus.PLACED) {
			next = OrderStatus.SHIPPED;
		}
		else if(current == OrderStatus.CANCELLED) {
			next = OrderStatus.CANCELLED;
		}
		
		order.setOrder_status(next);
		logger.debug("Order status updated from {} to {}", current, next);
		
		order = orderRepository.save(order);
		logger.info("Order {} status updated successfully", orderId);
		
		
		OrderResponseDto orderDto = modelMapper.map(order, OrderResponseDto.class);
		orderDto.setOrderId(order.getId());
		orderDto.setUserId(order.getUser().getId());
		
		List<OrderItemResponseDto> itemListDto = new ArrayList<OrderItemResponseDto>();
		for(OrderItem item: order.getOrderItems()) {
			ProductResponseDto product = modelMapper.map(item.getProduct(), ProductResponseDto.class);
			OrderItemResponseDto itemDto = modelMapper.map(item, OrderItemResponseDto.class);
			itemDto.setOrderItemId(item.getId());
			itemDto.setProduct(product);
			
			itemListDto.add(itemDto);
		}
		
		orderDto.setOrderItems(itemListDto);
		return orderDto;
		
	}
	
}



