/* document.getElementById("exampleFormControlSelect1").onchange = function () {
		  document.getElementById("accs").setAttribute("disabled", "disabled");
		  if (this.value == true)
			document.getElementById("accs").removeAttribute("disabled");
		}; */


			document.getElementById("exampleFormControlSelect1").onchange = function() {
		document.getElementById("accs").setAttribute("disabled", "disabled");
	if (this.value.includes("true")) 
		document.getElementById("accs").removeAttribute("disabled");
};
