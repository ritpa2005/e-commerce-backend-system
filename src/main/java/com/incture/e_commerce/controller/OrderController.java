package com.incture.e_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.incture.e_commerce.dto.OrderRequestDto;
import com.incture.e_commerce.dto.OrderResponseDto;
import com.incture.e_commerce.dto.UserRequestDto;
import com.incture.e_commerce.entity.User;
import com.incture.e_commerce.service.OrderService;

@RestController
@RequestMapping("/api/orders/")
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	
	@PostMapping("/checkout")
	public ResponseEntity<OrderResponseDto> checkout(@AuthenticationPrincipal User user){
		UserRequestDto userRequestDto = new UserRequestDto();
		userRequestDto.setUserId(user.getId());
		
		OrderResponseDto orderResponseDto = orderService.checkout(userRequestDto);
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(orderResponseDto);
	}
		
	
	@GetMapping("/")
	public ResponseEntity<List<OrderResponseDto>> getOrders(@AuthenticationPrincipal User user){
		UserRequestDto userRequestDto = new UserRequestDto();
		userRequestDto.setUserId(user.getId());
		
		List<OrderResponseDto> orders = orderService.getOrders(userRequestDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(orders);
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<OrderResponseDto> getOrder(@AuthenticationPrincipal User user, @PathVariable("id") Long orderId){
		UserRequestDto userRequestDto = new UserRequestDto();
		userRequestDto.setUserId(user.getId());
		
		OrderRequestDto orderRequestDto = new OrderRequestDto();
		orderRequestDto.setOrderId(orderId);
		
		OrderResponseDto orderResponseDto = orderService.getOrder(orderRequestDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(orderResponseDto);
	}
	
	
	@PutMapping("/{id}/status")
	public ResponseEntity<OrderResponseDto> editOrderStatus(@AuthenticationPrincipal User user, @PathVariable("id") Long orderId){
		UserRequestDto userRequestDto = new UserRequestDto();
		userRequestDto.setUserId(user.getId());
		
		OrderRequestDto orderRequestDto = new OrderRequestDto();
		orderRequestDto.setOrderId(orderId);
		
		OrderResponseDto orderResponseDto = orderService.updateOrderStatus(orderRequestDto);
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(orderResponseDto);
	}
	
}
