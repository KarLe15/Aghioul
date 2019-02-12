print("creation de l'index des tendances...")

var maphashtags = function(){
	for(let i in this.weshEntities.hashtags){
		emit(this.weshEntities.hashtags[i], 1);
	}
}

var reduce = function(key, values) {
	return values.length;
}

db.messages.mapReduce(maphashtags, reduce, {out: "hashtagIndex"})

var mapmentions = function(){
	for(let i in this.weshEntities.mentions){
		emit(this.weshEntities.mentions[i], 1);
	}
}

db.messages.mapReduce(mapmentions, reduce, {out: "mentionIndex"})