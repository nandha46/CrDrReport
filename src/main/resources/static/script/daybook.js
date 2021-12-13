/**
 * 
 */
 
 window.onload = function (){
			var min = document.getElementById("startDate").min;
			var max = document.getElementById("startDate").max;
			document.getElementById("startDate").value = min;
			document.getElementById("endDate").value = max;
		}