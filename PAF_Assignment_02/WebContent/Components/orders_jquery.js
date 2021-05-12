$(document).ready(function() {
	$("#paymentForm").hide();
	$("#updateForm").hide();
});

var pRowCount = 1;

//Add Product Row
$(document).on("click", "#btnAddProduct", function(event) {
	console.log("ButtonClick");
	 $("#tableProducts").each(function () {
         var tds = '<tr>';
         jQuery.each($('tr:last td', this), function () {
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

//Remove Product Row 
$(document).on("click", "#removeProduct", function(event) {
	if(pRowCount > 1) {
	$(this).closest("tr").remove();
	pRowCount = pRowCount - 1;
	}
})

//Validate Initial Form
function validateOrderForm() {
	var status = true;
	
	if ($("#buyerId").val().trim() == "") {
		$("#buyerId").closest("input").addClass('is-invalid');
		status = false;
	} else {
		$("#buyerId").closest("input").removeClass('is-invalid').addClass('is-valid');
	}
	
	if ($("#address").val().trim() == "") {
		status = false;
		$("#address").closest("input").addClass('is-invalid');
	} else {
		$("#address").closest("input").removeClass('is-invalid').addClass('is-valid');
	}
	
	jQuery.each($('tr', $("#tableProducts")), function () {
		var i = 0;
        jQuery.each($('td', this), function () {
        	if (i < 2) {
        		
            if($(this).find('.pInputs').val().trim() == "") {
            	status = false;
            	$(this).find('.pInputs').addClass('is-invalid');
            } else {
            	console.log($(this).find('.pInputs').val());
            	$(this).find('.pInputs').removeClass('is-invalid').addClass('is-valid');
            }
            i = i + 1;
        	}
        });
    });
	return status;
}

$(document).on("click", "#proceed", function(event) {
	// Form validation-------------------
	var status = validateOrderForm();
	if (status != true) {
		//$("#alertError").text(status);
		//$("#alertError").show();
		return;
	}
	console.log("Proced 3456");
	var jsonInput = "{\"buyerId\" : " + $("#initialform").find('#buyerId').val() + ", \"address\" : " + $("#initialform").find('#address').val() 
	+ ", \"orderDetails\" : [";
	
	jQuery.each($('tr:not(tr:eq(0))', $("#tableProducts")), function () {
		jsonInput += "{\"productId\" : " + $(this).find('td:eq(0)').find('#pId').val() + ", \"quantity\" : " + $(this).find('td:eq(1)').find('#quantity').val() +"},"
        });
	
	jsonInput = jsonInput.substring(0,jsonInput.length -1) + "]}";
		
		console.log(jsonInput);
	
	
	$.ajax({
		url : "OrdersAPI",
		type : "POST",
		data : jsonInput,
		contentType: "application/json",
	    dataType: 'json',
		complete : function(response, status) {
			console.log(response.responseText);
			if (status == "success") {
				var resultSet = JSON.parse(response.responseText);
				$("#paymentFormAlert").text("Order placed Successfully and Add payment slip of LKR " + resultSet.total + " to proceed the order.");
				$("#paymentForm").show();
				$("#orderIdP").val(resultSet.id);
				$("#paymentForm").find('input').removeClass('is-invalid');
			}
		}
	});
	$("#initialform")[0].reset();
	$("#initialform").find('input').removeClass('is-valid').removeClass('is-invalid');
	$("#initialform").hide();
});


//Validate Payment Form
function validatePaymentForm() {
	var status = true;
	
	if ($("#paySlip").val().trim() == "") {
		$("#paySlip").closest("input").addClass('is-invalid');
		status = false;
	} else {
		$("#paySlip").closest("input").removeClass('is-invalid').addClass('is-valid');
	}
	
	return status;
}

//Payment Update
$(document).on("click", "#proceed", function(event) {
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
				$("#initialform").find('input').removeClass('is-invalid');
			}
		}
	});
	
	$("#paymentForm")[0].reset();
	$("#paymentForm").find('input').removeClass('is-valid').removeClass('is-invalid');
	$("#paymentForm").hide();
	
});

