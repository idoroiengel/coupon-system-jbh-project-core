package com.jbh.dao;

import java.sql.SQLException;
import java.util.Collection;

import com.jbh.beans.Company;

public interface ICompanyDAO {

	public void createCompany(Company company) throws SQLException;

	public void removeCompany(Company company) throws SQLException;

	public void updateCompany(Company company) throws SQLException;

	public Company getCompany(long id) throws SQLException;

	public Collection<Company> getAllCompanies() throws SQLException;

	public void linkCompanyCoupon(long companyId, long couponId) throws SQLException;

	public void unlinkAllCompanyCoupon(long companyId) throws SQLException;

	public boolean login(String compName, String password) throws SQLException, Exception;

}
