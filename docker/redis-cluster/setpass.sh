#!/bin/bash
if [ ! $1 ]
then
  echo -e "$0 <密码>"
  exit
fi

# 将密码写到各配置文件
for item in `seq 7001 7006`
do
  echo -e "\nmasterauth $1\nrequirepass $1" >> config/redis_${item}.conf
done
