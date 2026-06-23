cd xxl-job
docker-compose up -d

# 观察日志，等出现 "Started XxlJobAdminApplication" 就成功了
docker logs -f xxl-job-admin