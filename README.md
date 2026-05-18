# 项目结构介绍


# 服务器环境配置

## docker-config

### docker配置文件相关，目录/etc/docker
[daemon.json(docker的镜像源文件)](docker-config/daemon.json)

### docker设置网络
docker network create mynet


### mysql

#### docker拉取mysql
docker pull mysql:5.7

#### 修改mysql配置
[my.conf](docker-config/my.conf)

#### docker启动mysql命令
[mysql-start.sh](docker-config/mysql-start.sh)


## nacos

### 拉取nacos
docker pull nacos/nacos-server:v2.3.1

### 创建数据库和表
[docker-nacos-mysql.sql](docker-config/docker-nacos-mysql.sql)

### 修改配置文件
[nacos.conf](docker-config/nacos.conf)

### 启动nacos
[nacos-start.sh](docker-config/nacos-start.sh)

## redis

### 拉取redis
docker pull redis:7.2 

### 修改配置文件
[redis.conf](docker-config/redis.conf)

### 启动redis
[redis-start.sh](docker-config/redis-start.sh)
