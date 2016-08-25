#!/bin/bash

#仅用于测试
#stop zk
#sudo ./Zookeeper/bin/zkServer.sh stop
#stop kafka
#sudo ./Kafka/bin/kafka-server-stop.sh

#start zk if not started
zk_status=$(sudo ./Zookeeper/bin/zkServer.sh status)
if [[ $zk_status =~ "Error contacting service" ]]; then
    echo "======>:starting zk server..."
    sudo ./Zookeeper/bin/zkServer.sh start
fi

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
fi

#build java project
mvn clean package -Dmaven.test.skip=ture

#start dispatcher
dispatcher_status=$(ps ax | grep nohup | grep -i 'Dispatcher' | grep java | grep -v grep | awk '{print $1}')
if [ -n "$dispatcher_status" ]; then
    echo "======>:killing dispatcher server"
    sudo kill -s TERM $dispatcher_status
fi
echo "======>:starting dispatcher server"
sudo nohup java -jar ./Dispatcher/target/Dispatcher-1.0.0-jar-with-dependencies.jar >/dev/null 2>&1 &

#start tcp server
tcpserver_status=$(ps ax | grep nohup | grep -i 'TcpServer' | grep java | grep -v grep | awk '{print $1}')
if [ -n "$tcpserver_status" ]; then
    echo "======>:killing tcp server"
    sudo kill -s TERM $tcpserver_status
fi
echo "======>:starting tcp server"
sudo nohup java -jar ./TcpServer/target/TcpServer-1.0.0-jar-with-dependencies.jar >/dev/null 2>&1 &

#start service
common_status=$(ps ax | grep nohup | grep -i 'CommonService' | grep java | grep -v grep | awk '{print $1}')
if [ -n "$common_status" ]; then
    echo "======>:killing common service"
    sudo kill -s TERM $common_status
fi
echo "======>:starting common service"
sudo nohup java -jar ./CommonService/target/CommonService-1.0.0-jar-with-dependencies.jar >/dev/null 2>&1 &