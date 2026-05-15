#mysql5.7
#命令：
docker run \
-d \
-p 3306:3306 \
--privileged=true \
-v /var/mysql/log:/var/log/mysql \
-v /var/mysql/data:/var/lib/mysql \
-v /var/mysql/conf.d:/etc/mysql/conf.d \
-e MYSQL_ROOT_PASSWORD=123456 \
--name mysql \
mysql:5.7