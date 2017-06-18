package com.jbh.tasks;

import java.sql.SQLException;

import com.jbh.dao.CouponDAO;

public class DailyCleaningTask implements Runnable {

	private boolean active = true;
	private CouponDAO couponDAO = new CouponDAO();

	public DailyCleaningTask() {
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public void run() {
		while (active) {
			try {
				couponDAO.removeExpiredCoupon();
				Thread.sleep(60000);
			} catch (SQLException | InterruptedException e) {
				System.out.println("Error encountered while attempting to run daily task");
			}
		}

	}

}
