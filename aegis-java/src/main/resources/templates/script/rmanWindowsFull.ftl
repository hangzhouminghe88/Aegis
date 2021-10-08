#full script
run {
<#assign nums=1..oracleChannels/>
<#list nums as num>
allocate channel mh${num} device type 'sbt_tape'
PARMS="ENV=(CATALOG_DIR=${rmanCatalogPath},OPERTYPE=SBTBACKUP)";
</#list>
BACKUP
    CURRENT CONTROLFILE;

BACKUP
	<#if oracleCompression==1>
    AS COMPRESSED BACKUPSET
	</#if>
    FILESPERSET 5
    DATABASE;
    <#if oracleRedoArchive ==1>
    sql 'alter system archive log current';
	</#if>
BACKUP
   filesperset 20
   ARCHIVELOG ALL;

BACKUP
   CURRENT CONTROLFILE;
<#list nums as num>
release channel mh${num};
</#list>}