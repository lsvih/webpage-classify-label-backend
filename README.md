# webpage-classify-label-backend
网页类型标注插件后端

## Environment
- java 1.7
- mysql

## Build
```
mvn clean
mvn package
```

## Run

Create database
```
mysql -uroot
create database webpage_label;
exit
```

Download [jar file HERE](https://github.com/lsvih/webpage-classify-label-backend/releases/latest)

```
java -jar webpage_label-1.0.0.jar --server.port=9999
```
