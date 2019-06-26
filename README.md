# redis sentinel(哨兵)、clustr(集群)的应用示例
* 使用docker-compose进行redis节点服务编排
* 基于spring boot 2.x 的`jedis`及`lettuce`两种不同的连接池配置示例
* 哨兵模式的多数据源配置示例

## redis for docker
```
 docker
 ├── redis-cluster
 │   ├── config
 │   │   ├── redis_7001.conf
 │   │   ├── redis_7002.conf
 │   │   ├── redis_7003.conf
 │   │   ├── redis_7004.conf
 │   │   ├── redis_7005.conf
 │   │   └── redis_7006.conf
 │   ├── create_cluster.sh
 │   ├── docker-compose.yml
 │   └── setpass.sh
 └── redis-sentinel
     ├── config
     │   ├── redis_1.conf
     │   ├── redis_2.conf
     │   ├── redis_3.conf
     │   ├── sentinel_1.conf
     │   ├── sentinel_2.conf
     │   └── sentinel_3.conf
     └── docker-compose.yml
```