#!/bin/bash

#仅用于测试
#It is single mode instead of cluster mode

#start zk if not started
zk_status=$(sudo ./Zookeeper/bin/zkServer.sh status)
if [[ $zk_status =~ "Error contacting service" ]]; then
    echo "======>:starting zk server..."
    sudo ./Zookeeper/bin/zkServer.sh start
fi
#stop zk
#sudo ./Zookeeper/bin/zkServer.sh stop

#start kafka if not started
kafka_status=$(ps ax | grep -i 'kafka\.Kafka' | grep java | grep -v grep | awk '{print $1}')
if [ -z "$kafka_status" ]; then
    echo "======>:starting kafka broker..."
    sudo ./Kafka/bin/kafka-server-start.sh -daemon ./Kafka/config/server.properties
    #wait for kafka broker
    sleep 3s
    #create topics if not exists
    sudo ./Kafka/bin/kafka-topics.sh --if-not-exists --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic test
    sudo ./Kafka/bin/kafka-topics.sh --if-not-exists --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic topic-service-resp
    sudo ./Kafka/bin/kafka-topics.sh --if-not-exists --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic topic-service
    sudo ./Kafka/bin/kafka-topics.sh --if-not-exists --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic topic-im-resp
    sudo ./Kafka/bin/kafka-topics.sh --if-not-exists --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic topic-im
fi
#stop kafka
#sudo ./Kafka/bin/kafka-server-stop.sh

#build java project
mvn clean package -Dmaven.test.skip=ture

#restart server modules in single mode
modules=("Dispatcher" "TcpServer" "BaseService")
for module in ${modules[@]}; do
    status=$(ps ax | grep nohup | grep ${module} | grep java | grep -v grep | awk '{print $1}')
    if [ -n "$status" ]; then
        echo "======>:killing ${module}"
        sudo kill -s TERM $status
    fi
    echo "======>:starting ${module}"
    sudo nohup java -jar ./${module}/target/${module}-1.0.0-jar-with-dependencies.jar >/dev/null 2>&1 &
done