package com.incture.e_commerce.utils;

import org.springframework.stereotype.Component;

/*
 * Utility class that simulates a payment gateway response.
 * Payment Success Probability: 90%
 * Payment Failure Probability: 10%
 */
@Component
public class PaymentSimulator {
	
	// Random value to simulate payment success or failure
	private double randomNumber;
	
	public PaymentSimulator() {
		randomNumber = Math.random();
	}
	
	/**
     * Determines whether the simulated payment is successful.
     *
     * @return true if payment succeeds, false otherwise
     */
	public boolean isPaymentSuccessful() {
		return randomNumber > 0.1;
	}
}
