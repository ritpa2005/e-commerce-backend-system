package com.incture.e_commerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
	@Mock
    private UserRepository userRepository;

	@Mock
	private ProductRepository productRepository;
	
    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ModelMapper modelMapper;
    
    @Mock
    private PaymentSimulator paymentSimulator;
    
    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Cart cart;
    private CartItem cartItem;
    private Product product;
    private Orders order;
    private OrderItem orderItem;
    private OrderResponseDto orderResponseDto;
    private OrderItemResponseDto orderItemResponseDto;
    private ProductResponseDto productResponseDto;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);

        product = new Product();
        product.setId(10L);
        product.setPrice(100);
        product.setStock(10);
        
        productResponseDto = new ProductResponseDto();
        productResponseDto.setId(1L);
        productResponseDto.setStock(10);

        cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        List<CartItem> cartItems = new ArrayList<CartItem>();
        cartItems.add(cartItem);

        cart = new Cart();
        cart.setId(1L);
        cart.setCartItems(cartItems);
        
        orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        
        orderItemResponseDto = new OrderItemResponseDto();
        orderItemResponseDto.setProduct(productResponseDto);
        orderItemResponseDto.setQuantity(2);
        
        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        orderItems.add(orderItem);
        
        order = new Orders();
        order.setOrderItems(orderItems);
        order.setUser(user);
        
        List<OrderItemResponseDto> itemListDto = new ArrayList<OrderItemResponseDto>();
        itemListDto.add(orderItemResponseDto);
        
        orderResponseDto = new OrderResponseDto();
        orderResponseDto.setOrderItems(itemListDto);
        orderResponseDto.setUserId(user.getId());
    }
    
    
    @Test
    void checkout_WhenOrderIsValidAndPaymentIsSuccessful_ShouldCheckoutSuccessfully() {

        UserRequestDto dto = new UserRequestDto();
        dto.setUserId(1L);

        orderItem.setQuantity(2);
        orderItem.setPrice(100);
        
        orderItemResponseDto.setQuantity(2);
        orderItemResponseDto.setPrice(100);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(cartItem, OrderItem.class)).thenReturn(orderItem);
        when(orderItemRepository.save(any())).thenReturn(orderItem);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(paymentSimulator.isPaymentSuccessful()).thenReturn(true);
        when(orderRepository.save(any())).thenReturn(order);
        doNothing().when(cartItemRepository).deleteByCartId(1L);
        when(cartRepository.save(any())).thenReturn(cart);
        doNothing().when(emailService).sendOrderConfirmation(user.getEmail(), order.getId());
        when(modelMapper.map(order, OrderResponseDto.class)).thenReturn(orderResponseDto);
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(productResponseDto);
        when(modelMapper.map(orderItem, OrderItemResponseDto.class)).thenReturn(orderItemResponseDto);
        
        OrderResponseDto result = orderService.checkout(dto);

        assertNotNull(result);
        assertEquals(result.getOrderItems().size(), order.getOrderItems().size());
        verify(orderRepository, times(2)).save(any());
        
    }
    
    @Test
    void checkout_WhenCartIsNotFound_ShouldThrowIdNotFoundException() {

        UserRequestDto dto = new UserRequestDto();
        dto.setUserId(1L);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> orderService.checkout(dto));
        
    }
    
    @Test
    void checkout_WhenCartIsEmpty_ShouldThrowEmptyCartException() {

        UserRequestDto dto = new UserRequestDto();
        dto.setUserId(1L);

        cart.setCartItems(new ArrayList<>());

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        assertThrows(EmptyCartException.class,
                () -> orderService.checkout(dto));
        
    }
    
    @Test
    void checkout_WhenOrderIsInvalid_ShouldCancelTheOrder() {

        UserRequestDto dto = new UserRequestDto();
        dto.setUserId(1L);

        orderItem.setQuantity(20);
        orderItem.setPrice(100);
        
        orderItemResponseDto.setQuantity(2);
        orderItemResponseDto.setPrice(100);
        
        order.setPayment_status(PaymentStatus.CANCELLED);
        order.setOrder_status(OrderStatus.CANCELLED);
        
        orderResponseDto.setPayment_status(PaymentStatus.CANCELLED);
        orderResponseDto.setOrder_status(OrderStatus.CANCELLED);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(cartItem, OrderItem.class)).thenReturn(orderItem);
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);
        when(orderRepository.save(any())).thenReturn(order);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(modelMapper.map(order, OrderResponseDto.class)).thenReturn(orderResponseDto);
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(productResponseDto);
        when(modelMapper.map(orderItem, OrderItemResponseDto.class)).thenReturn(orderItemResponseDto);
        

        OrderResponseDto result = orderService.checkout(dto);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getOrder_status());
        verify(orderRepository, times(2)).save(any());
        
    }
    
    @Test
    void checkout_WhenPaymentIsUnsuccessful_ShouldCancelTheOrder() {

        UserRequestDto dto = new UserRequestDto();
        dto.setUserId(1L);

        orderItem.setQuantity(2);
        orderItem.setPrice(100);
        
        orderItemResponseDto.setQuantity(2);
        orderItemResponseDto.setPrice(100);
        
        order.setPayment_status(PaymentStatus.UNSUCCESSFUL);
        order.setOrder_status(OrderStatus.CANCELLED);
        
        orderResponseDto.setPayment_status(PaymentStatus.UNSUCCESSFUL);
        orderResponseDto.setOrder_status(OrderStatus.CANCELLED);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(cartItem, OrderItem.class)).thenReturn(orderItem);
        when(orderItemRepository.save(any())).thenReturn(orderItem);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(paymentSimulator.isPaymentSuccessful()).thenReturn(true);
        when(orderRepository.save(any())).thenReturn(order);
        when(modelMapper.map(order, OrderResponseDto.class)).thenReturn(orderResponseDto);
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(productResponseDto);
        when(modelMapper.map(orderItem, OrderItemResponseDto.class)).thenReturn(orderItemResponseDto);
        
        OrderResponseDto result = orderService.checkout(dto);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getOrder_status());
        assertEquals(PaymentStatus.UNSUCCESSFUL, result.getPayment_status());
        verify(orderRepository, times(2)).save(any());
        
    }
    
    
    @Test
    void getOrders_WhenReceiveValidUserId_ShouldReturnAllOrdersSuccessfully() {

        UserRequestDto dto = new UserRequestDto();
        dto.setUserId(1L);

        List<Orders> orders = new ArrayList<>();
        orders.add(order);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.findByUserId(1L)).thenReturn(orders);
        when(modelMapper.map(order, OrderResponseDto.class)).thenReturn(orderResponseDto);
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(productResponseDto);
        when(modelMapper.map(orderItem, OrderItemResponseDto.class)).thenReturn(orderItemResponseDto);

        List<OrderResponseDto> result = orderService.getOrders(dto);

        assertEquals(1, result.size());
        verify(orderRepository).findByUserId(1L);
        
    }
    
    @Test
    void getOrders_WhenReceiveInvalidUserId_ShouldThrowIdNotFoundException() {

        UserRequestDto dto = new UserRequestDto();
        dto.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> orderService.getOrders(dto));
        
    }
    
    
    @Test
    void getOrder_WhenWhenReceiveValidOrderId_ShouldReturnOrderSuccessfully() {

        OrderRequestDto dto = new OrderRequestDto();
        dto.setOrderId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(modelMapper.map(order, OrderResponseDto.class)).thenReturn(orderResponseDto);
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(productResponseDto);
        when(modelMapper.map(orderItem, OrderItemResponseDto.class)).thenReturn(orderItemResponseDto);

        OrderResponseDto result = orderService.getOrder(dto);

        assertNotNull(result);
    }

    @Test
    void getOrder_WhenReceiveInvalidOrderId_ShouldThrowIdNotFoundException() {

        OrderRequestDto dto = new OrderRequestDto();
        dto.setOrderId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> orderService.getOrder(dto));
    }
    
    
    @Test
    void updateOrderStatus_WhenWhenReceiveValidOrderId_ShouldUpdateStatusSuccessfully() {

        OrderRequestDto dto = new OrderRequestDto();
        dto.setOrderId(1L);

        order.setOrder_status(OrderStatus.PLACED);
        
        orderResponseDto.setOrder_status(OrderStatus.SHIPPED);
        
        Orders updated = new Orders();
        updated.setUser(user);
        updated.setOrder_status(OrderStatus.SHIPPED);
        updated.setOrderItems(order.getOrderItems());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(updated);
        when(modelMapper.map(updated, OrderResponseDto.class)).thenReturn(orderResponseDto);
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(productResponseDto);
        when(modelMapper.map(orderItem, OrderItemResponseDto.class)).thenReturn(orderItemResponseDto);

        OrderResponseDto result = orderService.updateOrderStatus(dto);

        assertEquals(OrderStatus.SHIPPED, result.getOrder_status());
    }
    
    @Test
    void updateOrderStatus_WhenWhenReceiveInvalidOrderId_ShouldThrowIdNotFoundException() {

    	OrderRequestDto dto = new OrderRequestDto();
        dto.setOrderId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class,
                () -> orderService.updateOrderStatus(dto));
        
    }
    
}






