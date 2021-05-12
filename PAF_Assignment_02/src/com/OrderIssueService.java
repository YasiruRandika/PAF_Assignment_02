package com;
/* 
 * @author W.G. YASIRU RANDIKA 
 * IT19131184
 * 
 * */

import model.OrderIssues;

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
public class OrderIssueService {
	OrderIssues orderIssues = new OrderIssues();

	@POST
	@Path("/Issue")
	@RolesAllowed(value = { "Buyer" })
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String openIssue(String issueDetails) {
		// Convert the input string to a JSON object
		JsonObject itemObject = new JsonParser().parse(issueDetails).getAsJsonObject();
		// Read the values from the JSON object
		int orderId = itemObject.get("orderId").getAsInt();
		int productId = itemObject.get("productId").getAsInt();
		String issue = itemObject.get("issue").getAsString();

		String output = orderIssues.openIssue(orderId, productId, issue);
		return output;
	}

	@PUT
	@Path("/Issue")
	@RolesAllowed(value = { "Buyer", "Researcher", "Admin" })
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String changeIssueStatus(String issueDetails) {
		// Convert the input string to a JSON object
		JsonObject itemObject = new JsonParser().parse(issueDetails).getAsJsonObject();
		// Read the values from the JSON object
		int orderId = itemObject.get("issueId").getAsInt();
		String status = itemObject.get("status").getAsString();

		String output = orderIssues.changeIssueStatus(orderId, status);
		return output;
	}

	@DELETE
	@Path("/Issue/{id}")
	@RolesAllowed(value = { "Buyer" })
	@Produces(MediaType.TEXT_PLAIN)
	public String deleteIssue(@PathParam(value = "id") String id) {

		String output = orderIssues.deleteIssue(id);
		return output;
	}


	@GET
	@Path("/Issue")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_HTML)
	@RolesAllowed(value = { "Buyer", "Researcher", "Admin" })
	public String getIssuesforOrder(String data) {
		// Convert the input string to an XML document
		Document doc = Jsoup.parse(data, "", Parser.xmlParser());

		// Read the value from the element <itemID>
		String id = doc.select("OrderId").text();
		String output = orderIssues.issuesInOrder(id);
		return output;
	}
	
	@GET
	@RolesAllowed(value = { "Buyer", "Researcher", "Admin" })
	@Path("/Issue/{issueId}")
	@Produces(MediaType.TEXT_HTML)
	public String getIssueById(@PathParam("issueId") String issueId) {
		return orderIssues.getIssueById(Integer.parseInt(issueId));
	}
}
