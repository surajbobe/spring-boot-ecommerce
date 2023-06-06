package com.surajprojects.ecommerce.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.surajprojects.ecommerce.dto.PaymentInfo;
import com.surajprojects.ecommerce.dto.Purchase;
import com.surajprojects.ecommerce.dto.PurchaseResponse;

public interface CheckoutService {

	PurchaseResponse placeOrder(Purchase purchase);
	
	PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException;
}
