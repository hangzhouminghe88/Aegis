#!/bin/ksh
# function:get statpack report
# usage: crontab on aix&linux
# 2005-12-27 add comment and script standardization

DATE=`date +%Y-%m-%d`
TIME=`date +%H-%M-%S`

#start
do

#AWRDIR='/usr/local/apache/htdocs/aegis/public/awr/'
AWRDIR='/tmp/'
ADMDIR='/adm/'
OUTFILE=$SERVER'_'$DATE'_'$TIME.txt
OUTPUT=$ADMDIR$OUTFILE

SNAPFILE='/tmp/sqlmon/snapshot_'$SERVER'.lst'

<< EOF
set echo off
set feedback off
set heading off
set pagesize 0
set linesize 121
set trimspool on

var bst varchar2(32);
var est varchar2(32);
var bid number;
var eid number;
var awr varchar2(99);

begin
  select min(snap_id), max(snap_id),
         to_char(max(begin_interval_time), 'yyyy-mm-dd hh24:mi:ss'),
         to_char(max(end_interval_time), 'yyyy-mm-dd hh24:mi:ss') 
     into :bid, :eid, :bst, :est
    from (select snap_id, begin_interval_time, end_interval_time
            from v\$instance i, dba_hist_snapshot s
           where begin_interval_time >= sysdate - 60/360
            and i.instance_number = s.instance_number
            order by snap_id desc)
   where rownum < 3;
select 'awr_'||:bid||'_'||:eid||'.txt' into :awr from dual;
end;
/
spool $SNAPFILE
col val format a120 newline
select :bst||'@'||:est||'@'||'$SERVER'||'_'||:awr||'@'||'$SERVER'||'@'||:bid||'@'||:eid||'@'||'$SERVER' val
  from dual;
spool off

@/var/zyh/aegis/snapshot.sql

spool $OUTPUT
set serveroutput on size 1000000
set long 1000000
set pagesize 0
select dbms_advisor.get_task_report(t.task_name, 'TEXT', 'ALL', 'ALL', 'SYS') as ADDM_report
  from dba_advisor_tasks t
 where exists (select 1
                 from dba_advisor_recommendations r
                where t.task_id = r.task_id)
   and t.task_id = (select max(t.task_id)
                      from dba_advisor_tasks t
                     where t.advisor_name = 'ADDM')
   and t.status = 'COMPLETED';
spool off

EOF

AWRFNAME=`sed 's/'$SERVER'_//g' $SNAPFILE |grep @| cut -f3 -d @`
echo /bin/mv $AWRDIR$AWRFNAME $AWRDIR$SERVER'_'$AWRFNAME
/bin/mv $AWRDIR$AWRFNAME $AWRDIR$SERVER'_'$AWRFNAME

mysql -uroot -pjaguar -hlocalhost aegis2 <<EOF
  load data infile '$SNAPFILE' into table snapshot fields terminated by "@" enclosed by ''
   (create_time, end_time, report_name, db_name, begin_snap, end_snap, service);
EOF

cnt=`cat $OUTPUT|wc -l`
if [ $cnt -gt 9 ]; then
mysql -uroot -pjaguar -hlocalhost aegis2 <<EOF
  update snapshot
    set addm_rpt_name = '$OUTFILE' 
   where db_name = '$SERVER' and end_time > now() - interval 30 minute;
EOF
else
/bin/rm $OUTPUT
fi

done

#sh /usr/local/apache/htdocs/aegis/app/scripts/sqlstat.sh > /tmp/sqlmon/c1c_sqlstat.log
#/usr/local/php/bin/php /usr/local/apache/htdocs/aegis/app/scripts/rrdstat.php > /tmp/sqlmon/rrdstat.log
