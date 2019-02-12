var mois = ['janvier', 'fevrier', 'mars', 'avril', 'mai', 'juin', 'juillet', 'Aout', 'septembre', 'octobre', 'novembre', 'decembre']

function afficherprofil(node){
	var username = node.name
	
	$('body').load('profil.html#body', function(response, status, xhr){
		$.ajax({
	        url : 'search',
	        type : 'GET',
	        data : 'key='+session.key+'&user='+username,
	        dataType : 'json',
	        success : function(data, statut){
	            console.log(data)
	            infos.pageprofil = data.user
	            appendMessages(data, '#panel-messages')
	            initProfil()

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
	        }
	    })
	});
	
	return false;
}

function commenter(node){
	//node vaut le bouton de publication de commentaire
	var text = node.previousSibling.value
    node.previousSibling.value = "" //vider le champ commentaire
	var messageId = node.previousSibling.previousSibling.value
	$.ajax({
        url : 'comment',
        type : 'POST',
        data : 'key='+session.key+'&text='+text+'&messageId='+messageId,
        dataType : 'json',
        success : function(data, statut){
        	//afficher le commentaire
        	var div = node.parentElement.previousSibling
        	$(div).prepend(commentToHTML(data.comment))
        }
    });
	return false;
}


function follow(node){
	var follow = node.textContent
    //suivre l'utilisateur
    $.ajax({
        url : 'follow',
        type : 'POST',
        data : 'key='+session.key+'&user='+session.username+'&follow='+follow,
        dataType : 'json',
        success : function(data, statut){
            //rendre le bouton non cliquable et afficher une petite croix mimi
            session.followings[follow] = true
            node.disabled="disabled"
            var lol = document.createElement("span")
            lol.className = "glyphicon glyphicon-ok"
            node.prepend(lol)
        }
    });
	return false;
}

function like(node){
	var messageId = node.parentNode.firstElementChild.value
	$.ajax({
        url : 'message',
        type : 'POST',
        data : 'key='+session.key+'&update=like&messageId='+messageId,
        dataType : 'json',
        success : function(data, statut){
            var nb = node.lastElementChild.textContent
            node.lastElementChild.textContent = (parseInt(nb) + 1).toString()
        }
    });
	return false;
}

function dislike(node){
	//recuperer la valeur de l'id du message
	var messageId = node.parentNode.firstElementChild.value
	$.ajax({
        url : 'message',
        type : 'POST',
        data : 'key='+session.key+'&update=dislike&messageId='+messageId,
        dataType : 'json',
        success : function(data, statut){
            var nb = node.lastElementChild.textContent
            node.lastElementChild.textContent = (parseInt(nb) + 1).toString()
        }
    });
}

function share(node){
	//recuperer la valeur de l'id du message
	var messageId = node.parentNode.firstElementChild.value
	$.ajax({
        url : 'message',
        type : 'POST',
        data : 'key='+session.key+'&update=share&messageId='+messageId,
        dataType : 'json',
        success : function(data, statut){
        	/*incrementer le nombre de partages*/
            var nb = node.lastElementChild.textContent
            node.lastElementChild.textContent = (parseInt(nb) + 1).toString()
            /*afficher le wesh dans la timeline*/
            $('#timeline').prepend(messageToHTML(data.wesh))
        }
    });
}

function commentToHTML(comment){
	var d = new Date(comment.created_at.$date)
	var hour = d.getHours()+"h"+d.getMinutes()
	var date = d.getDate()+" "+mois[d.getMonth()]+" "+d.getFullYear()
	var res = "<div class='comment'><blockquote>"+comment.text+"<footer>"+comment.user.username+
		 " le "+date+" à "+hour+"</footer></blockquote></div>"
	return res
}

function commentsToHTML(wesh){
	var res = ""
	for(var i=0; i<wesh.comments.length; i++){
		var d = new Date(wesh.comments[i].created_at.$date)
		var hour = d.getHours()+"h"+d.getMinutes()
		var date = d.getDate()+" "+mois[d.getMonth()]+" "+d.getFullYear()
		 res+="<div class='comment'><blockquote>"+wesh.comments[i].text+"<footer>"+wesh.comments[i].user.username+
		 " le "+date+" à "+hour+"</footer></blockquote></div>"
	}
	return res
}

