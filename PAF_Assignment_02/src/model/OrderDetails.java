package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import util.ConnectDB;

/* 
 * @author W.G. YASIRU RANDIKA 
 * IT19131184
 * 
 * */

public class OrderDetails {

	/*
	 * Method for adding the products in the order to the database
	 * 
	 */
	public double addProductsInOrder(int OrderId, JsonArray orders) {
		double total = 0;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				return -1;
			}

			// Get the items in the order and add them to the Order details table
			for (int i = 0; i < orders.size(); i++) {
				JsonObject ord = orders.get(i).getAsJsonObject();
				String productId = ord.get("productId").getAsString();
				int quantity = ord.get("quantity").getAsInt();

				// Get the unit price of the product from Product Micro Service
				ClientConfig clientC = new ClientConfig();

				Client client = ClientBuilder.newClient(clientC);

				Response response = client.target("http://localhost:8080/ProductManagement/ProductService/product/read")
						.queryParam("productID", productId).request().get();

				String res = response.readEntity(String.class);
				
				JsonObject itemObject = new JsonParser().parse(res).getAsJsonObject();
				// Read the values from the JSON object
				String unitPrice = itemObject.get("unitPrice").getAsString();
				String sellerId = itemObject.get("sellerId").getAsString();

				// Calculate the amount
				double amount = quantity * Double.parseDouble(unitPrice);

				// Add the amount to the total price
				total += amount;

				String queryOD = "INSERT INTO `orderdetails`(`OrderId`, `ProductId`, `Quantity`, `UnitPrice`, `sellerId`) VALUES (? , ? , ? , ?,?)";
				PreparedStatement preparedStmt2 = con.prepareStatement(queryOD);
				// binding values
				preparedStmt2.setInt(1, OrderId);
				preparedStmt2.setInt(2, Integer.parseInt(productId));
				preparedStmt2.setInt(3, quantity);
				preparedStmt2.setDouble(4, amount);
				preparedStmt2.setInt(5, Integer.parseInt(sellerId));
				// execute the statement
				preparedStmt2.execute();
			}
		} catch (Exception e) {
			return -1;
		}

		return total;
	}

	/*
	 * Method for updating the products in the order to the database
	 * 
	 */
	public String updateProductsInOrder(int OrderId, JsonArray orders) {
		String output = "";
		double total = 0;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				return "-1";
			}

			// Get the items in the order and update them to the Order details table
			for (int i = 0; i < orders.size(); i++) {
				String pStatus;
				JsonObject ord = orders.get(i).getAsJsonObject();
				String productId = ord.get("productId").getAsString();
				int quantity = ord.get("quantity").getAsInt();
				int qty;
				double unitPrice;

				// Get the status and quantity of the product in the order
				String query2 = "SELECT `status`, quantity, unitPrice from orderDetails where orderId = ? and productId = ?";
				PreparedStatement preparedStmt2 = con.prepareStatement(query2);
				// binding values
				preparedStmt2.setInt(1, OrderId);
				preparedStmt2.setInt(2, Integer.parseInt(productId));
				// execute the statement
				ResultSet rs2 = preparedStmt2.executeQuery();
				

				if (rs2.next()) {
					pStatus = rs2.getString(1);
					qty = rs2.getInt(2);
					unitPrice = rs2.getDouble(3);
				} else {
					return "{'total' : " + total + ", 'msg' : " + "-1" + "}";
				}

				// If quantity has not been changed no need to update
				if (qty == quantity) {
					total += qty * unitPrice;
					break;
				}

				if (pStatus.equals("Shipped")) {
					output += "<h6>Your product " + productId
							+ " has been shipped therefore you cannot change the quantity now</h6>";
					break;
				}

				// Calculate the amount
				double amount = quantity * unitPrice;

				// Add the amount to the total price
				total += amount;

				String query3 = "UPDATE `orderdetails` SET `Quantity` = ?";
				PreparedStatement preparedStmt3 = con.prepareStatement(query3);
				// binding values
				preparedStmt3.setInt(1, quantity);
				// execute the statement
				preparedStmt3.execute();
			}
			con.close();
			output += "Executed Successully";
		} catch (Exception e) {
			return "{'total' : " + total + ", 'msg' : " + "-1" + "}";
		}

		return "{'total' : " + total + ", 'msg' : '" + output + "'}";
	}
	
	/*
	 * Method for adding shipping details the payment
	 * 
	 */
	public String addShipping(int orderId, int productId, String date, String shippingCompany, String trackId) {
		String output = "";
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				return "Error while connecting to the database for updating.";
			}

			// Convert String date to Date type
			java.util.Date dateD = new SimpleDateFormat("dd/MM/yyyy").parse(date);

			// create a prepared statement
			String query = "UPDATE OrderDetails SET status = 'Shipped', ShippingDate = ?, ShippingCompany = ?, ShipingTrackId = ? WHERE OrderId = ? AND ProductId = ?";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			// binding values
			preparedStmt.setDate(1, new Date(dateD.getTime()));
			preparedStmt.setString(2, shippingCompany);
			preparedStmt.setString(3, trackId);
			preparedStmt.setInt(4, orderId);
			preparedStmt.setInt(5, productId);
			// execute the statement
			preparedStmt.execute();

			con.close();
			output = "Shipping Details added successfully";
		} catch (Exception e) {
			output = "Error while inserting data";
			System.err.println(e.getMessage());
		}
		return output;
	}
	
	

	/*
	 * Method for confirming an item in the order First it will update the status of
	 * the item Then it will check the status of the other items in the order Then
	 * update the status of the order also
	 */
	public String confirmOrder(int orderId, int productId) {
		String output = null;
		try {
			Connection con = ConnectDB.connect();
			if (con == null) {
				return "Error while connecting to the database for updating.";
			}
			// create a prepared statement
			String query = "UPDATE OrderDetails SET status = 'Received' WHERE OrderId = ? AND ProductId = ?";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			// binding values
			preparedStmt.setInt(1, orderId);
			preparedStmt.setInt(2, productId);
			// execute the statement
			preparedStmt.execute();

			// Check the status of other products in the order
			// create a prepared statement
			String query1 = "SELECT status FROM OrderDetails WHERE OrderId = ? AND ProductId = ?";
			PreparedStatement preparedStmt1 = con.prepareStatement(query1);
			// binding values
			preparedStmt1.setInt(1, orderId);
			preparedStmt1.setInt(2, productId);
			// execute the statement
			ResultSet rs = preparedStmt1.executeQuery();

			String orderStatus = "Received All";
			while (rs.next()) {
				if (!rs.getString(1).equals("Received")) {
					orderStatus = "Received Some";
					break;
				}
			}

			Orders orders = new Orders();
			String outp = orders.updateOrderStatus(orderStatus, orderId);
			
			if (!outp.equals("1")) {
				return outp;
			}

			con.close();
			output = "Order Status updated successfully";
		} catch (Exception e) {
			output = "Error while updating data";
			System.err.println(e.getMessage());
		}
		return output;
	}
}
