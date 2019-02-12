function appendTendances(data){
	var div = $("#hashtags")
	for(let i in data.hashtags){
		div.append("<p><span class='aghioullabel'>"+data.hashtags[i]._id+"  "+data.hashtags[i].value+"</span></p>")
	}
	div = $("#mentions")
	for(let i in data.mentions){
		div.append("<p><span class='aghioullabel'>"+data.mentions[i]._id+"  "+data.mentions[i].value+"</span></p>")
	}
}