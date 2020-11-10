package com.paypal.bfs.test.employeeserv.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.bfs.test.employeeserv.api.EmployeeResource;
import com.paypal.bfs.test.employeeserv.api.model.Employee;
import com.paypal.bfs.test.employeeserv.util.H2JDBCUtils;

/**
 * Implementation class for employee resource.
 */
@RestController
public class EmployeeResourceImpl implements EmployeeResource {

	private static final String createTableSQL ="CREATE TABLE EMPLOYEE(ID INT PRIMARY KEY, FIRST_NAME VARCHAR(255),LAST_NAME VARCHAR(255),DOB DATE,LINE1 VARCHAR(300),LINE2 VARCHAR(200),CITY VARCHAR(20),STATE VARCHAR(20),COUNTRY VARCHAR(20),ZIPCODE VARCHAR(30))";
	private static final String InsertSQL = "INSERT INTO EMPLOYEE(ID,FIRST_NAME,LAST_NAME,DOB,LINE1,LINE2,CITY,STATE,COUNTRY,ZIPCODE) values(?,?,?,?,?,?,?,?,?,?)";
	private static final String QUERY = "select * from employee where id =?";
	
	@Override
	public ResponseEntity<String> createTableEmployee() {
		String msg = "Table Creation Failed";
		try (Connection connection = H2JDBCUtils.getConnection();
				// Step 2:Create a statement using connection object
				Statement statement = connection.createStatement();) {

			// Step 3: Execute the query or update query
			statement.execute(createTableSQL);
			msg="Table Created succussfully";

		} catch (SQLException e) {
			// print SQL exception information
			H2JDBCUtils.printSQLException(e);
		}

		return new ResponseEntity<>(msg, HttpStatus.OK);
	}


	@Override
	public ResponseEntity<Employee> employeeGetById(String id) {

		Employee employee = new Employee();
		Employee employee2 = new Employee();

		try (Connection connection = H2JDBCUtils.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(QUERY);) {
			preparedStatement.setInt(1, Integer.parseInt(id));
			System.out.println(preparedStatement);
			// Step 3: Execute the query or update query
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				employee.setId(rs.getInt("id"));
				employee.setFirstName(rs.getString("first_name"));
				employee.setLastName(rs.getString("last_name"));
				employee.setDateOfBirth(rs.getDate("dob"));
				employee2.setAdditionalProperty("linee1", rs.getNString("line1"));
				employee2.setAdditionalProperty("line2", rs.getNString("line2"));
				employee2.setAdditionalProperty("city", rs.getNString("city"));
				employee2.setAdditionalProperty("state", rs.getNString("state"));
				employee2.setAdditionalProperty("country", rs.getNString("country"));
				employee2.setAdditionalProperty("zipcode", rs.getNString("zipcode"));
				employee.setAddress(employee2);

			}

		} catch (SQLException e) {
			// print SQL exception information
			H2JDBCUtils.printSQLException(e);
		}

		return new ResponseEntity<>(employee, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResponseEntity<Employee> createEmployee(Employee employee) {

		LinkedHashMap<String,String> map = (LinkedHashMap<String, String>) employee.getAddress();
		String line1 = null;
		String line2 = null;
		String city = null;
		String state = null;
		String country = null;
		String zipcode = null;

		for (Map.Entry<String,String> entry : map.entrySet())  {

			if(entry.getKey().equals("line1"))
				line1= entry.getValue();
			if(entry.getKey().equals("line2"))
				line2= entry.getValue();
			if(entry.getKey().equals("city"))
				city= entry.getValue();
			if(entry.getKey().equals("state"))
				state= entry.getValue();
			if(entry.getKey().equals("country"))
				country= entry.getValue();
			if(entry.getKey().equals("zipcode"))
				zipcode= entry.getValue();
		}

		System.out.println("id"+employee.getId()+"firstName:"+employee.getFirstName()+
				"LastName:"+employee.getLastName()+"DOB"+employee.getDateOfBirth()+
				"Line1:"+line1+"line2"+line2+"city:"+city+"state:"+state+"country:"
				+country+"zipcode:"+zipcode);

		
		try (Connection connection = H2JDBCUtils.getConnection();
	            // Step 2:Create a statement using connection object
	            PreparedStatement preparedStatement = connection.prepareStatement(InsertSQL)) {
	           
			preparedStatement.setInt(1, employee.getId());  
			preparedStatement.setString(2, employee.getFirstName());
			preparedStatement.setString(3, employee.getLastName());
			Date date=Date.valueOf(employee.getDateOfBirth().toString());
			preparedStatement.setDate(4, date);
			preparedStatement.setString(5, line1);
			preparedStatement.setString(6, line2);
			preparedStatement.setString(7, city);
			preparedStatement.setString(8, state);
			preparedStatement.setString(9, country);
			preparedStatement.setString(10, zipcode);

			preparedStatement.executeUpdate();

		}catch (SQLException e) {

            // print SQL exception information
            H2JDBCUtils.printSQLException(e);
        }

		return new ResponseEntity<>(employee, HttpStatus.OK);
	}
}
