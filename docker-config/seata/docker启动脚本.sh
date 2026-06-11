docker run -d \
  --name seata-server \
  --network mynet \
  -p 8091:8091 \
  -p 7091:7091 \
  -e SEATA_IP=192.168.2.20 \
  -e SEATA_PORT=8091 \
  -v /var/seata/config:/seata-server/resources \
  -v /var/seata/sessionStore:/seata-server/sessionStore \
  seataio/seata-server:1.5.2