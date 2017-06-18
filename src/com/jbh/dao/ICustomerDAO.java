package com.jbh.dao;

import java.sql.SQLException;
import java.util.Collection;

import com.jbh.beans.Coupon;
import com.jbh.beans.Customer;

public interface ICustomerDAO {

	public void createCustomer(Customer customer) throws SQLException;

	public void removeCustomer(Customer customer) throws SQLException;

	public void updateCustomer(Customer customer) throws SQLException;

	public Customer getCustomer(long id) throws SQLException;

	public Collection<Customer> getAllCustomers() throws SQLException;

	public Collection<Coupon> getCustomerCoupons(long custId) throws SQLException;
	
	public void linkCustomerCoupon(long companyId, long couponId) throws SQLException;
		
	public void unlinkAllCustomerCoupon(long customerId) throws SQLException;

	public boolean login(String custName, String password) throws SQLException, Exception;

}