function reweshToHTML(wesh){
	var d = new Date(wesh.created_at.$date)
	var hour = d.getHours()+"h"+d.getMinutes()
	var msg = "<div class='well well-sm' style='background-color: #ecf0f1;'><p class='rewesh-p'>le "+d.getDate()+" "+mois[d.getMonth()]+" "+d.getFullYear()+" à "+hour+", "+wesh.user.username+" a rewesh</p>"
    + "<div class='row'><div class='col-sm-3'><a name='"+wesh.rewesh_user.username+"' onclick='afficherprofil(this); return false;'><img src='"
    if(wesh.rewesh_user.sex == "F" ) msg += "images/002-girl.png"
    else msg+="images/001-boy.png"
    msg+="' class='img-thumbnail' height='50' width='50' alt='Avatar'></a>"+
    "<p><a href=''>"+wesh.rewesh_user.name+"</a></p><p>"
    if(session.followings[wesh.user.username] == true || session.username == wesh.user.username){
    	msg+="<button type='button' disabled='disabled' class='btn btn-default btn-sm username-follow' href=''>"+wesh.rewesh_user.username+"</button></p></div>"
    }
    else{
        msg+="<button type='button' onclick='return follow(this)' class='btn btn-default btn-sm username-follow' href=''>"+wesh.rewesh_user.username+"</button></p></div>"
    }
    msg+="<div class='col-sm-9'><blockquote><p>"+wesh.text+"</p></blockquote><div class='btn-group btn-group-xs'>"+
    "<input type='hidden' value='"+wesh.rewesh_from.$oid+"'><button type='button' onclick='like(this)' class='btn btn-primary aghioul-like'><span class='glyphicon glyphicon-heart'></span>  trop bien! <span class='badge'>"+
    wesh.likes+"</span></button>"+
    "<button type='button' onclick='dislike(this)' class='btn btn-default aghioul-dislike'><span class='glyphicon glyphicon-remove'></span>  je n'aime pas <span class='badge'>"+wesh.dislikes+"</span></button>"+
    "<button type='button' onclick='share(this)' class='btn btn-default aghioul-share'><span class='glyphicon glyphicon-share'></span>  partager <span class='badge'>"+wesh.reweshes+"</span></button></div>"
    +"<div class='comments'>"+commentsToHTML(wesh)+"</div>"
    +"<div class='form-group textarea-comment'><input type='hidden' value='"+wesh._id.$oid+"'><textarea class='form-control textarea-comment' placeholder='racontez votre vie...' rows=1'></textarea><button type='button' class='btn btn-default btn-sm' onclick='return commenter(this)'> donner mon avis!</button></div>"
    +"</div></div></div>"

    return msg
}

function messageToHTML(wesh){
	
	if(wesh.is_rewesh)
		return reweshToHTML(wesh)
	var d = new Date(wesh.created_at.$date)
	var hour = d.getHours()+"h"+d.getMinutes()
	var msg = "<div class='row aghioul-message'><div class='col-sm-3'><div class='well'><a name='"+wesh.user.username+"' onclick='afficherprofil(this); return false'><img src='"
		if(wesh.user.sex == "M" )msg+="images/001-boy.png' class='img-thumbnail' height='90' width='90' alt='Avatar'></a>";
		else msg+="images/002-girl.png' class='img-thumbnail' height='90' width='90' alt='Avatar'>";
        msg += "<p><a href=''>"+wesh.user.name+"</a></p>"
        if(session.followings[wesh.user.username] == true || session.username == wesh.user.username){
        	msg+= "<p><button type='button' disabled='disabled' class='btn btn-default btn-sm username-follow' href=''>"+wesh.user.username+"</button></p></div></div>";
        }
        else{
            msg += "<p><button type='button' onclick='follow(this); return false;' class='btn btn-default btn-sm username-follow' href=''>"+wesh.user.username+"</button></p></div></div>";
        }
        msg += "<div class='col-sm-9'><blockquote><p>"+wesh.text+"</p><footer>le "+d.getDate()+" "+mois[d.getMonth()]+" "+d.getFullYear()+" à "+hour+"</footer></blockquote>";
        msg += "<div class='btn-group btn-group-xs'>"+
        	"<input type='hidden' value='"+wesh._id.$oid+"'>"+
            "<button type='button' onclick='like(this)' class='btn btn-primary aghioul-like'><span class='glyphicon glyphicon-heart'></span>  trop bien! <span class='badge'>"+wesh.likes+"</span></button>"+
            "<button type='button' onclick='dislike(this)' class='btn btn-default aghioul-dislike'><span class='glyphicon glyphicon-remove'></span>  je n'aime pas <span class='badge'>"+wesh.dislikes+"</span></button>"+
            "<button type='button' onclick='share(this)' class='btn btn-default aghioul-share'><span class='glyphicon glyphicon-share'></span>  partager <span class='badge'>"+wesh.reweshes+"</span></button></div>"+
            "<div class='comments'>"+commentsToHTML(wesh)+"</div>"+
            "<div class='form-group'>"+
            "<input type='hidden' value='"+wesh._id.$oid+"'><textarea class='form-control textarea-comment' rows=1' placeholder='racontez votre vie...'></textarea>"+
            "<button type='button' class='btn btn-default btn-sm' onclick='return commenter(this)'> donner mon avis!</button>"
            +"</div>"
            +"</div></div>";

	return msg;
}

