package com.jbh.facade;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;

import com.jbh.beans.Coupon;
import com.jbh.dao.CompanyDAO;
import com.jbh.dao.CouponDAO;
import com.jbh.dao.CustomerDAO;
import com.jbh.enums.CouponType;

public class CustomerFacade implements CouponClientFacade {

	private CustomerDAO customerDAO = new CustomerDAO();
	private CouponDAO couponDAO = new CouponDAO();
	private long loggedCustomer = 0;

	public CustomerFacade() {}

	public long getLoggedCustomer() {
		return loggedCustomer;
	}

	public void setLoggedCustomer(long customerID) {
		this.loggedCustomer = customerID;
	}


	public void purchaseCoupon(Coupon coupon) throws SQLException {
		Coupon targetCoupon = new Coupon();
		try {
		targetCoupon = couponDAO.getCoupon(coupon.getId());
		Collection<Coupon> customerCoupons = new ArrayList<>();
		customerCoupons = customerDAO.getCustomerCoupons(getLoggedCustomer());
		if (!customerCoupons.contains(targetCoupon))
			if (targetCoupon.getEndDate().after(new Date(System.currentTimeMillis())))
				if (targetCoupon.getAmount() > 0) {
					targetCoupon.setAmount(targetCoupon.getAmount() - 1);
					couponDAO.updateCoupon(targetCoupon);
					customerDAO.linkCustomerCoupon(getLoggedCustomer(), targetCoupon.getId());
					System.out.println("Coupon " + targetCoupon.getTitle() + " " 
					+ targetCoupon.getId() + " Purchased!");
					System.out.println("Expires at: " + targetCoupon.getEndDate().toString());
				}
		}
		catch(SQLException e){
			throw new SQLException("Failed to purchase Coupon. Please contact our support team.");
		}
	}
	
	public Collection<Coupon> getAllPurchasedCoupons() throws SQLException  {
		Collection<Coupon> purchased = null;
		try {
			purchased = customerDAO.getCustomerCoupons(loggedCustomer);
			System.out.println("Purchased coupons retrieved!");
		} catch (SQLException e) {
			throw new SQLException("Failed to retrieve purchased Coupons. Please contact our"
					+ "support team.");
		}
		return purchased;
	}

	public Collection<Coupon> getAllPurchasedCouponsByType(CouponType type) throws SQLException  {
		Collection<Coupon> purchasedOfType = new ArrayList<>();
		try {
			purchasedOfType = customerDAO.getCustomerCoupons(loggedCustomer);
		} catch (SQLException e) {
			throw new SQLException("Failed to retrieve Coupons. Please contact our support team.");
		}
		Iterator<Coupon> iter = purchasedOfType.iterator();
		Coupon coup = new Coupon();
		while (iter.hasNext()) {
			coup = iter.next();
			if(!type.equals(coup.getType())){
				iter.remove();
			}
		}
		System.out.println("Purchased coupons of type " + type + " retrieved!");
		return purchasedOfType;
	}

	public Collection<Coupon> getAllPurchasedCouponsByPrice(double price) throws SQLException {
		Collection<Coupon> purchasedOfPrice = new ArrayList<>();
		try {
			purchasedOfPrice = customerDAO.getCustomerCoupons(loggedCustomer);
		} catch (SQLException e) {
			throw new SQLException("Failed to retrieve Coupons. Please contact our support team.");
		}
		Iterator<Coupon> iter = purchasedOfPrice.iterator();
		while(iter.hasNext()){
			Coupon coup = iter.next();
			if (coup.getPrice() < price)
				iter.remove();
		}
		System.out.println("Purchased coupons that cost at least " + price + " retrieved!");
		return purchasedOfPrice;
	}

	@Override
	public CouponClientFacade login(String name, String password) throws Exception {
		if (customerDAO.login(name, password)) {
			CustomerFacade clientFacade = new CustomerFacade();
			clientFacade.setLoggedCustomer(customerDAO.getLoginID());
			return clientFacade;
		} else
			throw new Exception("Login FAILED. Please contact our support team.");
	}

	@Override
	public String toString() {
		return "customer";
	}

}
