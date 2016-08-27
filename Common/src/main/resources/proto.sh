#!/bin/bash

filelist=`ls`
current_path=$(pwd)
parent_path=${current_path%/*}
for filename in $filelist
do
    if [ "${filename##*.}" = "proto" ]; then
        ./protoc --proto_path=${current_path} --java_out=${parent_path}/java ${current_path}/$filename
    fi
done