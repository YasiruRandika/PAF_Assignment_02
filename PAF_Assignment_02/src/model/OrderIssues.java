package model;

import util.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderIssues {
	/*
	 * Method for open an issue regarding the order
	 * 
	 */
	public String openIssue(int orderId, int productId, String issue) {
		String output = "";
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				return "Error while connecting to the database for updating.";
			}

			// create a prepared statement
			String query = "INSERT INTO OrderIssues(OrderId, ProductId, IssueDescription) VALUES(?, ?, ?)";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			// binding values
			preparedStmt.setInt(1, orderId);
			preparedStmt.setInt(2, productId);
			preparedStmt.setString(3, issue);
			// execute the statement
			preparedStmt.execute();

			con.close();
			output = "Successfully Opned an Issue";
		} catch (Exception e) {
			output = "Error while opening an issue";
			System.err.println(e.getMessage());
		}
		return output;
	}

	/*
	 * Method for changing the status of the issue When an issue is solved or
	 * dismiss it will change the status
	 * 
	 */
	public String changeIssueStatus(int orderId, String status) {
		String output = null;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				return "Error while connecting to the database for updating.";
			}
			// create a prepared statement
			String query = "UPDATE OrderIssues SET status = ? WHERE issueId = ?";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			// binding values
			preparedStmt.setString(1, status);
			preparedStmt.setInt(2, orderId);
			// execute the statement
			preparedStmt.execute();

			con.close();
			output = "Issue Status updated successfully";
		} catch (Exception e) {
			output = "Error while updating data";
			System.err.println(e.getMessage());
		}
		return output;
	}

	/*
	 * Method for deleting an issue
	 * 
	 */
	public String deleteIssue(String issueId) {
		String output = null;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				return "Error while connecting to the database for updating.";
			}

			// Delete the record in issue table
			String query1 = "DELETE FROM orderIssues WHERE issueId = ?";
			PreparedStatement preparedStmt1 = con.prepareStatement(query1);
			// binding values
			preparedStmt1.setInt(1, Integer.parseInt(issueId));
			// execute the statement
			preparedStmt1.execute();

			output = "Issue Deleted Successfully";

			// Close the connection
			con.close();

		} catch (Exception e) {
			output = "Error while deleting order";
			System.err.println(e.getMessage());
		}
		return output;
	}

	/*
	 * Method for getting an issue by its id
	 * 
	 */
	public String getIssueById(int issueId) {
		String output;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				output = "Error while connecting to the database for reading.";
				return output;
			}

			// Prepare the html table to be displayed
			output = "<table border='1'><tr><th>Issue Id</th><th>Order Id</th>" + "<th>Product Id</th>"
					+ "<th>Issue</th>" + "<th>Date</th>" + "<th>Status</th></tr>";

			// SQL Query for selecting the issue
			String query = "select * from orderIssues WHERE issueId = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, issueId);
			ResultSet rs = stmt.executeQuery();
			// iterate through the rows in the result set
			while (rs.next()) {

				String orderId = Integer.toString(rs.getInt("OrderId"));
				String productId = Integer.toString(rs.getInt("productId"));
				String date = rs.getDate("created_at").toString();
				String status = rs.getString("Status");
				String issue = rs.getString("IssueDescription");

				// Add into the html table
				output += "<tr><td>" + issueId + "</td>";
				output += "<td>" + orderId + "</td>";
				output += "<td>" + productId + "</td>";
				output += "<td>" + issue + "</td>";
				output += "<td>" + date + "</td>";
				output += "<td>" + status + "</td></tr>";
			}
			con.close();
			// Complete the html table
			output += "</table>";
		} catch (Exception e) {
			output = "Error while reading the records.";
			System.err.println(e.getMessage());
		}
		return output;
	}

	/*
	 * Method for getting an issue in an order
	 * 
	 */
	public String issuesInOrder(String id) {
		String output;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				output = "Error while connecting to the database for reading.";
				return output;
			}

			// Prepare the html table to be displayed
			output = "<table border='1'><tr><th>Issue Id</th>" + "<th>Product Id</th>" + "<th>Issue</th>"
					+ "<th>Date</th>" + "<th>Status</th></tr>";

			// SQL Query for selecting the issue
			String query = "select * from orderIssues WHERE orderId = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, Integer.parseInt(id));
			ResultSet rs = stmt.executeQuery();
			// iterate through the rows in the result set
			while (rs.next()) {
				String issueId = Integer.toString(rs.getInt("issueId"));
				String productId = Integer.toString(rs.getInt("productId"));
				String date = rs.getDate("created_at").toString();
				String status = rs.getString("Status");
				String issue = rs.getString("IssueDescription");

				// Add into the html table
				output += "<tr><td>" + issueId + "</td>";
				output += "<td>" + productId + "</td>";
				output += "<td>" + issue + "</td>";
				output += "<td>" + date + "</td>";
				output += "<td>" + status + "</td></tr>";
			}
			con.close();
			// Complete the html table
			output += "</table>";
		} catch (Exception e) {
			output = "Error while reading the records.";
			System.err.println(e.getMessage());
		}
		return output;
	}
}
