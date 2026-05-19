docker run \
        -d \
        -p 6379:6379 \
        --privileged=true \
        --network mynet \
        --name redis \
        -v /var/redis/redis.conf:/etc/redis/redis.conf \
        -v /var/redis/data:/data \
        -v /var/redis/redis.log:/etc/redis.log \
        redis:7.2 \
        redis-server /etc/redis/redis.conf \