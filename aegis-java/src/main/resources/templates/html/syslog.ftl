<html>
<head>

<!-- Javascript goes in the document HEAD -->
<script type="text/javascript">
function altRows(id){
    if(document.getElementsByTagName){ 
         
        var table = document.getElementById(id); 
        var rows = table.getElementsByTagName("tr");
          
        for(i = 0; i < rows.length; i++){         
            if(i % 2 == 0){
                rows[i].className = "evenrowcolor";
            }else{
                rows[i].className = "oddrowcolor";
            }     
        }
    }
}
 
window.onload=function(){
    altRows('alternatecolor');
}
</script>
 
 
<!-- CSS goes in the document HEAD or added to your external stylesheet -->
<style type="text/css">
body{text-align:center}
table.altrowstable {
    font-family: Microsoft YaHei;verdana,arial,sans-serif;
    font-size:11px;
    color:#333333;
    border-width: 1px;
    border-color: #a9c6c9;
    border-collapse: collapse;
    margin:0 auto
}
table.altrowstable th {
    border-width: 1px;
    padding: 8px;
    border-style: solid;
    border-color: #a9c6c9;
}
table.altrowstable td {
    border-width: 1px;
    padding: 8px;
    border-style: solid;
    border-color: #a9c6c9;
}
.oddrowcolor{
    background-color:#d4e3e5;
}
.evenrowcolor{
    background-color:#c3dde0;
}
</style>
</head>
 <body>
 <h1>Netpro系统日志${month!}</h1>
<table class="altrowstable" id="alternatecolor">
<tr>
    <#list headers as value>
    	<th>${value!''}</th>
	</#list>
</tr>
<#list list as p>
<tr>
    <td>${p.userName!''}</td>
    <td>${p.daemonName!''}</td>
    <td>${p.logText!''}</td>
    <td>${(p.time?string("yyyy-MM-dd HH:mm:ss"))!}</td>
    <td>${p.actResult!''}</td>
</tr>
</#list>
</table>
</body>
<html>