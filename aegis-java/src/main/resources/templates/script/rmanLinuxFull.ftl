#! /bin/sh

# ---------------------------------------------------------------------------
# Determine the user which is executing this script.
# ---------------------------------------------------------------------------

CUSER=`id |cut -d"(" -f2 | cut -d ")" -f1`

# ---------------------------------------------------------------------------
# Log the start of this script.
# ---------------------------------------------------------------------------

echo Script $0
echo ==== started on `date` ====
echo

# ---------------------------------------------------------------------------
# Replace /db/oracle/product/ora81, below, with the Oracle home path.
# ---------------------------------------------------------------------------

ORACLE_HOME=${oracleHome}
export ORACLE_HOME

# ---------------------------------------------------------------------------
# Replace ora81, below, with the Oracle SID of the target database.
# ---------------------------------------------------------------------------

ORACLE_SID=${oracleSid}
export ORACLE_SID

# ---------------------------------------------------------------------------
# Replace ora81, below, with the Oracle DBA user id (account).
# ---------------------------------------------------------------------------

ORACLE_USER=oracle

# ---------------------------------------------------------------------------
# Set the target connect string.
# Replace "sys/manager", below, with the target connect string.
# ---------------------------------------------------------------------------

TARGET_CONNECT_STR=${oracleTargetConnect}

# ---------------------------------------------------------------------------
# Set the Oracle Recovery Manager name.
# ---------------------------------------------------------------------------

RMAN=$ORACLE_HOME/bin/rman

# ---------------------------------------------------------------------------
# Print out the value of the variables set by this script.
# ---------------------------------------------------------------------------

echo
echo   "RMAN: $RMAN"
echo   "ORACLE_SID: $ORACLE_SID"
echo   "ORACLE_USER: $ORACLE_USER"
echo   "ORACLE_HOME: $ORACLE_HOME"


echo  "ORACLE FULL BACKUP"

# ---------------------------------------------------------------------------
# NOTE: This script assumes that the database is properly opened. If desired,
# this would be the place to verify that.
# ---------------------------------------------------------------------------

echo


echo "Full backup requested"
BACKUP_TYPE="INCREMENTAL LEVEL=0"



# ---------------------------------------------------------------------------

CMD_STR="
ORACLE_HOME=$ORACLE_HOME
export ORACLE_HOME
ORACLE_SID=$ORACLE_SID
export ORACLE_SID
$RMAN target $TARGET_CONNECT_STR nocatalog << EOF
RUN {
<#assign nums=1..oracleChannels/>
<#list nums as num>
ALLOCATE CHANNEL mh${num} DEVICE TYPE 'sbt_tape' 
PARMS='ENV=(CATALOG_DIR=${rmanCatalogPath},ORACLE_SID=${oracleSid},OPERTYPE=SBTBACKUP)';
</#list>

BACKUP
    CURRENT CONTROLFILE;

BACKUP
	<#if oracleCompression == 1>
	AS COMPRESSED BACKUPSET
	</#if>
    $BACKUP_TYPE
    SKIP INACCESSIBLE
    TAG hot_db_bk_level0
    FILESPERSET 5
    DATABASE PLUS ARCHIVELOG;

BACKUP
    CURRENT CONTROLFILE;

DELETE NOPROMPT ARCHIVELOG ALL COMPLETED BEFORE 'SYSDATE-7';

<#assign nums=1..oracleChannels/>
<#list nums as num>
RELEASE CHANNEL mh${num};
</#list>

}
EOF
"

# Initiate the command string

if [ "$CUSER" = "root" ]
then
    su - $ORACLE_USER -c "$CMD_STR"
    RSTAT=$?
else
    /bin/sh -c "$CMD_STR"
    RSTAT=$?
fi

# ---------------------------------------------------------------------------

if [ "$RSTAT" = "0" ]
then
    LOGMSG="ended successfully"
else
    LOGMSG="ended in error"
fi

echo
echo Script $0
echo ==== $LOGMSG on `date` ====
echo

exit $RSTAT

