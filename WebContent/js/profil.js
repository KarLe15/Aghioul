function loadTimeline(){
	$('body').load('timeline.html#body')
	initTimeline()
	return false
}

function initProfil(){
	$(function(){
		if(infos.pageprofil.sex == "M"){
			$('#profile-pic')[0].src = "images/001-boy.png"
		}
		else{
			$('#profile-pic')[0].src = "images/002-girl.png"
		}

		if(session.sex == "M"){
			$('#user-pic')[0].src = "images/001-boy.png"
		}
		else{
			$('#user-pic')[0].src = "images/002-girl.png"
		}

		$('#profil-username')[0].text = infos.pageprofil.username
		$('#profil-name')[0].text = infos.pageprofil.name

		$("#user-username")[0].text = session.username
		$("#user-name")[0].text = session.name

		$('#abonnements')[0].innerText = Object.keys(session.followings).length;
		$('#abonnes')[0].innerText = Object.keys(session.followers).length;

		$.ajax({
			async: false, //synchrone
			url : 'follow',
		    type : 'GET',
		    data : 'key='+session.key+'&followings='+infos.pageprofil.username,
		    dataType : 'json',
		    success : function(data, statut){
				$('#followings')[0].innerText = Object.keys(data.users).length;
		    }
		})
		//recuperer les abonnés de l'utilisateur
		$.ajax({
			async: false, //synchrone
			url : 'follow',
		    type : 'GET',
		    data : 'key='+session.key+'&followers='+infos.pageprofil.username,
		    dataType : 'json',
		    success : function(data, statut){
		    	$('#followers')[0].innerText = Object.keys(data.users).length;
		    }
		})
	})
}




