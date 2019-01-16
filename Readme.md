# Concurrent - Sayurbox



## Tech
Uses a number of open source projects to work properly:

* [Kafka] - is used for building real-time data pipelines
* [Java 8] 
* [Spring Boot] 

## Installation

You need to have [Kafka], Maven and [Java 8]  installed.
Java 8 should be used for building in order to support both [Java 8]  at runtime.
in order to install kafka you can check this [Kafka Documentation]

### Start Zookeeper
Kafka uses ZooKeeper so you need to first start a ZooKeeper server if you don't already have one. You can use the convenience script packaged with kafka to get a quick-and-dirty single-node ZooKeeper instance.

Windows:
```sh
> bin/windows/zookeeper-server-start.bat config/zookeeper.properties
```
Linux:
```sh
> bin/zookeeper-server-start.sh config/zookeeper.properties
```
### Start Kafka Server
Windows:
```sh
> bin/windows/kafka-server-start.bat config/server.properties
```
Linux:
```sh
> bin/kafka-server-start.sh config/server.properties
```

### Compile Jar Application
Make sure you have installed [Java 8] before start the application.
```sh
> mvn package clean install spring-boot:repackage
```

### Running Jar Application
Make sure you have installed [Java 8] before compile the code into jar applicaiton.
```sh
> cd Concurrent-SayurBox\target
```
```sh
> java -jar Concurrent-SayurBox-0.0.1-SNAPSHOT.jar
```

### Configure Database
In this  project, the database by default is using H2 Database so you can run the application without initiate database before. But if you  need to see the database scheme and value, you must comment the H2 Database configuration in properties   file and disable comment the MySql Database Configuration, but remember you have installed the  MySql  Databse before and create table new with **concurrent-sayurbox** name.

### Test Application

The testing scenario is based on case scenario:
1. Manda Select item Apel 2 and  Mangga  4.
 -in this schenario, (based  on image) i assume the select process is not in concurrent but Manda is doing it before.
```sh
curl -X POST \
  http://localhost:8080/transaction/select-item \
  -H 'Content-Type: application/json' \
  -d '{
    "userId": 1,
    "items": [
        {
            "id": 1,
            "amount": 2
        },
        {
            "id": 3,
            "amount": 4
        }
    ]
}'
```
2. Susan Select item Apel 5 and  Pepaya  1
    -in this  step actually the application is checked by realtime  the availibility of each  item that been selected by User, you can custom the response message whether it error message or not. By default i've set  up the response  is  not an error message, the error message is appear while the customer order the items.
```sh
curl -X POST \
  http://localhost:8080/transaction/select-item \
  -H 'Content-Type: application/json' \
  -d '{
	"userId":2,
	"items":[{
		"id":1,
		"amount":5
	},
	{
		"id":2,
		"amount":1
	}
		]
}'
```
3. Manda order the selected item
```sh
curl -X POST \
  http://localhost:8080/transaction/select-item \
  -H 'Content-Type: application/json' \
  -d '{
	"userId":2,
	"items":[{
		"id":1,
		"amount":5
	},
	{
		"id":2,
		"amount":1
	}
		]
}'
```
3. Susan order the selected item
```sh
curl -X POST \
  http://localhost:8080/transaction/select-item \
  -H 'Content-Type: application/json' \
  -d '{
	"userId":2,
	"items":[{
		"id":1,
		"amount":5
	},
	{
		"id":2,
		"amount":1
	}
		]
}'
```
in term of concurrent request order between Manda and Susan you can test it by [JMeter] or any other tool.


   [Kafka]: <https://kafka.apache.org/>
   [Kafka Documentation]: <https://kafka.apache.org/quickstart>
   [Java 8]: <https://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html>
   [Spring Boot]: <http://spring.io/projects/spring-boot>
   [JMeter]:<https://jmeter.apache.org/>
