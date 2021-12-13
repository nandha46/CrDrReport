/**
 *  Scripts specific for find Ledger page
 */

function level(obj) {
	// console.log(obj.value);
		if (obj.value.includes("true")) {
			document.getElementById("accs").removeAttribute("disabled");
		} else {
			document.getElementById("accs").setAttribute("disabled", "disabled");
		}
}

function multiSelect(obj) {
	console.log("level: " + obj.options[obj.selectedIndex].id + " count: " + obj.options[obj.selectedIndex].getAttribute('count'));
	if (obj.options[obj.selectedIndex].value == 0) {
		second(obj, parseInt(obj.options[obj.selectedIndex].id), parseInt(obj.options[obj.selectedIndex].getAttribute('count')));
	}
}

function second(elem, level, count) {
	//	console.log(typeof level);
	for (var i = 0; i < elem.options.length; i++) {
		if (parseInt(elem.options[i].getAttribute('count')) > count) {
			if (parseInt(elem.options[i].id) <= level) {
				return;
			}
			elem.options[i].selected = true;
			console.log("Selected:" + elem.options[i].getAttribute('count'));

		}
	}
}

window.onload = function (){
			var min = document.getElementById("startDate").min;
			var max = document.getElementById("startDate").max;
			document.getElementById("startDate").value = min;
			document.getElementById("endDate").value = max;
		}