Rem awrrpt_mayj.sql

set echo off;
set veri off;
set feedback off;
set termout on;
set heading off;

variable rpt_options number;

define NO_OPTIONS = 0;
define ENABLE_ADDM = 8;

rem according to your needs, the value can be 'text' or 'html'
variable dbid number;
variable inst_num number;
variable bid number;
variable eid number;
define report_type='text';
begin
  :rpt_options := &NO_OPTIONS;
  select dbid into :dbid from v$database;
  select instance_number into :inst_num from v$instance;
  select min(snap_id), max(snap_id) into :bid, :eid
    from (select snap_id from dba_hist_snapshot where instance_number = :inst_num order by snap_id desc) 
   where rownum < 3;
end;
/

column ext new_value ext noprint
column fn_name new_value fn_name noprint;
column lnsz new_value lnsz noprint;

select 'txt' ext from dual where lower('&report_type') = 'text';
select 'html' ext from dual where lower('&report_type') = 'html';
select 'awr_report_text' fn_name from dual where lower('&report_type') = 'text';
select 'awr_report_html' fn_name from dual where lower('&report_type') = 'html';
select '80' lnsz from dual where lower('&report_type') = 'text';

set linesize 80;
column report_name new_value report_name noprint;

select '/tmp/awr_'||:bid||'_'||:eid||'.txt' report_name from dual;
set termout off
spool &report_name;
select output from table(dbms_workload_repository.&fn_name(:dbid, :inst_num, :bid, :eid, :rpt_options ));
spool off;
set termout on;
clear columns sql;
ttitle off;
btitle off;
repfooter off;
undefine report_name
undefine report_type
undefine fn_name
undefine lnsz
undefine NO_OPTIONS
