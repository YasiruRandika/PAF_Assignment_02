<%@page import="model.Orders"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Orders - GB Platform</title>
<script src="Components/jquery-3.6.0.min.js"></script>
<script src="Components/orders_jquery.js"></script>
<link rel="stylesheet" href="Components/main.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.5.0/font/bootstrap-icons.css">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-wEmeIV1mKuiNpC+IOBjI7aAzPcEZeedi5yW5f2yOq55WWLwNGmvvx4Um1vskeMj0"
	crossorigin="anonymous">
</head>
<body>
<img src="Components/bg.png" id="bg" alt="">
	<div class="container">
	
		<div class="row row-cols-auto">
			<div class="col-sm-12 col-md-6">
				<form id="initialform" name="initialform">
					<h4>GB Order System</h4>
					<hr>
					<!-- Order Details User Input -->
					<label class="form-label">User Details</label> <input type="text"
						class="form-control" id="buyerId" name="buyerId"
						placeholder="Buyer Id" aria-label="Buyer Id"> <input
						type="text" class="form-control" id="address" name="address"
						placeholder="Shipping Address" aria-label="Shipping Address">
					<!-- Order Product Details User Input -->
					<br> <label class="form-label">Products Details</label>
					<table class="table" id="tableProducts">
						<thead>
							<tr>
								<th scope="col">Product Id</th>
								<th scope="col">Quantity</th>
								<th></th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><input type="text" id="pId"
									class="form-control pInputs"></td>
								<td><input type="number" id="quantity"
									class="form-control pInputs"></td>
								<td><button type="button" id="removeProduct"
										class="btn btn-danger btn-sm">Remove</button></td>
							</tr>
						</tbody>
					</table>
					<button type="button" id="btnAddProduct"
						class="btn btn-outline-primary btn-sm">
						<i class="bi bi-cart-plus-fill"></i>
					</button>
					<br> <br>
					<button class="btn btn-primary" type="button" id="proceed">Proceed
						to Payment</button>
				</form>

				<!-- Payment Form -->
				<form id="paymentForm" name="paymentForm">
					<h4>GB Order System</h4>
					<hr>
					<div class="alert alert-success" id="paymentFormAlert" role="alert">
						A simple success alert—check it out!</div>
					<br>
					<!-- Order Details User Input -->
					<input type="text" class="form-control" id="paySlip" name="paySlip"
						placeholder="Payment Slip" aria-label="Buyer Id"> <input
						type="text" id="orderIdP" name="orderIdP" hidden> <br>
					<button class="btn btn-primary" type="button" id="proceed">Add
						Payment</button>
				</form>

				<!-- Update Form -->
				<form id="updateForm" name="updateForm">
					<h4>GB Order System</h4>
					<hr>
					<!-- Order Details User Input -->
					 <input type="hidden" id="orderIdU" name="orderId">
					<label class="form-label">User Details</label> <input type="text"
						class="form-control" id="buyerIdU" name="buyerId"
						placeholder="Buyer Id" aria-label="Buyer Id"> <input
						type="text" class="form-control" id="addressU" name="address"
						placeholder="Shipping Address" aria-label="Shipping Address">
					<!-- Order Product Details User Input -->
					<br> <label class="form-label">Products Details</label>
					<table class="table" id="tableProductsCard">
						<thead>
							<tr>
								<th scope="col">Product Id</th>
								<th scope="col">Quantity</th>
								<th></th>
							</tr>
						</thead>
						<tbody id="dataIns">
							
						</tbody>
					</table>

					<button type="button" id="btnAddProductUp"
						class="btn btn-outline-primary btn-sm">
						<i class="bi bi-cart-plus-fill"></i>
					</button>
					<br> <br> <label class="form-label">Payment
						Details</label> <input type="text" class="form-control" id="paySlip"
						name="paySlip" placeholder="Payment Slip" aria-label="Buyer Id">

					<br>
					<button class="btn btn-primary" type="button" id="updateOrder">Update
						Order</button>
				</form>
				<div class="alertContainer">
				<div class="alert alert-danger" id = "alertError" role="alert">
  A simple primary alert—check it out!
</div>
<div class="alert alert-success" id = "alertOk" role="alert">
  A simple secondary alert—check it out!
</div></div>
			</div>
			<div class="col-sm-12 col-md-6">
				<div class="container" id="cardContainer">
					<%
					Orders orders = new Orders();
					out.print(orders.getAllOrders());
					%>
				</div>

			</div>
		</div>
	</div>
	
</body>
</html>