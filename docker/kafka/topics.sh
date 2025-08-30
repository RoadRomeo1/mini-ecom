#!/usr/bin/env bash
set -e
kafka-topics --bootstrap-server localhost:9092 --create --if-not-exists --topic
order-events --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server localhost:9092 --create --if-not-exists --topic
payment-events --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server localhost:9092 --create --if-not-exists --topic
inventory-events --partitions 3 --replication-factor 1
