print("creation de l'index tf-idf de la collection messages...")

var map = function(){
	var tokens = this.text.trim().replace(/[.,\/#!$%\^&\*;:{}=\-_`~()?]/g,"").toLowerCase().split(" ");
	var words = {};
	for(var i=0; i<tokens.length; i++){
		if(words[tokens[i]] == undefined){
			words[tokens[i]] = 1;
		}
		else{
			words[tokens[i]] += 1;
		}
	}
	for(let w in words){
		var ret = {};
		ret[this._id.valueOf()] = words[w] / tokens.length;
		emit(w, ret);
	}
}

var reduce = function(key, values) {
	//key représente un mot et values une liste de {document : frequence}
	// --> on veut récupérer le tf-idf
	var idf = Math.log(N/(values.length));
	var ret = {};
	for(var i=0; i<values.length; i++){
		for(let d in values[i]){
			ret[d] = values[i][d] * idf;
		}
	}
	return ret;
}

db.messages.mapReduce(map, reduce, {out: "messageIndex", scope: {N: db.messages.count()}})