function surveyToHTML(survey){
	var d = ""
	if(survey.expire_date != null)
		d = new Date(survey.date.$date)
	else 
		d = null
    var surv = "<div class='row'><div class='col-sm-3'>"+
                    "<div class='well'>"
    if(survey.user.sex == "M" )
        surv += "<a name='' onclick='afficherprofil(this); return false; return false'><img src='images/001-boy.png' class='img-thumbnail' height='90' width='90' alt='Avatar'></a>"
    else
        surv += "<a name='' onclick='afficherprofil(this); return false'><img src='images/002-girl.png' class='img-thumbnail' height='90' width='90' alt='Avatar'></a>"
                        "<p><a href=''>"+survey.user.name+"</a></p><p>"
                        if(session.followings[survey.user.username] == true || session.username == survey.user.username)
                        	surv+= "<button type='button' onclick='return follow(this)' class='btn btn-default btn-sm username-follow' href=''>"+survey.user.username+"</button></p>"
                        else
                        	surv+= "<button type='button' disabled='disabled' class='btn btn-default btn-sm username-follow' href=''>"+survey.user.username+"</button></p>"
                    surv+= "</div>"+
                "</div>"+
                "<div class='col-sm-9'>"+
                    "<h3 class='sondage-titre'>"+survey.title+"</h3>"
    for(var i=0; i<survey.questions.length; i++){
        surv += "<p class='sondage-question'>"+survey.questions[i].question+"</p>"+
                    "<meter min='0' max='100' value='"+survey.questions[i].votes.toString()+"' class='meter-option1'></meter>"+
                    "<span class='sondage-compte'>"+survey.questions[i].votes.toString()+"</span>"+
                    "<button type='button' class='btn bouton-option1 btn-sm' onclick='return voter(this)' value='"+survey._id.$oid+" "+i.toString()+"'>vote!</button>"
    }
	if(d != null)
		surv += "<p class='expire-date'>expire le "+d.getDate()+" "+mois[d.getMonth()]+" "+d.getFullYear()+"</p></div></div>"

    return surv
}


function appendMessages(data, htmlNode){
	/*prend en param. un objet Json de type 
	tel que celui retourné par l'API et un node
	et ajoute (preprend) les messages dans ce node*/
	for(var i=0; i<data.weshs.length; i++){
		$(htmlNode).append(messageToHTML(data.weshs[i]))
	}
	
}


function prout(text){
    var expression = /(https?:\/\/(?:www\.|(?!www))[^\s\.]+\.[^\s]{2,}|www\.[^\s]+\.[^\s]{2,})/gi;
    var matches = text.match(expression);
    if(matches.length == 0)
        return text;

    ret = "";
    var i = 0;
    for(match in matches) {
        var link = matches[match];

        ret += text.substr(i, startsAt = text.indexOf(matches[match]))
        ret += "<a target='blank' href='"+link+"'>"+link+"</a>"
        i = text.indexOf(matches[match]) + matches[match].length;
    }

    ret += text.substr(i)

    return ret

}

"yo la mif allez checker https://www.youtube.com/watch?v=ZMwbswoUdP8 mon son c un pur délire et http://www.toutjavascript.com/reference/ref-string.substr.php c un ouf"