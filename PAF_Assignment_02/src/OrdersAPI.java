

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import model.Orders;

/**
 * Servlet implementation class OrdersAPI
 */
@WebServlet("/OrdersAPI")
public class OrdersAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Orders orders = new Orders();

    /**
     * Default constructor. 
     */
    public OrdersAPI() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuffer jb = new StringBuffer();
		  String line = null;
		  JsonObject jsonObject;
		  try {
		    BufferedReader reader = request.getReader();
		    while ((line = reader.readLine()) != null)
		      jb.append(line);
		  } catch (Exception e) { /*report an error*/ }

		  try {
			  jsonObject = new JsonParser().parse(jb.toString()).getAsJsonObject();
		  } catch (JSONException e) {
		    // crash and burn
		    throw new IOException("Error parsing JSON request string");
		  }
		  
		  System.out.println(jsonObject);
		//JsonObject itemObject = new JsonParser().parse(getBody(request)).getAsJsonObject();
		//JSONObject itemObject = new JSONObject(request.getCharacterEncoding());
		String buyerId = jsonObject.get("buyerId").toString();
		String shippingAddress = jsonObject.get("address").toString();
		JsonArray orderDetails = jsonObject.get("orderDetails").getAsJsonArray();
		
		String output = orders.addOrder(Integer.parseInt(buyerId), shippingAddress, orderDetails);
		
		response.getWriter().write(output);
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuffer jb = new StringBuffer();
		  String line = null;
		  JsonObject jsonObject;
		  try {
		    BufferedReader reader = request.getReader();
		    while ((line = reader.readLine()) != null)
		      jb.append(line);
		  } catch (Exception e) { /*report an error*/ }

		  try {
			  jsonObject = new JsonParser().parse(jb.toString()).getAsJsonObject();
		  } catch (JSONException e) {
		    // crash and burn
		    throw new IOException("Error parsing JSON request string");
		  }
		  
		  System.out.println(jsonObject);
		String buyerId = jsonObject.get("buyerId").toString();
		String shippingAddress = jsonObject.get("address").toString();
		String paySlip = jsonObject.get("paySlip").toString();
		String orderId = jsonObject.get("orderId").toString();
		JsonArray orderDetails = jsonObject.get("orderDetails").getAsJsonArray();
		
		String output = orders.updateOrder(Integer.parseInt(orderId), Integer.parseInt(buyerId), shippingAddress, orderDetails);
		System.out.println(output);
		
		orders.updatePayment(paySlip, Integer.parseInt(orderId));
		response.getWriter().write(output);
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map paras = getParasMap(request);
		String output = orders.deleteOrder(paras.get("id").toString());
		response.getWriter().write(output);
	}
	
	// Convert request parameters to a Map
		private static Map getParasMap(HttpServletRequest request) {
			Map<String, String> map = new HashMap<String, String>();
			try {
				Scanner scanner = new Scanner(request.getInputStream(), "UTF-8");
				String queryString = scanner.hasNext() ? scanner.useDelimiter("\\A").next() : "";
				scanner.close();
				String[] params = queryString.split("&");
				for (String param : params) {
					String[] p = param.split("=");
					map.put(p[0], p[1]);
				}
			} catch (Exception e) {
			}
			return map;
		}
	

}
