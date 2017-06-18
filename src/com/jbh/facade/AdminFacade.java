package com.jbh.facade;

import java.util.Collection;

import com.jbh.beans.Company;
import com.jbh.beans.Coupon;
import com.jbh.beans.Customer;
import com.jbh.dao.CompanyDAO;
import com.jbh.dao.CouponDAO;
import com.jbh.dao.CustomerDAO;

public class AdminFacade implements CouponClientFacade {

	private CompanyDAO companyDAO = new CompanyDAO();
	private CouponDAO couponDAO = new CouponDAO();
	private CustomerDAO customerDAO = new CustomerDAO();

	public AdminFacade() {
	}


	public void createCompany(Company company) throws Exception {
		if (companyDAO.getAllCompanies() != null)
			if (companyDAO.getAllCompanies().contains(company)) {
				throw new Exception("Failed to create Company. Adding a Company"
										+ " with an existing name is not allowed.");
			}
		companyDAO.createCompany(company);
	}

	public void removeCompany(Company company) throws Exception {
		try {
			Collection<Coupon> coupons = couponDAO.getCompanyCoupons(company.getId());
			for (Coupon c : coupons) {
				couponDAO.removeCompanyCoupon(c.getId());
				couponDAO.removeCustomerCoupon(c.getId());
				couponDAO.removeCoupon(c);
			}
			companyDAO.removeCompany(company);
		} catch (Exception e) {
			throw new Exception("Failed to remove Company. Please check the cause for this.");
		}
	}

	public void updateCompany(Company company) throws Exception {
		try {
			companyDAO.updateCompany(company);
		} catch (Exception e) {
			throw new Exception("Error encountered while attempting to update company."
					+ "Please check the cause for this.");
		}
	}

	public Company getCompany(long id) throws Exception {
		try {
			return companyDAO.getCompany(id);
		} catch (Exception e) {
			throw new Exception("Error encountered while attempting to retrieve company."
					+ "Please check the cause for this.");
		}
	}

	public Collection<Company> getAllCompanies() throws Exception {
		try {
			return companyDAO.getAllCompanies();
		} catch (Exception e) {
			throw new Exception("Error encountered while attempting to retrieve companies. "
								+ "Please check the cause for this.");
		}
	}

	public void createCustomer(Customer customer) throws Exception {
		if (customerDAO.getAllCustomers() != null)
			if (customerDAO.getAllCustomers().contains(customer)) {
				throw new Exception("Failed to create Customer. Adding a Custoemr with"
									+ " an existing name is not allowed.");
			}
		customerDAO.createCustomer(customer);
	}

	public void removeCustomer(Customer customer) throws Exception {
		try {
			Collection<Coupon> coupons = customerDAO.getCustomerCoupons(customer.getId());
			for (Coupon c : coupons) {
				couponDAO.removeCustomerCoupon(c.getId());
			}
			customerDAO.removeCustomer(customer);
		} catch (Exception e) {
			throw new Exception("Error encountered while attempting to delete customer. "
								+ "Please check the cause for this.");
		}
	}

	public void updateCustomer(Customer customer) throws Exception {
		try {
			customerDAO.updateCustomer(customer);
		} catch (Exception e) {
			throw new Exception("Error encountered while attempting to update customer."
					+ "Please check the cause for this.");
		}
	}

	public Customer getCustomer(long id) throws Exception {
		try {
			return customerDAO.getCustomer(id);
		} catch (Exception e) {
			throw new Exception("Error encountered while attempting to retrieve customer."
					+ "Please check the cause for this.");
		}
	}

	public Collection<Customer> getAllCustomers() throws Exception {
		try {
			return customerDAO.getAllCustomers();
		} catch (Exception e) {
			throw new Exception("Error encountered while attempting to retrieve all customers."
					+ "Please check the cause for this.");
		}
	}

	@Override
	public CouponClientFacade login(String name, String password) throws Exception {
		if (name.equalsIgnoreCase("admin") && password.equals("1234"))
			return new AdminFacade();
		else
			throw new Exception("Login FAILED for admin");
	}

	@Override
	public String toString() {
		return "admin";
	}
}
