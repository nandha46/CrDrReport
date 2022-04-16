/**
 * 
 */

$(document).ready(function() {

	$("#companies").change(function() {
		fire_ajax_post();
	});

});

function fire_ajax_post() {
	let param = 'companyName='.concat($("#companies").val());

	$.ajax({
		type: "GET",
		contentType: "application/json",
		url: "/api/v1/years",
		data: param,
		dataType: 'json',
		cache: false,
		timeout: 600000,
		success: function(data) {
			console.log(data);

		let elSelect = $("#years");
	
		for (const [id, year] of Object.entries(data)){
			let option =  $("<option></option>").attr("value",`${id}`);
			option.text(`${year}`);
			elSelect.html(option);
		}

		},
		error: function(e) {

			var json = "<h4>Ajax Response</h4>&lt;pre&gt;"
				+ e.responseText + "&lt;/pre&gt;";
			$('#feedback').html(json);

			console.log("ERROR : ", e);
			$("#btn-search").prop("disabled", false);

		}
	});

}