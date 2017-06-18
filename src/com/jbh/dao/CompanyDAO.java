package com.jbh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.jbh.beans.Company;
import com.jbh.utils.ConnectionPool;

public class CompanyDAO implements ICompanyDAO {

	private Connection connection;
	private long loginID = 0;

	public long getLoginID() {
		return loginID;
	}

	public void setLoginID(long loginID) {
		this.loginID = loginID;
	}

	@Override
	public void createCompany(Company company) throws SQLException {
		try {
			connection = ConnectionPool.getInstance().getConnection();
			String createSQL = "INSERT INTO COUPON_DB.COMPANY(NAME, PASSWORD, EMAIL)"
								+ " VALUES (?,?,?)";
			PreparedStatement pStatement = connection.prepareStatement(createSQL);
			pStatement.setString(1, company.getName());
			pStatement.setString(2, company.getPassword());
			pStatement.setString(3, company.getEmail());
			pStatement.executeUpdate();
			System.out.println("a new Company was created in the database.");
		} catch (SQLException e) {
			throw new SQLException("Error encountered while attempting to create a new company.");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
	}

	@Override
	public void removeCompany(Company company) throws SQLException {
		try {
			connection = ConnectionPool.getInstance().getConnection();

			unlinkAllCompanyCoupon(company.getId());

			String removeSQL1 = "DELETE FROM COUPON_DB.COMPANY WHERE ID=?";
			PreparedStatement pStatement1 = connection.prepareStatement(removeSQL1);
			pStatement1.setLong(1, company.getId());
			pStatement1.executeUpdate();
			System.out.println("Company " + company.getName() + " ID " + company.getId() 
									+ " was removed from the database.");
		} catch (SQLException e) {
			throw new SQLException("Error encountered while attempting to remove company from "
										+ "the database.");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
	}

	@Override
	public void updateCompany(Company company) throws SQLException {
		try {
			connection = ConnectionPool.getInstance().getConnection();

			String updateSQL = "UPDATE COUPON_DB.COMPANY SET PASSWORD=?, EMAIL=?, "
									+ "NAME=? WHERE ID=?";
			PreparedStatement pStatement = connection.prepareStatement(updateSQL);
			pStatement.setString(1, company.getPassword());
			pStatement.setString(2, company.getEmail());
			pStatement.setString(3, company.getName());
			pStatement.setLong(4, company.getId());
			pStatement.execute();

			System.out.println("Company " + company.getName() + " was updated!");
		} catch (SQLException e) {
			throw new SQLException("Error encountered while attempting to update company.");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
	}

	@Override
	public Company getCompany(long id) throws SQLException {
		Company company = new Company();
		try {
			connection = ConnectionPool.getInstance().getConnection();
			String getSQL = "SELECT * FROM COUPON_DB.COMPANY WHERE ID=?";
			PreparedStatement pStatement = connection.prepareStatement(getSQL);
			pStatement.setLong(1, id);
			ResultSet result = pStatement.executeQuery();
			if (result != null) {
				while (result.next()) {
					company.setName(result.getString("NAME"));
					company.setPassword(result.getString("PASSWORD"));
					company.setEmail(result.getString("EMAIL"));
					company.setId(result.getLong("ID"));
				}
			}
		} catch (SQLException e) {
			throw new SQLException("Error encountered while attempting to retrieve "
									+ "company from the database.");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
		return company;
	}

	@Override
	public Collection<Company> getAllCompanies() throws SQLException {
		Collection<Company> companies = new ArrayList<>();
		
		try {
			connection = ConnectionPool.getInstance().getConnection();
			String getAllSQL = "SELECT * FROM COUPON_DB.COMPANY";
			PreparedStatement pStatement = connection.prepareStatement(getAllSQL);
			ResultSet result = pStatement.executeQuery();

			while (result.next()) {
				Company comp = new Company();
				comp.setId(result.getLong(1));
				comp.setName(result.getString(2));
				comp.setPassword(result.getString(3));
				comp.setEmail(result.getString(4));
				companies.add(comp);
			}
		} catch (SQLException e) {
			throw new SQLException("Error encountered while attempting to retrieve "
									+ "all companies from the database.");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
		return companies;
	}

	@Override
	public void linkCompanyCoupon(long companyId, long couponId) throws SQLException {
		try {
			connection = ConnectionPool.getInstance().getConnection();

			String linkSQL = "INSERT INTO COUPON_DB.COMPANY_COUPON VALUES(?,?)";
			PreparedStatement pStatement = connection.prepareStatement(linkSQL);
			pStatement.setLong(1, companyId);
			pStatement.setLong(2, couponId);
			pStatement.executeUpdate();
			System.out.println("Company " + companyId + " was linked with coupon " + couponId);
		} catch (SQLException e) {
			throw new SQLException("Failed to link Company with the new coupon.");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
	}

	@Override
	public void unlinkAllCompanyCoupon(long companyId) throws SQLException {
		try {
			connection = ConnectionPool.getInstance().getConnection();

			String linkSQL = "DELETE FROM COUPON_DB.COMPANY_COUPON WHERE 'COMPANY_ID'=?";
			PreparedStatement pStatement = connection.prepareStatement(linkSQL);
			pStatement.setLong(1, companyId);
			pStatement.executeUpdate();
			System.out.println("Company " + companyId + " was unlinked from its coupons");

		} catch (SQLException e) {
			throw new SQLException("Failed to unlink Company from its Coupons");
		} finally {
			ConnectionPool.getInstance().returnConnection(connection);
		}
	}
	@Override
	public boolean login(String compName, String password) throws SQLException, Exception {
		String dbPassword = null;
		try {
			connection = ConnectionPool.getInstance().getConnection();

			String loginSQL = "SELECT * FROM COUPON_DB.COMPANY WHERE NAME=?";
			PreparedStatement pStatement = connection.prepareStatement(loginSQL);
			pStatement.setString(1, compName);
			ResultSet result = pStatement.executeQuery();

			while (result.next()) {
				dbPassword = result.getString("PASSWORD");
				if (password.equals(dbPassword)) {
					setLoginID(result.getLong("ID"));
					System.out.println("Login success!");
					return true;
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
