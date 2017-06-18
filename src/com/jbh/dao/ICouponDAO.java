package com.jbh.dao;

import java.sql.SQLException;
import java.util.Collection;

import com.jbh.beans.Coupon;
import com.jbh.enums.CouponType;

public interface ICouponDAO {

	public void createCoupon(Coupon coupon) throws SQLException;

	public void removeCoupon(Coupon coupon) throws SQLException;

	public void updateCoupon(Coupon coupon) throws SQLException;

	public Coupon getCoupon(long id) throws SQLException;

	public Collection<Coupon> getAllCoupons() throws SQLException;

	public Collection<Coupon> getCompanyCoupons(long companyId) throws SQLException;

	public Collection<Coupon> getCouponByType(CouponType couponType) throws SQLException;

	void removeCompanyCoupon(long couponId) throws SQLException;

	void removeCustomerCoupon(long couponId) throws SQLException;

	public void removeExpiredCoupon() throws SQLException;

}
