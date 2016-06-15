#!/bin/bash
jarfile=datamapping.jar
mvn clean
mvn package

mkdir output -p
if [ -f output/$jarfile ]; then
	rm output/$jarfile -rf
fi
cp target/datamapping-0.0.1-SNAPSHOT-jar-with-dependencies.jar output/$jarfile
