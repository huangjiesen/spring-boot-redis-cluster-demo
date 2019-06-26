#!/bin/bash
# 创建集群
docker exec -i redis_7001 \
redis-cli --cluster-replicas 1 \
--cluster create \
192.168.2.45:7001 \
192.168.2.45:7002 \
192.168.2.45:7003 \
192.168.2.45:7004 \
192.168.2.45:7005 \
192.168.2.45:7006

echo "集群创建success,别忘记修改配置文件设置密码哦！！！"