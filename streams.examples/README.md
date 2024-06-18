# Docker 이미지 빌드
docker build -t my-kafka-streams-app .

# Docker 컨테이너 실행
docker run -d --name my-kafka-streams-app my-kafka-streams-app


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
$ mvn clean 
$ mvn package
$ mvn compile
$ mvn exec:java -Dexec.mainClass="myapps.DynamicPartitionTopologyForwarder" -Dexec.args="arg1 arg2"

$ mvn exec:java -Dexec.mainClass=myapps.DynamicPartitionTopologyForwarder
```
