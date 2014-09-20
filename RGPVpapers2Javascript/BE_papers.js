//alert("start");

var table=document.getElementById("extract");
var arr=table.getElementsByTagName("a");
var container=document.createElement("div");
var tr;
var month, year, monthset;
document.body.appendChild(container);
for(var i=0;i<arr.length;i++){
	//in11=arr[i].in1nerHTML;
	//console.log(arr[i].getElementsByTagName("span").length);
	monthset=false;
	tr=arr[i].parentNode;
	//console.log(tr.tagName);
	while(tr.tagName.toString()!="TR"){
		tr=tr.parentNode;
	}
	console.log(tr.getElementsByTagName("td").length);
	console.log(tr.childNodes);
	console.log(tr);
	if(tr.getElementsByTagName("td").length==3){
		year=tr.children[0].innerText;
		console.log("Setting year to "+year);
		if(!monthset){
			month=tr.children[1].innerText;
			console.log("Setting month to "+month);
		}
	}
	if(tr.getElementsByTagName("td").length==2){
		console.log("Setting month to "+month);
		month=tr.children[0].innerText;
		monthset=true;
	}
	
	if(arr[i].getElementsByTagName("span").length==0){
		//console.log(arr[i]);
		container.innerHTML=container.innerHTML+year+","+month+","+arr[i].innerText+","+arr[i].getAttribute("href")+"<br>";
	}else if(arr[i].getElementsByTagName("span").length==1){
		//console.log(arr[i].getElementsByTagName("span")[0].innerText);
		container.innerHTML=container.innerHTML+year+","+month+","+arr[i].getElementsByTagName("span")[0].innerText+","+arr[i].getAttribute("href")+"<br>";
	}else{
		alert("more than 2");
	}

}
document.body.appendChild(container);
//console.log(container);