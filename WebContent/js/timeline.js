/*ce fichier inclut des éléments de w3schools https://www.w3schools.com/howto/howto_css_modals.asp
 * pour afficher une boite modale.
 * */

/*charge les nouveaux messages dans la timeline
	un nouveau message est un message plus récent que le plus 
	récent message affiché
*/
function chargerNouveauxMessages(){

	$.ajax({
		url : 'message',
		type : 'GET',
		data : 'key='+session.key+"&posteriorTo="+infos.oldestMessage,
		dataType : 'json',
		success : function(data, statut){
			if(data.weshs.length != 0){
				//memoriser les messages localement
			    appendMessages(data, '#timeline')
			    infos.mostRecentMessage = data.weshs[0].created_at.$date 
			}  
		}
	});
}

/*charge les messages suivants dans la timeline
	Ce sont les 15 messages plus anciens que le plus vieux 
	message affiché
*/
function chargerMessagesSuivants(){
	$.ajax({
		url : 'message',
		type : 'GET',
		data : 'key='+session.key+"&anteriorTo="+infos.oldestMessage,
		dataType : 'json',
		success : function(data, statut){
			if(data.weshs.length != 0){
				//memoriser les messages localement
			    infos.messages.concat(data.weshs)
			    appendMessages(data, '#timeline')
			    infos.oldestMessage = data.weshs[data.weshs.length - 1].created_at.$date 
			}  
		}
	});
}

/*voter pour une question du sondage.*/
function voter(node){
	var surveyId = node.value.split(" ")[0]
	$.ajax({
        url : 'survey',
        type : 'POST',
        data : 'key='+session.key+'&surveyId='+surveyId+'&question='+node.value.split(" ")[1],
        dataType : 'json',
        success : function(data, statut){
            //augmenter le compteur de 1
            node.previousSibling.textContent = (parseInt(node.previousSibling.textContent)+1).toString()
        }
    })
}

function publierSondage(node){
	var reponse1 = $('#reponse1').val()
	var reponse2 = $('#reponse2').val()
	var title = $('#title-sondage').val()
	$.ajax({
        url : 'survey',
        type : 'POST',
        data : 'title='+title+'&key='+session.key+'&reponse1='+reponse1+'&reponse2='+reponse2,
        dataType : 'json',
        success : function(data, statut){
            //afficher le sondage dans la timeline
            $('#timeline').prepend(surveyToHTML(data.survey))
        }
    });
    /*vider les champs de texte*/
	$('#reponse1').val("")
	$('#reponse2').val("")
	$('#title-sondage').val("")
	var modal = document.getElementById('myModal');
	//fermer la fenetre modal
	modal.style.display = "none"
	return false
}

function logout(node){
	$.ajax({
        url : 'logout',
        type : 'GET',
        data : 'username=' + session.username + '&key=' + session.key,
        dataType : 'json',
        success : function(data, statut){
            window.location.replace("/");
        }
    });
	return false
}

function publish(node){
	var wesh = $("#status").val();
	/// detecte weshs

	wesh = detectSmiley(wesh);
	console.log(wesh);
    if(wesh.length>0 && wesh.length < 140){
        $.ajax({
            url : 'publish',
            type : 'POST',
            data : 'message=' + wesh + '&key=' + session.key,
            dataType : 'json',
            success : function(data, statut){
                console.log(data);
                if(data.status == "Error"){
                    if(data.message == "session expired"){
                        alert("Vous avez été déconnecté après une inactivité prolongée.\n veuillez vous reconnecter");
                        window.location.replace("/");
                    }
                }else{
                    $("#status").val("") ;
                    $('#timeline').prepend(messageToHTML(data.wesh))
                    //ajouter a l'environnement
                }
            }
        });
    }
    return false
}


function detectSmiley(text){
	expression = new RegExp(":(?:\\)|D|\\()", "g");
    let matches = text.match(expression);
    if(matches.length === 0){
        return text;
    }

    let ret = "";
    let i = 0;
    for(match in matches) {
        let smiley = matches[match];

        ret += text.substr(i, startsAt = text.indexOf(matches[match]));
        ret += "<img src='images/emojis/"+smiley+".png'></img>";
        i = text.indexOf(matches[match]) + matches[match].length;
    }

    ret += text.substr(i);
    return ret;

}

