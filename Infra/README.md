# MySpringProjects
Contains all my sample projects

Root Directory - ~/GitHub/sumantabiswas2/MySpringProjects

https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax


## Install Neo4j

1. Root for all docker command - ~/GitHub/sumantabiswas2
2. Running All commands from ~/GitHub/sumantabiswas2
3. Create a data folder under ~/GitHub/sumantabiswas2/datafolder/neo4j-data-folder


--- Neo4j

```
docker run \
    --name neo4j \
    -p 7474:7474 -p 7687:7687 \
    -e NEO4J_AUTH=neo4j/adminadmin \
    -v ~/GitHub/sumantabiswas2/datafolder/neo4j-data-folder:/data \
    -d \
    neo4j:5.26
```


```
docker run \
    --name mongodb \
    -p 27017:27017 \
    -e MONGO_INITDB_ROOT_USERNAME=admin \
    -e MONGO_INITDB_ROOT_PASSWORD=adminadmin \
    -v ~/GitHub/sumantabiswas2/datafolder/mongodb-data-folder:/data/db \
    -d \
    mongo:8.3
```


Installed My SQL workbench

```
docker run \
    --name mysql \
    -p 3306:3306 \
    -e MYSQL_ROOT_PASSWORD=adminadmin \
    -v ~/GitHub/sumantabiswas2/datafolder/mysql-data-folder:/var/lib/mysql \
    -d \
    mysql:9.7.0
```


install Redis

```
docker run \
    --name redis \
    -p 6379:6379 \
    -v ~/GitHub/sumantabiswas2/datafolder/redis-data:/data \
    -v ~/GitHub/sumantabiswas2/datafolder/redis-backups:/backups \
    -d \
    redis:alpine \
    redis-server \
    --appendonly yes \
    --appendfsync everysec \
    --save 900 1 \
    --save 300 10 \
    --save 60 10000 \
    --dir /data \
    --dbfilename dump.rdb \
    --appendfilename appendonly.aof

```