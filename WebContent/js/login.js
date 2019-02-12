function veriflogin(username, password){
	var ok = true
	$('#username').parent().removeClass("has-error")
	$('#passwd').parent().removeClass("has-error")
	if(username.length == 0){
		$('#username').parent().addClass("has-error")
		ok = false
	}
	if(password.length == 0){
		$('#passwd').parent().addClass("has-error")
		ok = false
	}
	return ok
}