package com;
/* 
 * @author W.G. YASIRU RANDIKA 
 * IT19131184
 * 
 * */

import model.OrderDetails;
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
public class OrderDetailsService {
	OrderDetails orderDetails = new OrderDetails();
	
	@PUT
	@Path("/addShipping")
	@RolesAllowed(value = { "Researcher" })
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String addShipping(@FormParam("orderId") int OrderId, @FormParam("productId") int productId, @FormParam("date") String date, @FormParam("shippingCompany") String shippingCompany, @FormParam("trackId") String trackId) {
		String output = orderDetails.addShipping(OrderId, productId, date, shippingCompany, trackId);
		return output;
	}
	
	@PUT
	@Path("/confirmOrder")
	@RolesAllowed(value = { "Buyer"})
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String confirmOrder(@FormParam("orderId") int OrderId, @FormParam("productId") int productId) {
		String output = orderDetails.confirmOrder(OrderId, productId);
		return output;
	}
}

