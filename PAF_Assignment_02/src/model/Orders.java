package model;
/* 
 * @author W.G. YASIRU RANDIKA 
 * IT19131184
 * 
 * */

import java.sql.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import util.ConnectDB;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.WebResource;

public class Orders {
	/*
	 * Read all records in the orders table
	 * 
	 */
	public String getAllOrders() {
		String output = "";
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				output = "Error while connecting to the database for reading.";
				return output;
			}
			
			OrderDetails orderDetails = new OrderDetails();

			// SQL Query for selecting all orders
			String query = "select * from orders";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			// iterate through the rows in the result set
			while (rs.next()) {

				String orderId = Integer.toString(rs.getInt("OrderId"));
				String buyerId = Integer.toString(rs.getInt("BuyerId"));
				String date = rs.getDate("created_at").toString();
				String status = rs.getString("Status");
				String address = rs.getString("ShippingAddress");
				String totalAmount = Double.toString(rs.getDouble("TotalAmount"));

				output += "<div class=\"card\" style=\"width: 32rem;\">\r\n"
						+ "						<div class=\"card-body\">\r\n"
						+ "							<div class=\"row\">\r\n"
						+ "								<div class=\"col\">\r\n"
						+ "									<h6 class=\"card-title\">Order ID :" + orderId + "</h6>\r\n"
						+ "									<h6 class=\"card-subtitle mb-2 text-muted\">" + date
						+ "</h6>\r\n" + "								</div>\r\n"
						+ "								<div class=\"col\">\r\n"
						+ "									<span class=\"badge bg-success\">" + totalAmount
						+ "</span> <span\r\n"
						+ "										class=\"badge bg-primary\">"+ status+ "</span>\r\n"
						+ "								</div>\r\n"
						+ "								<div class=\"col\">\r\n"
						+ "									<button type=\"button\" style=\"float: right; margin-left: 10px\"\r\n"
						+ "										id=\"btnCancelOrder\" class=\"btn btn-outline-danger btn-sm\">\r\n"
						+ "										<i class=\"bi bi-trash\"></i>\r\n"
						+ "									</button>\r\n"
						+ "									<button type=\"button\" style=\"float: right\" id=\"btnEditOrder\"\r\n"
						+ "										class=\"btn btn-outline-warning btn-sm\">\r\n"
						+ "										<i class=\"bi bi-pencil\"></i>\r\n"
						+ "									</button>\r\n" + "								</div>\r\n"
						+ "							</div>\r\n"
						+ "							<div style=\"color: graytext;\">\r\n"
						+ "								<h8>Buyer Id :" + buyerId + "</h8>\r\n"
						+ "								<br>\r\n"
						+ "								<h8>Shipping Address : " + address + "</h8>\r\n"
						+ "							</div>\r\n"
						+ "							<br><table class=\"table table-sm\" id=\"tableProductsDisplay\">\r\n"
						+ "								<thead>\r\n" + "									<tr>\r\n"
						+ "										<th scope=\"col\">Product Id</th>\r\n"
						+ "										<th scope=\"col\">Quantity</th>\r\n"
						+ "										<th scope=\"col\">Status</th>\r\n"
						+ "									</tr>\r\n" + "								</thead>\r\n"
						+ "								<tbody>";
				
				String qu = "select * from OrderDetails WHERE OrderId = ?";
				PreparedStatement st = con.prepareStatement(qu);
				st.setString(1, orderId);
				ResultSet res = st.executeQuery();
				
				while (res.next()) {
					String productId = res.getString("ProductId");
					String quantity = res.getString("Quantity");
					String sta = res.getString("Status");
					
					output += "<tr>\r\n" + 
							"										<td>" + productId + "</td>\r\n" + 
							"										<td>" + quantity + "</td>\r\n" + 
							"										<td>" + sta +"</td>\r\n" + 
							"									</tr>";
 				}
				output += "</tbody>\r\n" + "							</table>\r\n" + "\r\n"
						+ "						</div>\r\n" + "					</div>";
			
			}
			con.close();
			// Complete the html table
		} catch (Exception e) {
			output = "Error while reading the records.";
			System.err.println(e.getMessage());
		}
		return output;
	}

	/*
	 * Add order to the database
	 * 
	 */
	public String addOrder(int buyerId, String shippingAddress, JsonArray orders) {
		String output = "";
		int orderId;
		double total = 0;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				return "Error while connecting to the database for updating.";
			}

			String sAdr = getShippingAddress(shippingAddress, String.valueOf(buyerId));

			// create a prepared statement
			String query = "INSERT INTO Orders(BuyerId, ShippingAddress) VALUES(?, ?)";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			// binding values
			preparedStmt.setInt(1, buyerId);
			preparedStmt.setString(2, sAdr);
			// execute the statement
			preparedStmt.execute();

			String queryOrderId = "SELECT OrderId FROM orders WHERE BuyerId = ? ORDER BY OrderId DESC LIMIT 1";
			PreparedStatement preparedStmt1 = con.prepareStatement(queryOrderId);
			// binding values
			preparedStmt1.setInt(1, buyerId);
			// execute the statement
			ResultSet resultSet = preparedStmt1.executeQuery();

			if (resultSet.next()) {
				orderId = resultSet.getInt(1);
			} else {
				return "Error whiling processing";
			}

			OrderDetails orderDetails = new OrderDetails();

			total = orderDetails.addProductsInOrder(orderId, orders);

			if (total == -1) {
				return "Error while processing";
			}

			// Update the orders table with total price of the order
			String queryTP = "UPDATE `orders` SET `TotalAmount`= ? WHERE OrderId = ?";
			PreparedStatement preparedStmt3 = con.prepareStatement(queryTP);
			// binding values
			preparedStmt3.setInt(2, orderId);
			preparedStmt3.setDouble(1, total);
			// execute the statement
			preparedStmt3.execute();

			con.close();
			output = "{\"id\" : " + orderId + ",\"total\" : " + total + "}";
		} catch (Exception e) {
			output = "Error while inserting data";
			System.err.println(e.getMessage());
		}
		return output;
	}

	/*
	 * Get the shipping address from for the order If the user has mentioned same
	 * then the shipping address is taken from User Management via API call Else use
	 * the address provided by the user at the order placement
	 * 
	 */
	private String getShippingAddress(String shippingAddress, String buyerId) {
		String sAdr;
		if (shippingAddress.equals("Same Address")) {
			ClientConfig clientC = new ClientConfig();

			Client client = ClientBuilder.newClient(clientC);

			Response response = client.target("http://localhost:8080/UserManagement/UserService/buyer/get-address")
					.queryParam("id", buyerId).request().get();

			sAdr = response.readEntity(String.class);
		} else {
			sAdr = shippingAddress;
		}
		return sAdr;
	}

	/*
	 * Delete a order When deleting the order it will check the stage of the order
	 * According to the status of the order it will decide whether delete(cancel)
	 * the order or not
	 * 
	 * If we can delete the order, it will delete all the records related to that
	 * order
	 */
	public String deleteOrder(String orderId) {
		String output = null;
		String status = null;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				return "Error while connecting to the database for updating.";
			}
			// Get the status of the order
			String query = "SELECT `status` from orders where orderId = ?";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			// binding values
			preparedStmt.setInt(1, Integer.parseInt(orderId));
			// execute the statement
			ResultSet rs = preparedStmt.executeQuery();

			if (rs.next()) {
				status = rs.getString(1);
			} else {
				return "Something went wrong";
			}

			if (status.equals("SHIPPED ALL") || status.equals("SHIPPED SOME ITEMS")) {
				output = "Your order has been shipped therefore you cannot delete the order now";
				return output;
			}

			// Delete the record in order table
			String query1 = "DELETE FROM orders WHERE orderId = ?";
			PreparedStatement preparedStmt1 = con.prepareStatement(query1);
			// binding values
			preparedStmt1.setInt(1, Integer.parseInt(orderId));
			// execute the statement
			preparedStmt1.execute();

			output = "Order Deleted Successfully";

			// Close the connection
			con.close();

		} catch (Exception e) {
			output = "Error while deleting order";
			System.err.println(e.getMessage());
		}
		return output;
	}

	/*
	 * Method for updating the order details If the orders have already shipped it
	 * will not allowed to update the details of the order
	 * 
	 */
	public String updateOrder(int orderId, int buyerId, String shippingAddress, JsonArray orders) {
		String output = "";
		double total = 0;
		String orStatus;
		String sAdr;
		String currentSA;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				return "Error while connecting to the database for updating.";
			}

			// Get the status and shipping address of the order
			String query = "SELECT `status`, shippingAddress from orders where orderId = ?";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			// binding values
			preparedStmt.setInt(1, orderId);
			// execute the statement
			ResultSet rs = preparedStmt.executeQuery();

			// Get the shipping address
			sAdr = "";
			// getShippingAddress(shippingAddress, String.valueOf(buyerId));

			if (rs.next()) {
				orStatus = rs.getString(1);
				currentSA = rs.getString(2);
			} else {
				return "Something went wrong";
			}

			if ((orStatus.equals("SHIPPED ALL") || orStatus.equals("SHIPPED SOME ITEMS")) && !currentSA.equals(sAdr)) {
				output = "<h6>Your order has been shipped therefore you cannot change the shipping address<h6>";
				sAdr = currentSA;
			}

			// Update order details in order detail table
			OrderDetails orderDetails = new OrderDetails();
			String res = orderDetails.updateProductsInOrder(orderId, orders);

			System.out.println(res);

			JsonObject data = new JsonParser().parse(res).getAsJsonObject();

			System.out.println(data);

			String msg = data.get("msg").getAsString();

			if (msg.equals("-1")) {
				return "Error occured while processing";
			} else {
				output += msg;
			}

			total = Double.parseDouble(data.get("total").getAsString());

			// Update the orders table with total price of the order
			String queryTP = "UPDATE `orders` SET `TotalAmount`= ?, shippingAddress = ? WHERE OrderId = ?";
			PreparedStatement preparedStmt3 = con.prepareStatement(queryTP);
			// binding values
			preparedStmt3.setInt(3, orderId);
			preparedStmt3.setDouble(1, total);
			preparedStmt3.setString(2, sAdr);
			// execute the statement
			preparedStmt3.execute();

			con.close();
			output += "<h5>Update operation successfully executed. Check whehter some errors in the description<h5>";
		} catch (Exception e) {
			output = "Error while updating data";
			System.err.println(e.getMessage());
		}
		return output;
	}

	/*
	 * Method for add the payment for the order
	 * 
	 */
	public String addPayment(String orderId, String paymentSlip) {
		String output = null;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				return "Error while connecting to the database for updating.";
			}
			// create a prepared statement
			String query = "UPDATE Orders SET PaymentSlipUrl = ?, Status = 'Processing', paymentAccepted = 'Pending' WHERE OrderId = ?";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			// binding values
			preparedStmt.setString(1, paymentSlip);
			preparedStmt.setInt(2, Integer.parseInt(orderId));
			// execute the statement
			preparedStmt.execute();

			con.close();
			output = "Payment Slip Successfully added to the order. Wait till get accept the payment by GB Online.";
		} catch (Exception e) {
			output = "Error while inserting data";
			System.err.println(e.getMessage());
		}
		return output;
	}

	/*
	 * Method for accepting the payment
	 * 
	 */
	public String acceptPayment(int orderId) {
		String output = null;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				return "Error while connecting to the database for updating.";
			}
			// create a prepared statement
			String query = "UPDATE Orders SET paymentAccepted = 'YES' WHERE OrderId = ?";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			// binding values
			preparedStmt.setInt(1, orderId);
			// execute the statement
			preparedStmt.execute();

			con.close();
			output = "Payment Accepted Successfully.";
		} catch (Exception e) {
			output = "Error while accepting the payment.";
			System.err.println(e.getMessage());
		}
		return output;
	}

	/*
	 * Method for rejecting the payment
	 * 
	 */
	public String rejectPayment(int orderId) {
		String output = null;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				return "Error while connecting to the database for updating.";
			}
			// create a prepared statement
			String query = "UPDATE Orders SET paymentAccepted = 'NO', status = 'Not Paid' WHERE OrderId = ?";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			// binding values
			preparedStmt.setInt(1, orderId);
			// execute the statement
			preparedStmt.execute();

			con.close();
			output = "Payment Rejected Successfully and the Buyer will get notify to add the payment again.";
		} catch (Exception e) {
			output = "Error while rejecting the payment.";
			System.err.println(e.getMessage());
		}
		return output;
	}

	/*
	 * Method for updating the status of the order table
	 */
	public String updateOrderStatus(String status, int orderId) {
		String output = null;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				return "Error while connecting to the database for updating.";
			}

			// Update status in the order table
			// create a prepared statement
			String query2 = "UPDATE Orders SET status = ? WHERE OrderId = ?";
			PreparedStatement preparedStmt2 = con.prepareStatement(query2);
			// binding values
			preparedStmt2.setString(1, status);
			preparedStmt2.setInt(2, orderId);
			// execute the statement
			preparedStmt2.execute();

			output = "1";

		} catch (Exception e) {
			output = "Error while updating data";
			System.err.println(e.getMessage());
		}
		return output;
	}

	/*
	 * Method for get order details by orderId
	 * 
	 */
	public String getOrderById(int orderId) {
		String output = "";
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				output = "Error while connecting to the database for reading.";
				return output;
			}

			// SQL Query
			String query = "select * from orders where orderId = ?";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1, orderId);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				output += "<h5>Order Details</h5><ul><li>Order Id : " + orderId + "</li><li>Date : "
						+ rs.getDate("created_at").toString() + "</li><li>Status : " + rs.getString("Status")
						+ "</li><li>Payment Accepted : " + rs.getString("paymentAccepted")
						+ "</li><li>Shipping Address : " + rs.getString("ShippingAddress") + "</li><li>Total Amount : "
						+ rs.getString("TotalAmount") + "</li></ul><br><br>";
			}

			// Prepare the html table to be displayed
			output += "<table border='1'><tr><th>Product Id</th><th>Unit Price</th><th>Quantity</th><th>Status</th><th>Shipped Date</th><th>Shipping Company</th><th>Shipped Track Id</th></tr>";

			// SQL Query for selecting all orders
			String query1 = "select * from orderdetails where orderId = ?";
			PreparedStatement preparedStatement1 = con.prepareStatement(query1);
			preparedStatement1.setInt(1, orderId);

			ResultSet rs1 = preparedStatement1.executeQuery();

			// iterate through the rows in the result set
			while (rs1.next()) {
				int productId = rs1.getInt("ProductId");
				String sDate = "";
				try {
					sDate = rs1.getDate("ShippingDate").toString();
				} catch (Exception e) {
				}

				String status = rs1.getString("Status");
				String unitPrice = Double.toString(rs1.getDouble("UnitPrice"));
				String sCompany = rs1.getString("ShippingCompany");
				String ShipingTrackId = rs1.getString("ShipingTrackId");
				int quantity = rs1.getInt("Quantity");

				// Add into the html table
				output += "<tr><td>" + productId + "</td>";
				output += "<td>" + unitPrice + "</td>";
				output += "<td>" + quantity + "</td>";
				output += "<td>" + status + "</td>";
				output += "<td>" + sDate + "</td>";
				output += "<td>" + sCompany + "</td>";
				output += "<td>" + ShipingTrackId + "</td></tr>";
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
	 * Method for get order details by buyerId
	 * 
	 */
	public String getOrdersByBuyer(String buyerId) {
		String output;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				output = "Error while connecting to the database for reading.";
				return output;
			}

			// Prepare the html table to be displayed
			output = "<table border='1'><tr><th>Order Id</th>" + "<th>Status</th>" + "<th>Status</th>"
					+ "<th>Total Amount</th>";

			// SQL Query for selecting all orders
			String query = "select * from orders where BuyerId = ?";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1, Integer.parseInt(buyerId));

			ResultSet rs = preparedStatement.executeQuery();
			// iterate through the rows in the result set
			while (rs.next()) {
				String orderId = rs.getString("orderId");
				String date = rs.getDate("created_at").toString();
				String status = rs.getString("Status");
				String totalAmount = Double.toString(rs.getDouble("TotalAmount"));

				// Add into the html table
				output += "<tr><td>" + orderId + "</td>";
				output += "<td>" + date + "</td>";
				output += "<td>" + status + "</td>";
				output += "<td>" + totalAmount + "</td></tr>";
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
	 * Method for get order details by seller ID
	 * 
	 */
	public String getOrdersBySeller(String sellerId) {
		String output;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				output = "Error while connecting to the database for reading.";
				return output;
			}

			// Prepare the html table to be displayed
			output = "<table border='1'><tr><th>Order Id</th>" + "<th>Product Id</th>" + "<th>Ordered Date</th>"
					+ "<th>Buyer Id</th><th>Unit Price</th><th>Quantity</th><th>Is Payment Accepted</th><th>Status</th><th>Shipping Address</th><th>Shipped Date</th><th>Shipping Company</th><th>Shipped Track Id</th>";

			// SQL Query for selecting all orders
			String query = "select * from orderDetails where sellerId = ?";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1, Integer.parseInt(sellerId));

			ResultSet rs = preparedStatement.executeQuery();
			// iterate through the rows in the result set
			while (rs.next()) {
				String orderId = rs.getString("orderId");
				int productId = rs.getInt("ProductId");
				String sDate = "";
				try {
					sDate = rs.getDate("ShippingDate").toString();
				} catch (Exception e) {
				}

				String itemStatus = rs.getString("Status");
				String unitPrice = Double.toString(rs.getDouble("UnitPrice"));
				String sCompany = rs.getString("ShippingCompany");
				String ShipingTrackId = rs.getString("ShipingTrackId");
				int quantity = rs.getInt("Quantity");

				String oDate = null, sAdr = null, isPaymentAcc = null, bId = null;

				String query1 = "select * from orders where orderId = ?";
				PreparedStatement preparedStatement1 = con.prepareStatement(query1);
				preparedStatement1.setInt(1, Integer.parseInt(orderId));
				ResultSet resultSet = preparedStatement1.executeQuery();

				resultSet.next();

				bId = resultSet.getString(2);
				oDate = resultSet.getString(3);
				sAdr = resultSet.getString(4);
				isPaymentAcc = resultSet.getString(6);

				// Add into the html table
				output += "<tr><td>" + orderId + "</td>";
				output += "<td>" + productId + "</td>";
				output += "<td>" + oDate + "</td>";
				output += "<td>" + bId + "</td>";
				output += "<td>" + unitPrice + "</td>";
				output += "<td>" + quantity + "</td>";
				output += "<td>" + isPaymentAcc + "</td>";
				output += "<td>" + itemStatus + "</td>";
				output += "<td>" + sAdr + "</td>";
				output += "<td>" + sDate + "</td>";
				output += "<td>" + sCompany + "</td>";
				output += "<td>" + ShipingTrackId + "</td></tr>";
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