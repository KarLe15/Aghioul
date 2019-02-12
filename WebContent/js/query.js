function aghioulquery() {
	var query = $("#query").val().trim().replace(/[.,\/#!$%\^&\*;:{}=\-_`~()?]/g,"").toLowerCase();
	console.log(query);
	/////////////////////////////////////
    let searchMessage = true;
    let nbMot = query.split(" ");
    if( nbMot.length === 1){
        $.ajax({
            async: false,
            url: 'search',
            type : "GET",
            data: "key="+session.key+"&user="+nbMot[0],
            dataType: 'json',
            success : (data, statut)=>{
                if(data.status ==="Accepted"){
                    console.log("Utilisateur trouvé");
                    console.log(data);
                    searchMessage = false;
                    infos.messages = data.messages.weshs;

                }
            }
        })
    }


    if(searchMessage) {
        //parser la requête (sera a nouveau verifiee coté serveur)
        $.ajax({
            async: false, //synchrone
            url: 'search',
            type: 'GET',
            data: 'key=' + session.key + "&query=" + query,
            dataType: 'json',
            success: function (data, statut) {
                if (data.status == "Accepted") {
                    console.log("query accepted");
                    infos.messages = data.weshs
                }
            }
        })
    }
	//si la requête n'a renvoyé aucun message
	if (infos.messages.length < 1){
		alert("aucun message ne correspond à votre recherche")
	}
	else{
		//on remplace les messages sur la page
		$("#timeline").empty() //pour enlever les messages affichés
		//on remplace...
		for(let i in infos.messages){
			$("#timeline").append(messageToHTML(infos.messages[i]))
		}
	}

	return false;
}