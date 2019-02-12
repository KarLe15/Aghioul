let re_mail = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
let re_password = /^[a-zA-Z0-9-]{8,30}$/;
let re_username = /^[a-zA-Z0-9_-]{6,25}$/;
let re_nom = /^[a-zA-Z0-9\s-]{3,25}$/;
let re_sexe = /^[M|F]$/;

var api_key = "";

function verifsignup(){
		$('#userName').parent().parent().removeClass("has-error")
		$('#passwd-1').parent().parent().removeClass("has-error")
		$('#passwd-2').parent().parent().removeClass("has-error")
		$('#firstname').parent().parent().removeClass("has-error")
		$('#lastname').parent().parent().removeClass("has-error")
		$('#mail').parent().parent().removeClass("has-error")
		$('#sexe').removeClass("has-error")

		var ok = true
		if(!re_mail.test($('#mail').val())){
			$('#mail').parent().parent().addClass("has-error")
			ok = false
		}
		if($('#passwd-1').val() !== $('#passwd-2').val()){
			$('#passwd-1').parent().parent().addClass("has-error")
			$('#passwd-2').parent().parent().addClass("has-error")
			ok = false
		}
		if(!re_password.test($('#passwd-1').val())){
			$('#passwd-1').parent().parent().addClass("has-error")
			$('#passwd-2').parent().parent().addClass("has-error")
			ok = false
		}
		if(!re_username.test($('#userName').val())){
			$('#userName').parent().parent().addClass("has-error")
			ok = false
		}
		if(!re_nom.test($('#firstname').val())){
			$('#firstname').parent().parent().addClass("has-error")
			ok = false
		}
		if(!re_nom.test($('#lastname').val())){
			$('#lastname').parent().parent().addClass("has-error")
			ok = false
		}
		if(!re_sexe.test($('input[name=sexe]:checked').val())){
			$('#sexe').addClass("has-error")
			ok = false
		}

		return ok
	}


function clean_form() {
	$('#mail').val("")
	$('#passwd-1').val("")
	$('#passwd-1').val("")
	$('#userName').val("")
	$('#firstname').val("")
	$('#lastname').val("")
}
