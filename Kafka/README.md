- Kafka
- 版本：kafka_2.11-0.10.0.0
- 创建主题: 
1.bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic test
2.bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic topic-service-resp
3.bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic topic-account
4.bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic topic-im
- 前台启动：sudo bin/kafka-server-start.sh config/server.properties
- 后台启动：sudo bin/kafka-server-start.sh -daemon config/server.properties