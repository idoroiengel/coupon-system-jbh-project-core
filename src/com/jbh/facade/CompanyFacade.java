package com.jbh.facade;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.jbh.beans.Coupon;
import com.jbh.dao.CompanyDAO;
import com.jbh.dao.CouponDAO;
import com.jbh.enums.CouponType;

public class CompanyFacade implements CouponClientFacade {

	private CompanyDAO companyDAO = new CompanyDAO();
	private CouponDAO couponDAO = new CouponDAO();
	private long loggedCompany = 0;

	public CompanyFacade() {}
	
	public long getLoggedCompany() {
		return loggedCompany;
	}

	public void setLoggedCompany(long loggedCompany) {
		this.loggedCompany = loggedCompany;
	}

	public void createCoupon(Coupon coupon) throws Exception {
		if (couponDAO.getAllCoupons().contains(coupon)) {
			throw new Exception("Failed to create Coupon."
					+ " Adding a Coupon with an existing name is not allowed.");
		} else {
			couponDAO.createCoupon(coupon);
			companyDAO.linkCompanyCoupon(loggedCompany, coupon.getId());
		}
	}

	public void removeCoupon(Coupon coupon) throws SQLException {
		try{
		couponDAO.removeCompanyCoupon(coupon.getId());
		couponDAO.removeCustomerCoupon(coupon.getId());
		couponDAO.removeCoupon(coupon);
		}catch(SQLException e){
			throw new SQLException("Failed to remove Coupon. "
								+ "Please consult with your administartor");
		}
	}

	public void updateCoupon(Coupon coupon) throws SQLException  {
		try {
			couponDAO.updateCoupon(coupon);
		} catch (SQLException e) {
			throw new SQLException("Failed to update Coupon."
					+ "Please consult with your administrator");
		}
	}

	public Coupon getCoupon(long id) throws SQLException {
		try {
			return couponDAO.getCoupon(id);
		} catch (SQLException e) {
			throw new SQLException("Failed to retrieve Coupon"
					+ "Please consult with yur administrator.");
		}
	}

	public Collection<Coupon> getAllCoupons() throws SQLException  {
		Collection<Coupon> coupons = new ArrayList<>();
		try {
			coupons = couponDAO.getCompanyCoupons(loggedCompany);
		} catch (SQLException e) {
			throw new SQLException("Failed to retrieve all Coupons. Please consult your"
					+ "administrator.");
			}
		return coupons;
	}

	public Collection<Coupon> getCouponByType(CouponType couponType) throws SQLException  {
		Collection<Coupon> couponsOfType = new ArrayList<>();
		try {
			couponsOfType = couponDAO.getCompanyCoupons(loggedCompany);
		} catch (SQLException e) {
			throw new SQLException("Failed to retrieve Coupons according to type."
					+ "Please consult your administrator.");
		}
		Iterator<Coupon> iter = couponsOfType.iterator();
		while (iter.hasNext()) {
			Coupon coup = iter.next();
			if (!(coup.getType().equals(couponType)))
				iter.remove();
		}
		System.out.println("Purchased coupons of type " + couponType + " retrieved!");
		return couponsOfType;
	}

	@Override
	public CouponClientFacade login(String name, String password) throws Exception {
		if (companyDAO.login(name, password)) {
			CompanyFacade clientFacade = new CompanyFacade();
			clientFacade.setLoggedCompany(companyDAO.getLoginID());
			return clientFacade;
		} else
			throw new Exception("Login FAILED. Please consult with your administrator.");
	}

	@Override
	public String toString() {
		return "company";
	}

}
