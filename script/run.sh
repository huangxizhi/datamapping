#!/bin/bash
set -x
jarfile=datamapping.jar
taskclass=com.hellomyboy.Main

basedir=`pwd`
orig_file=$basedir/audio_url.list

# 配置参数
line_per_genfile=1000000

# 传入java程序的参数
infileDir="$basedir/01_input"
outfileDir="$basedir/02_output"
outfileSum=50
outfilePrefix="data_"
outfileSuffix=".list"
writerSum=`cat /proc/cpuinfo |grep processor | wc -l`

# 分割文件
echo "split input file..."
mkdir $infileDir $outfileDir -p && cd $infileDir && rm * -rf
split -a 3 -d -l $line_per_genfile $orig_file data_
readerSum=`ls | wc -l`
cd $basedir

# 映射文件
echo "run java, put data to bucket"
params="-infileDir $infileDir -outfileDir $outfileDir -outfileSum $outfileSum -outfilePrefix $outfilePrefix -outfileSuffix $outfileSuffix -readerSum $readerSum -writerSum $writerSum"
cd output
java -cp $jarfile com.hellomyboy.Main $params > nohup.log 2>err.log
cd ..

echo "over...."

