package com.jbh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.jbh.utils.ConnectionPool;
import com.jbh.beans.Coupon;
import com.jbh.beans.Customer;
import com.jbh.dao.CouponDAO;

public class CustomerDAO implements ICustomerDAO {

	private Connection connection;
	private CouponDAO couponDAO = new CouponDAO();
	private long loginID = 0;

	public long getLoginID() {
		return loginID;
	}

	public void setLoginID(long loginID) {
		this.loginID = loginID;
	}

	@Override
	public void createCustomer(Customer customer) throws SQLException {
		try {
			connection = ConnectionPool.getInstance().getConnection();

			String createSQL = "INSERT INTO COUPON_DB.CUSTOMER (NAME,PASSWORD) VALUES (?,?)";
			PreparedStatement pStatement = connection.prepareStatement(createSQL);
			pStatement.setString(1, customer.getName());
			pStatement.setString(2, customer.getPassword());
			pStatement.executeUpdate();

			System.out.println("Customer " + customer.getName() + " was created!");
		} catch (SQLException e) {
			throw new SQLException("Failed to create a new Customer.");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
	}

	@Override
	public void removeCustomer(Customer customer) throws SQLException {
		try {
			connection = ConnectionPool.getInstance().getConnection();
			unlinkAllCustomerCoupon(customer.getId());

			String removeSQL = "DELETE FROM COUPON_DB.CUSTOMER WHERE ID=?";
			PreparedStatement pStatement2 = connection.prepareStatement(removeSQL);
			pStatement2.setLong(1, customer.getId());
			pStatement2.executeUpdate();
			System.out.println("Customer " + customer.getName() + " ID "
								+ customer.getId() + " was deleted!");
		} catch (SQLException e) {
			throw new SQLException("Failed to remove Customer from the database.");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}

	}

	@Override
	public void updateCustomer(Customer customer) throws SQLException {
		try {
			ConnectionPool.getInstance().getConnection();

			String updateSQL = "UPDATE COUPON_DB.CUSTOMER SET NAME=?, PASSWORD=? WHERE ID=?";
			PreparedStatement pStatement = connection.prepareStatement(updateSQL);
			pStatement.setString(1, customer.getName());
			pStatement.setString(2, customer.getPassword());
			pStatement.setLong(3, customer.getId());
			pStatement.execute();
			System.out.println("Customer " + customer.getName() + " was updated!");
			
		} catch (SQLException e) {
			throw new SQLException("Failed to update Customer.");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
	}

	@Override
	public Customer getCustomer(long id) throws SQLException {
		Customer customer = new Customer();
		try {
			connection = ConnectionPool.getInstance().getConnection();
			String getSQL = "SELECT * FROM COUPON_DB.CUSTOMER WHERE ID=?";
			PreparedStatement pStatement = connection.prepareStatement(getSQL);
			pStatement.setLong(1, id);
			ResultSet result = pStatement.executeQuery();
			if (result != null) {
				while (result.next()) {
					customer.setName(result.getString("NAME"));
					customer.setPassword(result.getString("PASSWORD"));
					customer.setId(id);
				}
			System.out.println("Customer " + customer.getName() + " was retrieved!");
			}
		} catch (SQLException e) {
			throw new SQLException("Failed to retrieve Customer.");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
		return customer;
	}

	@Override
	public Collection<Customer> getAllCustomers() throws SQLException {
		Collection<Customer> customers = new ArrayList<>();
		try {
			connection = ConnectionPool.getInstance().getConnection();

			String getAllSQL = "SELECT * FROM COUPON_DB.CUSTOMER";
			PreparedStatement pStatement = connection.prepareStatement(getAllSQL);
			ResultSet result = pStatement.executeQuery();
			if(result != null){
				while (result.next()) {
					Customer customer = new Customer();
					customer.setId(result.getLong(1));
					customer.setName(result.getString(2));
					customer.setPassword(result.getString(3));
					customers.add(customer);
				}
			}
		} catch (SQLException e) {
			throw new SQLException("Failed to retrieve all Customers in the database.");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
		return customers;
	}

	@Override
	public Collection<Coupon> getCustomerCoupons(long custId) throws SQLException {
		Collection<Coupon> CustCoupons = new ArrayList<>();
		try {
			connection = ConnectionPool.getInstance().getConnection();

			String getCouponsSQL = "SELECT COUPON_ID FROM COUPON_DB.CUSTOMER_COUPON "
									+ "WHERE CUSTOMER_ID = ?";
			PreparedStatement pStatement = connection.prepareStatement(getCouponsSQL);
			pStatement.setLong(1, custId);
			ResultSet result = pStatement.executeQuery();
			if(result != null){
				while (result.next()) {
					Coupon coupon = couponDAO.getCoupon(result.getLong(1));
						if(coupon.getId()!=0)
							CustCoupons.add(coupon);
				}
			}
		} catch (SQLException e) {
			throw new SQLException("Failed to retrieve Customer's Coupons.");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
		return CustCoupons;
	}

	@Override
	public void linkCustomerCoupon(long customerId, long couponId) throws SQLException {
		try {
			connection = ConnectionPool.getInstance().getConnection();
			String linkSQL = "INSERT INTO COUPON_DB.Customer_Coupon (CUSTOMER_ID , COUPON_ID)"
								+ " values (?,?)";
			PreparedStatement pStatement = connection.prepareStatement(linkSQL);
			pStatement.setLong(1, customerId);
			pStatement.setLong(2, couponId);
			pStatement.executeUpdate();
			System.out.println("Customer " + customerId + " was linked with coupon " + couponId);
		} catch (SQLException e) {
			throw new SQLException("Failed to link Customer and Coupon.");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
	}

	@Override
	public void unlinkAllCustomerCoupon(long customerId) throws SQLException {
		try {
			connection = ConnectionPool.getInstance().getConnection();

			String linkSQL = "DELETE FROM COUPON_DB.CUSTOMER_COUPON WHERE 'CUSTOMER_ID'=?";
			PreparedStatement pStatement = connection.prepareStatement(linkSQL);
			pStatement.setLong(1, customerId);
			pStatement.executeUpdate();
			System.out.println("Customer " + customerId + " was unlinked from all coupons");
		} catch (SQLException e) {
			throw new SQLException("Failed to unlink Customer from its Coupons.");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
	}

	@Override
	public boolean login(String custName, String password) throws SQLException {
		String dbPassword = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();
			String loginSQL = "SELECT * FROM COUPON_DB.CUSTOMER WHERE NAME=?";
			PreparedStatement pStatement = connection.prepareStatement(loginSQL);
			pStatement.setString(1, custName);
			ResultSet result = pStatement.executeQuery();
			if(result != null){
				while (result.next()) {
					dbPassword = result.getString("PASSWORD");
					if (password.equals(dbPassword)) {
						setLoginID(result.getLong("ID"));
						return true;
					}
				}
			}
		} catch (SQLException e) {
			throw new SQLException("Login FAILED !");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
		return false;
	}

}
