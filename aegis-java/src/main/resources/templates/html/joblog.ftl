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
 <h1>Netpro任务日志</h1>
<table class="altrowstable" id="alternatecolor">
<tr>
    	<th>作业ID</th>
    	<th>作业名称</th>
    	<th>备份类型</th>
    	<th>代理类型</th>
    	<th>级别</th>
		<th>开始时间</th>
		<th>结束时间</th>
		<th>客户端</th>
		<th>介质池</th>
		<th>文件数</th>
		<th>字节数</th>
    	<th>状态</th>
</tr>
<tr>
    <td>${info.jobId!}</td>
    <td>${info.name!}</td>
    <td>${info.type!}</td>
    <td>${info.agentType!}</td>
    <td>${info.level!}</td>    
    <td>${(info.startTime?string("yyyy-MM-dd HH:mm:ss"))!}</td>
	<td>${(info.endTime?string("yyyy-MM-dd HH:mm:ss"))!}</td>
	<td>${info.clientName!}</td>
	<td>${info.poolName!}</td>
	<td>${info.jobFiles!}</td>
	<td>${fileSize!}</td>
	<td>${info.jobStatus!}</td>
</tr>
</table>
<br/>
<table class="altrowstable" width="800">
<tr class="evenrowcolor">
    	<th>作业日志</th>
</tr>
<tr>
    <td>
    <#list list as p>
    	${p.logText!''}<br/>
    </#list>
    </td>
</tr>
</table>
</body>
<html>