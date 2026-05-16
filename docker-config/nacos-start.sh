docker run -d \
  --name nacos \
  -p 8848:8848 \
  -p 9848:9848 \
  -p 9849:9849 \
  --privileged=true \
  --network mynet \
  -e JVM_XMS=256m \
  -e JVM_XMX=256m \
  -e MODE=standalone \
  -e SPRING_DATASOURCE_PLATFORM=mysql \
  -e MYSQL_SERVICE_HOST=mysql \
  -e MYSQL_SERVICE_PORT=3306 \
  -e MYSQL_SERVICE_DB_NAME=nacos-config \
  -e MYSQL_SERVICE_USER=nacos-config \
  -e MYSQL_SERVICE_PASSWORD=123456 \
  -v /var/nacos/logs/:/home/nacos/logs \
  -v /var/nacos/conf/:/home/nacos/conf/ \
  --restart=always \
  nacos/nacos-server:v2.3.1

