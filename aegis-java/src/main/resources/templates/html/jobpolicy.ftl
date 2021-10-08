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
 <h1>Netpro备份策略</h1>
<table class="altrowstable" id="alternatecolor">
<tr>
   	<th>策略名称</th>
   	<th>类型</th>
   	<th>级别</th>
   	<th>代理类型</th>
   	<th>客户端</th>
   	<th>介质池</th>
   	<th>文件集</th>
   	<th>优先级</th>
   	<th>作业过期时间</th>
   	<th>最大允许时间</th>
   	<th>重试次数</th>
   	<th>重试间隔时间</th>
   	<th>Rman备份脚本</th>
   	<th>作业运行前脚本</th>
   	<th>作业运行后脚本</th>
   	<th>策略</th>
   	<th>备份集</th>
   	<th>状态</th>
</tr>
<#list list as p>
<tr>
    <td>${p.name!''}</td>
    <td>${p.type!''}</td>
    <td>${p.level!''}</td>
    <td>${p.agentType!}</td>
    <td>${p.clientName!}</td>
    <td>${p.poolName!}</td>
    <td>${p.fileSetName!}</td>
    <td>${p.priority!}</td>
    <td>${p.retention!}天</td>
    <td>${p.maxRunTime!}小时</td>
    <td>${p.rescheduleTimes!}次</td>
    <td>${p.rescheduleInterval!}分钟</td>
    <td>${p.rmanScript!}</td>
    <td>${p.runBeforeJob!}</td>
    <td>${p.runAfterJob!}</td>
    <td>${p.run!}</td>
    <td>${p.fileSetInclude!}</td>
    <td>${p.enabled!}</td>
</tr>
</#list>
</table>
</body>
<html>