function afficherTimeline(){

	//si on se trouvait sur une page profil
	if (infos.pageprofil != null){
		infos.pageprofil = null
		initTimeline()
		return;
	}

	//recuperer les abonnements de l'utilisateur
		$.ajax({
			async: false, //synchrone
			url : 'follow',
		    type : 'GET',
		    data : 'key='+session.key+'&followings='+session.username,
		    dataType : 'json',
		    success : function(data, statut){
		    	console.log(data)
		    	//on memorise lune table de hachage de nos abonnements
		    	session.followings = data.users
		    }
		})
		//recuperer les abonnés de l'utilisateur
		$.ajax({
			async: false, //synchrone
			url : 'follow',
		    type : 'GET',
		    data : 'key='+session.key+'&followers='+session.username,
		    dataType : 'json',
		    success : function(data, statut){
		    	console.log(data)
		    	//on memorise unes table de hachage de nos abonnés
		    	session.followers = data.users
		    }
		})
		$('#abonnes')[0].innerText = Object.keys(session.followers).length;
		$('#abonnements')[0].innerText = Object.keys(session.followings).length;
		/*afficher la timeline de l'utilisateur sur la page d'accueil*/
		$("#timeline").empty()
		$.ajax({
		    url : 'message',
		    type : 'GET',
		    data : 'key=' + session.key,
		    dataType : 'json',
		    success : function(data, statut){
		        console.log(data)
		        //memoriser les messages localement
		        infos.messages = data.weshs
		        appendMessages(data, '#timeline')
		        infos.mostRecentMessage = data.weshs[0].created_at.$date
		        infos.oldestMessage = data.weshs[data.weshs.length - 1].created_at.$date
		    }
		});
		//recupérer les tendances
		$("#mentions").empty()
		$("#hashtags").empty()
		$.ajax({
		    url : 'tendances',
		    type : 'GET',
		    dataType : 'json',
		    success : function(data, statut){
		        console.log(data)
		        appendTendances(data)
		    }
		});
		
		$.ajax({
		    url : 'survey',
		    type : 'GET',
		    data : 'key='+session.key,
		    dataType : 'json',
		    success : function(data, statut){
		        console.log(data)
		    	//memoriser les sondages
		    	infos.surveys = data.surveys
		    	for(var i=0; i<data.surveys.length; i++){
		    		$("#timeline").prepend(surveyToHTML(data.surveys[i]))
				}
		    }
		});

		return false;
}

function initTimeline(){
	$('body').load('timeline.html#body', function(data, status, xhr){
		var modal = document.getElementById('myModal');
		var span = document.getElementsByClassName("close-sondage")[0];
		// When the user clicks on the button, open the modal 
		$("#sondage-button").click(function() {
		    modal.style.display = "block";
		})
		// When the user clicks on <span> (x), close the modal
		span.onclick = function() {
			modal.style.display = "none";
		}
		$("#profile-pic")[0].src = (session.sex == "M")? "images/001-boy.png" : "images/002-girl.png";
		$('#user-username').text(session.username)
		$('#monprofil')[0].name = session.username
		$('#user-name').text(session.name)
		//recuperer les abonnements de l'utilisateur
		$.ajax({
			async: false, //synchrone
			url : 'follow',
		    type : 'GET',
		    data : 'key='+session.key+'&followings='+session.username,
		    dataType : 'json',
		    success : function(data, statut){
		    	console.log(data)
		    	//on memorise lune table de hachage de nos abonnements
		    	session.followings = data.users
		    }
		});
		//recuperer les abonnés de l'utilisateur
		$.ajax({
			async: false, //synchrone
			url : 'follow',
		    type : 'GET',
		    data : 'key='+session.key+'&followers='+session.username,
		    dataType : 'json',
		    success : function(data, statut){
		    	console.log(data)
		    	//on memorise unes table de hachage de nos abonnés
		    	session.followers = data.users
		    }
		})
		$('#abonnes')[0].innerText = Object.keys(session.followers).length;
		$('#abonnements')[0].innerText = Object.keys(session.followings).length;
		/*afficher la timeline de l'utilisateur sur la page d'accueil*/
		$.ajax({
		    url : 'message',
		    type : 'GET',
		    data : 'key=' + session.key,
		    dataType : 'json',
		    success : function(data, statut){
		        console.log(data)
		        appendMessages(data, '#timeline')
		        //memoriser les messages localement
		        infos.messages = data.weshs
		        infos.mostRecentMessage = data.weshs[0].created_at.$date
		        infos.oldestMessage = data.weshs[data.weshs.length - 1].created_at.$date

		        appear({
				  init: function init(){

				  },
				  elements: function elements(){
				  	//on se repère par rapport au footer, quand il apparait on charge les prochains messages
				    return document.getElementsByClassName('footer');
				  },
				  appear: function appear(el){
				  	//déclenchée quand le bas de la page est atteint
				  	chargerMessages()
				  	//$('#timeline').empty()
				  },
				  disappear: function disappear(el){
				  	//déclenchée quand le bas de la page n'est plus visible
				  },
				  bounds: 200,
				  reappear: true //ne marche pas qu'une seule fois!
				});
		    }
		});
		//recupérer les tendances
		$.ajax({
		    url : 'tendances',
		    type : 'GET',
		    dataType : 'json',
		    success : function(data, statut){
		        console.log(data)
		        appendTendances(data)
		    }
		});
		
		$.ajax({
		    url : 'survey',
		    type : 'GET',
		    data : 'key='+session.key,
		    dataType : 'json',
		    success : function(data, statut){
		        console.log(data);
		        for(var i=0; i<data.surveys.length; i++){
		    		$("#timeline").prepend(surveyToHTML(data.surveys[i]))
		    	}
		    	//memoriser les sondages
		    	infos.surveys = data.surveys
		    }
		});   
	});

	initSmiley();
	
}

function initSmiley(){
	/// mettre en place les onclick des bouton de smileys.



}


