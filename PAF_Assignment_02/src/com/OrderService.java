package com;
/* 
 * @author W.G. YASIRU RANDIKA 
 * IT19131184
 * 
 * */

import model.Orders;

import javax.annotation.security.RolesAllowed;
//For REST Service
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

//For JSON
import com.google.gson.*;

@Path("/Orders")
public class OrderService {
	Orders orderModel = new Orders();
	
	@GET
	@Path("/{orderId}")
	@RolesAllowed({"Buyer","Admin", "Researcher"})
	@Produces(MediaType.TEXT_HTML)
	public String getOrder(@PathParam("orderId") String orderId) {
		return orderModel.getOrderById(Integer.parseInt(orderId));
	}
	
	@GET
	@Path("/Buyers/{buyerId}")
	@RolesAllowed(value = { "Buyer", "Admin"})
	@Produces(MediaType.TEXT_HTML)
	public String getOrdersByBuyer(@PathParam("buyerId") String buyerId) {
		return orderModel.getOrdersByBuyer(buyerId);
	}
	
	@GET
	@Path("/Sellers/{sellerId}")
	@RolesAllowed(value = { "Researcher", "Admin" })
	@Produces(MediaType.TEXT_HTML)
	public String getOrdersBySeller(@PathParam("sellerId") String sellerId) {
		return orderModel.getOrdersBySeller(sellerId);
	}
	
	@GET
	@Path("/")
	@RolesAllowed(value = { "Admin" })
	@Produces(MediaType.TEXT_HTML)
	public String getOrders() {
		return orderModel.getAllOrders();
	}
	
	@POST
	@Path("/")
	@RolesAllowed(value = { "Buyer"})
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public String addOrder(String itemData) {
		// Convert the input string to a JSON object
		JsonObject itemObject = new JsonParser().parse(itemData).getAsJsonObject();
		// Read the values from the JSON object
		String buyerId = itemObject.get("buyerId").getAsString();
		String shippingAddress = itemObject.get("shippingAddress").getAsString();
		JsonArray orderDetails = itemObject.get("orderDetails").getAsJsonArray();
		
		String output = orderModel.addOrder(Integer.parseInt(buyerId), shippingAddress, orderDetails);
		return output;
	}
	
	@DELETE
	@Path("/{id}")
	@RolesAllowed(value = { "Buyer" })
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	public String deleteOrder(@PathParam("id") String id) {
		// Convert the input string to an XML document
		//Document doc = Jsoup.parse(itemData, "", Parser.xmlParser());

		// Read the value from the element <itemID>
		//String orderId = doc.select("id").text();
		String output = orderModel.deleteOrder(id);
		return output;
	}
	
	@PUT
	@Path("/")
	@RolesAllowed(value = { "Buyer", "Admin" })
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public String updateOrder(String itemData) {
		// Convert the input string to a JSON object
		JsonObject itemObject = new JsonParser().parse(itemData).getAsJsonObject();
		// Read the values from the JSON object
		String orderId =  itemObject.get("orderId").getAsString();
		String buyerId =  itemObject.get("buyerId").getAsString();
		String shippingAddress = itemObject.get("shippingAddress").getAsString();
		JsonArray orderDetails = itemObject.get("orderDetails").getAsJsonArray();
		
		String output = orderModel.updateOrder(Integer.parseInt(orderId), Integer.parseInt(buyerId), shippingAddress, orderDetails);
		return output;
	}
	
	
	@PUT
	@Path("/addPayment")
	@RolesAllowed(value = { "Buyer"})
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String addPayment(@FormParam("orderId") String orderId, @FormParam("paymentSlipUrl") String paymentSlip) {
		String output = orderModel.addPayment(orderId, paymentSlip);
		return output;
	}
	
	@PUT
	@Path("/acceptPayment")
	@RolesAllowed(value = { "Admin" })
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String acceptPayment(@QueryParam("orderId") int OrderId) {
		String output = orderModel.acceptPayment(OrderId);
		return output;
	}
	
	@PUT
	@Path("/rejectPayment")
	@RolesAllowed(value = { "Admin" })
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String rejectPayment(@QueryParam("orderId") int OrderId) {
		String output = orderModel.rejectPayment(OrderId);
		return output;
	}
}

