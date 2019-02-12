/*fichier global contenant les instructions pour l'ensemble du site*/

var session = {key : null, id: null, username: null, name: null, sex: null, followings : null, followers: null}

var infos = {pageprofil: null, mostRecentMessage: null, oldestMessage: null}

$(function() {
	$("#loginform").submit(function(e){
		var username = $('#username').val();
		var password = $('#passwd').val();
		if(!veriflogin(username, password)){
			e.preventDefault();
			return
		}
		 $.ajax({
		       url : 'login',
		       type : 'POST',
		       data : 'username=' + username + '&password=' + password,
		       dataType : 'json',
		       success : function(data, statut){
		         	console.log(data);
				   if(data.key != undefined){
					   session.username = username;
					   session.name = data.name;
					   session.sex = data.sex;
					   session.key = data.key;
					   session.id = data.id;
					   initTimeline()
				   }
		       }
		    });
		e.preventDefault()
	});

	$("#registerform").submit(function(e){
		var username = $('#userName').val()
		var password = $('#passwd-1').val()
		var email = $('#mail').val()
		var nom = $('#lastname').val()
		var prenom = $('#firstname').val()
		var sexe = $('input[name=sexe]:checked').val()
		if(!verifsignup()){
			e.preventDefault()
			return
		}
		$.ajax({
		       url : 'signup',
		       type : 'POST',
		       data : 'nom='+nom+'&prenom='+prenom+'&password='+password+'&username='+username+'&sexe='+sexe+"&email="+email,
		       dataType : 'json',
		       success : function(data, statut){
		    	   //à remplir
		    	   if(data.status == "Error"){
		    	   	//traiter les cas d'erreur
		    	   } else{
		    	   	clean_form()
		    	   	console.log(data)
		    	   }
		           
		       }
		    });
		e.preventDefault()
	})
});

