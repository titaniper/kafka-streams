# 로컬 환경
- Gradle 8.8
- openjdk 17.0.11

# Docker 
```
docker build -t streams-app .
docker run -d --env-file .env --add-host=localhost:172.30.1.63 --name streams-app streams-app
```

