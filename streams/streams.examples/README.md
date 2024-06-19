# Docker 이미지 빌드
```
# 테스트용이라 별 게 다 있따.
docker build --build-arg KAFKA_BROKER=localhost:9092 --build-arg KAFKA_TOPIC=mytopic -t myapp .
```

# Docker 컨테이너 실행
```
docker container rm myapp
docker run -d --add-host=localhost:172.30.1.63 --name myapp myapp
```


# mvn
- clean: 빌드된 파일을 삭제합니다.
- validate: 프로젝트 구조가 유효한지 확인합니다.
- compile: 소스 코드를 컴파일합니다.
- test: 테스트 코드를 컴파일하고 테스트를 실행합니다.
- package: 컴파일된 코드를 패키징합니다 (예: JAR, WAR).
- verify: 추가적인 검증 작업을 실행합니다.
- install: 패키지를 로컬 Maven 저장소에 설치합니다.
- site: 프로젝트 문서를 생성합니다.
- deploy: 패키지를 원격 저장소에 배포합니다.

```
$ mvn clean package exec:java
$ mvn exec:java -Dexec.mainClass="myapps.DynamicPartitionTopologyForwarder" -Dexec.args="arg1 arg2"
$ mvn exec:java -Dexec.mainClass=myapps.DynamicPartitionTopologyForwarder
```

# 환경 변수 
```
export KAFKA_BROKER="localhost:9092"
export KAFKA_TOPIC="my_topic"

unset KAFKA_BROKER KAFKA_TOPIC
```