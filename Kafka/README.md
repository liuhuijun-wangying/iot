- Kafka
- 版本：kafka_2.11-0.10.0.0
 
- 前台启动：sudo bin/kafka-server-start.sh config/server.properties
- 后台启动：sudo bin/kafka-server-start.sh -daemon config/server.properties

- 第一次启动,需要创建主题:
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic test
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic topic-service-resp
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic topic-account
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic topic-im