/*

common javascript for 3 find pages
 */
 function show_level (obj) {
	if (obj.value.includes("true")) {
		document.getElementById("accs").removeAttribute("disabled");
		document.getElementById("select_level").style.display = "block";
	} else {
		document.getElementById("accs").setAttribute("disabled", "disabled");
		document.getElementById("select_level").style.display = "none";
	}
	
}

function select_acc (obj){
	// console.log(typeof obj.value);
	var accs = document.getElementById("accs");
	switch (obj.value){
		case "1":
		select(accs,1);
		break;
		case "2":
		select(accs,2);
		break;
		case "3":
		select(accs,3);
		break;
		case "4":
		select(accs,4);
		break;
		case "5":
		select(accs,5);
		break;
		case "6":
		select(accs,6);
		break;
	}
}

function select(accs, lvl){
	for (var i = 0; i<accs.options.length; i++){
		
		if (parseInt(accs.options[i].id) <= lvl) {
			accs.options[i].disabled = false;
			accs.options[i].selected = true;
		} else {
		accs.options[i].disabled = true;
		accs.options[i].selected = false;	
		}
	}
	
}

 window.onload = function (){
			var max = document.getElementById("endDate").max;
			document.getElementById("endDate").value = max;
			
			var stock = document.getElementById("closingStock");
			if (stock != null){
				stock.value = stock.getAttribute("stock");
			}
		}