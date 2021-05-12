$(document).ready(function() {
	$("#paymentForm").hide();
	$("#updateForm").hide();
});

var pRowCount = 1;

// Add Product Row
$(document).on("click", "#btnAddProduct", function(event) {
	console.log("ButtonClick");
	$("#tableProducts").each(function() {
		var tds = '<tr>';
		jQuery.each($('tr:last td', this), function() {
			tds += '<td>' + $(this).html() + '</td>';
		});
		tds += '</tr>';
		if ($('tbody', this).length > 0) {
			$('tbody', this).append(tds);
		} else {
			$(this).append(tds);
		}
		pRowCount = pRowCount + 1;
	});
});

// Remove Product Row
$(document).on("click", "#removeProduct", function(event) {
	if (pRowCount > 1) {
		$(this).closest("tr").remove();
		pRowCount = pRowCount - 1;
	}
})

// Validate Initial Form
function validateOrderForm() {
	var status = true;

	if ($("#buyerId").val().trim() == "") {
		$("#buyerId").closest("input").addClass('is-invalid');
		status = false;
	} else {
		$("#buyerId").closest("input").removeClass('is-invalid').addClass(
				'is-valid');
	}

	if ($("#address").val().trim() == "") {
		status = false;
		$("#address").closest("input").addClass('is-invalid');
	} else {
		$("#address").closest("input").removeClass('is-invalid').addClass(
				'is-valid');
	}

	jQuery.each($('tr', $("#tableProducts")), function() {
		var i = 0;
		jQuery.each($('td', this), function() {
			if (i < 2) {

				if ($(this).find('.pInputs').val().trim() == "") {
					status = false;
					$(this).find('.pInputs').addClass('is-invalid');
				} else {
					console.log($(this).find('.pInputs').val());
					$(this).find('.pInputs').removeClass('is-invalid')
							.addClass('is-valid');
				}
				i = i + 1;
			}
		});
	});
	return status;
}


//Add Order
$(document).on(
		"click",
		"#proceed",
		function(event) {
			// Form validation-------------------
			var status = validateOrderForm();
			if (status != true) {
				// $("#alertError").text(status);
				// $("#alertError").show();
				return;
			}
			console.log("Proced 3456");
			var jsonInput = "{\"buyerId\" : "
					+ $("#initialform").find('#buyerId').val()
					+ ", \"address\" : "
					+ $("#initialform").find('#address').val()
					+ ", \"orderDetails\" : [";

			jQuery.each($('tr:not(tr:eq(0))', $("#tableProducts")), function() {
				jsonInput += "{\"productId\" : "
						+ $(this).find('td:eq(0)').find('#pId').val()
						+ ", \"quantity\" : "
						+ $(this).find('td:eq(1)').find('#quantity').val()
						+ "},"
			});

			jsonInput = jsonInput.substring(0, jsonInput.length - 1) + "]}";

			console.log(jsonInput);

			$.ajax({
				url : "OrdersAPI",
				type : "POST",
				data : jsonInput,
				contentType : "application/json",
				dataType : 'json',
				complete : function(response, status) {
					console.log(response.responseText);
					if (status == "success") {
						var resultSet = JSON.parse(response.responseText);
						$("#paymentFormAlert").text(
								"Order placed Successfully and Add payment slip of LKR "
										+ resultSet.total
										+ " to proceed the order.");
						$("#paymentForm").show();
						$("#orderIdP").val(resultSet.id);
						$("#paymentForm").find('input').removeClass(
								'is-invalid');
					}
				}
			});
			$("#initialform")[0].reset();
			$("#initialform").find('input').removeClass('is-valid')
					.removeClass('is-invalid');
			$("#initialform").hide();
			pRowCount = 1;
		});

// Validate Payment Form
function validatePaymentForm() {
	var status = true;

	if ($("#paySlip").val().trim() == "") {
		$("#paySlip").closest("input").addClass('is-invalid');
		status = false;
	} else {
		$("#paySlip").closest("input").removeClass('is-invalid').addClass(
				'is-valid');
	}

	return status;
}

// Payment Update
$(document).on(
		"click",
		"#proceed",
		function(event) {
			var status = validatePaymentForm();

			if (status != true) {
				return;
			}

			$.ajax({
				url : "OrdersPayments",
				type : "POST",
				data : $("#paymentForm").serialize(),
				dataType : "text",
				complete : function(response, status) {
					if (status == "success") {
						$("#initialform").show();
						$("#initialform").find('input').removeClass(
								'is-invalid');
					}
				}
			});

			$("#paymentForm")[0].reset();
			$("#paymentForm").find('input').removeClass('is-valid')
					.removeClass('is-invalid');
			$("#paymentForm").hide();

		});

// Delete Order
$(document).on("click", "#btnCancelOrder", function(event) {
	console.log("Remove Button Clicked");
	var orderId = $(this).closest(".card").find('#c_OrderId').val();
	$.ajax({
		url : "OrdersAPI",
		type : "DELETE",
		data : "id=" + orderId,
		dataType : "text",
		complete : function(response, status) {
			// onItemDeleteComplete(response.responseText, status);
			console.log(response.responseText);
		}
	});
});

