#!/bin/bash

JARS_LOCATION="../cli-app/build/libs-all"
MAIN_CLASS="org.openmuc.jmbus.app.ConsoleApp"
SYSPROPS=""
PARAMS=""

# from gradle start script:
# Attempt to set SCRIPT_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
SCRIPT_HOME="`pwd -P`"
cd "$SAVED" >/dev/null


CLASSPATH=$(JARS=("$SCRIPT_HOME"/"$JARS_LOCATION"/*.jar); IFS=:; echo "${JARS[*]}")

for i in $@; do 
    if [[ $i == -D* ]]; then
	    SYSPROPS="$SYSPROPS $i";
    else
	    PARAMS="$PARAMS $i";
    fi
done

java $SYSPROPS -cp $CLASSPATH $MAIN_CLASS $PARAMS
