//alert("start");

var table=document.getElementById("extract");
var arr=table.getElementsByTagName("a");
var container=document.createElement("div");
var tr;
var month, year, monthset;
document.body.appendChild(container);
for(var i=0;i<20;i++){
	//in11=arr[i].in1nerHTML;
	//console.log(arr[i].getElementsByTagName("span").length);
	monthset=false;
	tr=arr[i].parentNode;
	console.log(arr[i].parentNode);
	while(tr.tagName!="tr"){
		tr=tr.parentNode;
	}
	if(tr.childNodes==4){
		year=tr.children[0].innerText;
		if(!monthset){
			month=tr.children[1].innerText;
		}
	}
	if(tr.childNodes==3){
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
console.log(container);
}