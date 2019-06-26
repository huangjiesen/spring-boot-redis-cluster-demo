#!/bin/bash
if [ ! $1 ]
then
  echo -e "$0 <密码>"
  exit
fi

# 创建集群
for item in `seq 7001 7006`
do
  echo -e "\nmasterauth $1\nrequirepass $1" >> config/redis_${item}.conf
done