// Update Order
$(document).on("click", "#btnEditOrder", function(event) {
	$("#updateForm")[0].reset();
	$('#updateForm').find('#tableProductsCard').find('tbody').empty();
	$('#updateForm').find('#buyerIdU').val($(this).closest('.card').find('#c_BuyerId').val());
	$('#updateForm').find('#addressU').val($(this).closest('.card').find('#c_Address').val());
	$('#updateForm').find('#paySlip').val($(this).closest('.card').find('#c_PaySlip').val());
	$('#updateForm').find('#orderIdU').val($(this).closest('.card').find('#c_OrderId').val());
	var tb = "";
	pRowCount = 0;
	jQuery.each($('tr', $(this).closest(".card").find("#tableProductsDisplay").find("tbody")), function() {
		tb += "<tr>" + 
		"<td><input type=\"text\" id=\"pId\" class=\"form-control pInputs\" value = '" + $(this).closest(".card").find("#tableProductsDisplay").find("tbody").find('td:eq(0)').text() +"'>"+
		"</td>"+
		"<td><input type=\"number\" id=\"quantity\" class=\"form-control pInputs\" value = '" +$(this).closest(".card").find("#tableProductsDisplay").find("tbody").find('td:eq(1)').text()  +"'></td>"+
		"<td><button type=\"button\" id=\"removeProduct\"+ class=\"btn btn-danger btn-sm\">Remove</button></td>"
							+"</tr>"
		pRowCount += 1;
	});
	
	$('#updateForm').find('#tableProductsCard').find('tbody').append(tb);
	$("#initialform")[0].reset();
	$("#initialform").find('input').removeClass('is-valid')
			.removeClass('is-invalid');
	$("#initialform").hide();
	$("#updateForm").show();
	
});

//Add Product Row in Update Form
$(document).on("click", "#btnAddProductUp", function(event) {
	console.log("ButtonClick");
	$("#tableProductsCard").each(function() {
		var tds = '<tr>';
		jQuery.each($('tr:last td', this), function() {
			tds += '<td>' + $(this).html() + '</td>';
		});
		tds += '</tr>';
		if ($('tbody', this).length > 0) {
			$('tbody', this).append(tds);
		} else {
			$(this).append(tds);
		}
		
		pRowCount = pRowCount + 1;
	});
	
	$("#tableProductsCard").find('tr:last').find('.pInputs').val("");
});

//Validate Updating  Form
function validateUpdateForm() {
	var status = true;

	if ($("#buyerIdU").val().trim() == "") {
		$("#buyerIdU").closest("input").addClass('is-invalid');
		status = false;
	} else {
		$("#buyerIdU").closest("input").removeClass('is-invalid').addClass(
				'is-valid');
	}

	if ($("#addressU").val().trim() == "") {
		status = false;
		$("#addressU").closest("input").addClass('is-invalid');
	} else {
		$("#addressU").closest("input").removeClass('is-invalid').addClass(
				'is-valid');
	}
	

	jQuery.each($('tr', $("#tableProductsCard")), function() {
		var i = 0;
		jQuery.each($('td', this), function() {
			if (i < 2) {

				if ($(this).find('.pInputs').val().trim() == "") {
					status = false;
					$(this).find('.pInputs').addClass('is-invalid');
				} else {
					console.log($(this).find('.pInputs').val());
					$(this).find('.pInputs').removeClass('is-invalid')
							.addClass('is-valid');
				}
				i = i + 1;
			}
		});
	});
	return status;
}


//Update Order
$(document).on(
		"click",
		"#updateOrder",
		function(event) {
			// Form validation-------------------
			var status = validateUpdateForm();
			console.log("Status"  +status);
			if (status != true) {
				// $("#alertError").text(status);
				// $("#alertError").show();
				return;
			}
			console.log("Proced 3456");
			var jsonInput = "{\"buyerId\" : "
					+ $("#updateForm").find('#buyerIdU').val()
					+", \"orderId\" : " + $("#updateForm").find('#orderIdU').val()
					+ ", \"paySlip\" : " + $("#updateForm").find('#paySlip').val() +  ",\"address\" : "
					+ $("#updateForm").find('#addressU').val()
					+ ", \"orderDetails\" : [";

			jQuery.each($('tr:not(tr:eq(0))', $("#tableProductsCard")), function() {
				jsonInput += "{\"productId\" : "
						+ $(this).find('td:eq(0)').find('#pId').val()
						+ ", \"quantity\" : "
						+ $(this).find('td:eq(1)').find('#quantity').val()
						+ "},"
			});

			jsonInput = jsonInput.substring(0, jsonInput.length - 1) + "]}";

			console.log(jsonInput);

			$.ajax({
				url : "OrdersAPI",
				type : "PUT",
				data : jsonInput,
				contentType : "application/json",
				complete : function(response, status) {
					console.log(response.responseText);
					console.log(status);
					var resultSet = JSON.parse(response);
					if (resultSet.status.trim() == "success") {
						$("#initialform").show();
						$("#initialform").find('input').removeClass('is-invalid');
						$("#tableProductsCard").find('tbody').html(resultSet.data);
					} else if (resultSet.status.trim() == "error") {
						//$("#alertError").text(resultSet.data);
						//$("#alertError").show();
					}
				}
			});
			$("#updateForm")[0].reset();
			$("#updateForm").find('input').removeClass('is-valid')
					.removeClass('is-invalid');
			$("#updateForm").hide();
			pRowCount = 1;
		});
