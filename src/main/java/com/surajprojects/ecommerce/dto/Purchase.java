package com.surajprojects.ecommerce.dto;

import java.util.Set;

import com.surajprojects.ecommerce.entity.Address;
import com.surajprojects.ecommerce.entity.Customer;
import com.surajprojects.ecommerce.entity.Order;
import com.surajprojects.ecommerce.entity.OrderItem;

import lombok.Data;

@Data
public class Purchase {

	private Customer customer;
	private Address shippingAddress;
	private Address billingAddress;
	private Order order;
	private Set<OrderItem> orderItems;
}
