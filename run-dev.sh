#!/bin/bash

#仅用于测试
set -e

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
    sudo ./Kafka/bin/kafka-topics.sh --if-not-exists --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic topic-account
    sudo ./Kafka/bin/kafka-topics.sh --if-not-exists --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic topic-im
fi

#build java project
#mvn clean package -Dmaven.test.skip=ture

#start dispatcher
#need not start when dev

#start tcp server
tcpserver_status=$(ps ax | grep nohup | grep -i 'TcpServer' | grep java | grep -v grep | awk '{print $1}')
if [ -n "$tcpserver_status" ]; then
    echo "======>:killing tcp server"
    sudo kill -s TERM $tcpserver_status
fi
echo "======>:starting tcp server"
sudo nohup java -jar ./TcpServer/target/TcpServer-1.0.0.jar >/dev/null 2>&1 &

#start account service
account_status=$(ps ax | grep nohup | grep -i 'AccountService' | grep java | grep -v grep | awk '{print $1}')
if [ -n "$account_status" ]; then
    echo "======>:killing account service"
    sudo kill -s TERM $account_status
fi
echo "======>:starting account service"
sudo nohup java -jar ./AccountService/target/AccountService-1.0.0.jar >/dev/null 2>&1 &

#start im service
im_status=$(ps ax | grep nohup | grep -i 'IMService' | grep java | grep -v grep | awk '{print $1}')
if [ -n "$im_status" ]; then
    echo "======>:killing im service"
    sudo kill -s TERM $im_status
fi
echo "======>:starting im service"
sudo nohup java -jar ./IMService/target/IMService-1.0.0.jar >/dev/null 2>&1 &
