package com.surajprojects.ecommerce.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.surajprojects.ecommerce.dao.CustomerRepository;
import com.surajprojects.ecommerce.dto.PaymentInfo;
import com.surajprojects.ecommerce.dto.Purchase;
import com.surajprojects.ecommerce.dto.PurchaseResponse;
import com.surajprojects.ecommerce.entity.Customer;
import com.surajprojects.ecommerce.entity.Order;
import com.surajprojects.ecommerce.entity.OrderItem;

import jakarta.transaction.Transactional;
import lombok.extern.java.Log;

@Service
@Log
public class CheckoutServiceImpl implements CheckoutService{

	@Autowired
	private CustomerRepository customerRepository;
	
	public CheckoutServiceImpl(CustomerRepository customerRepository,
								@Value("${stripe.key.secret}") String secretKey) {
		this.customerRepository = customerRepository;
	
		//intialize stripe API with secret key
		Stripe.apiKey = secretKey;
	}
	
	@Override
	@Transactional
	public PurchaseResponse placeOrder(Purchase purchase) {
		
		// retrieve the order info from dto
		Order order = purchase.getOrder();
		
		//generate tracking number
		String orderTrackingNumber = generateOrderTrackingNumber();
		order.setOrderTrackingNumber(orderTrackingNumber);
		
		//populate order with orderItems
		Set<OrderItem> orderItems = purchase.getOrderItems();
		orderItems.forEach( item -> order.add(item));
		
		//populate order with billingAddress and shppingAddress
		order.setBillingAddress(purchase.getBillingAddress());
		order.setShippingAddress(purchase.getShippingAddress());
		
		//populate customer with order
		Customer customer = purchase.getCustomer();
		
		//check if this is an existing customer
		String theEmail = customer.getEmail();
		
		Customer customerFromDB = customerRepository.findByEmail(theEmail);
		
		if(customerFromDB != null) {
			
			customer = customerFromDB;
		}
		
		customer.add(order);
		
		//save to the database
		customerRepository.save(customer);
		log.info(orderTrackingNumber);
		//return a response
		return new PurchaseResponse(orderTrackingNumber);
	}
	
	private String generateOrderTrackingNumber() {
		
		//generate a random UUID number (UUID version-4)
		return UUID.randomUUID().toString();
	}

	@Override
	public PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException {

		List<String> paymentMethodTypes = new ArrayList<>();
		paymentMethodTypes.add("card");
		
		Map<String, Object> params = new HashMap<>();
		params.put("amount", paymentInfo.getAmount());
		params.put("currency", paymentInfo.getCurrency());
		params.put("payment_method_types", paymentMethodTypes);
		params.put("description", "SurajShoppy purchase");
		params.put("receipt_email", paymentInfo.getReceiptEmail());
		
		return PaymentIntent.create(params);
	}

}
