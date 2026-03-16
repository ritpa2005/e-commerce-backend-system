package com.incture.e_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.incture.e_commerce.dto.CartRequestDto;
import com.incture.e_commerce.dto.CartResponseDto;
import com.incture.e_commerce.dto.CartUpdateDto;
import com.incture.e_commerce.entity.User;
import com.incture.e_commerce.service.CartService;

@RestController
@RequestMapping("/api/cart/")
public class CartController {

	@Autowired
	private CartService cartService;
	
	@PostMapping("/add/{productId}")
	public ResponseEntity<String> addProductToCart(@PathVariable Long productId, @AuthenticationPrincipal User user){

		CartRequestDto cartRequestDto = new CartRequestDto();
		cartRequestDto.setProductId(productId);
		cartRequestDto.setUserId(user.getId());
		
		cartService.addproductToCart(cartRequestDto);
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body("Product Added To Cart Successfully");
	}
	
	
	@PutMapping("/update/{productId}")
	public ResponseEntity<String> updateProductInCart(@PathVariable Long productId, @AuthenticationPrincipal User user, @RequestBody CartUpdateDto cartUpdateDto){

		CartRequestDto cartRequestDto = new CartRequestDto();
		cartRequestDto.setProductId(productId);
		cartRequestDto.setUserId(user.getId());
		
		cartService.updateProductInCart(cartRequestDto, cartUpdateDto);
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body("Product Updated In Cart Successfully");
	}
	
	
	@DeleteMapping("/remove/{productId}")
	public ResponseEntity<String> removeProductFromCart(@PathVariable Long productId, @AuthenticationPrincipal User user){

		CartRequestDto cartRequestDto = new CartRequestDto();
		cartRequestDto.setProductId(productId);
		cartRequestDto.setUserId(user.getId());
		
		cartService.removeProductFromCart(cartRequestDto);
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Product Removed From Cart Successfully");
	}
	
	
	@GetMapping("/")
	public ResponseEntity<CartResponseDto> getCart(@AuthenticationPrincipal User user){

		CartRequestDto cartRequestDto = new CartRequestDto();
		cartRequestDto.setUserId(user.getId());
		
		CartResponseDto cartResponseDto = cartService.getCart(cartRequestDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(cartResponseDto);
	}
}
