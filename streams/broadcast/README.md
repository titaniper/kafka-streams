# 로컬 환경
- Gradle 8.8
- openjdk 17.0.11

# Docker 
```
docker build -t broadcast-app --build-arg NODE_ENV=test --build-arg COMMIT_HASH=ereer . 
docker run -d --env-file .env --add-host=localhost:172.30.1.63 --name broadcast-app broadcast-app .

docker exec broadcast-app sh -c 'env | grep SECRET_KEY'

```

# Deploy
```
docker login  -u devjyk
docker push devjyk/streams-app
```


# 역할
## gradlew 및 gradlew.bat
이 두 파일은 Gradle Wrapper 스크립트입니다.

#### gradlew:
- Unix 계열 운영체제(Linux, macOS 등)에서 Gradle Wrapper를 실행하기 위한 스크립트입니다.
- 이 스크립트를 실행하면, 프로젝트에 필요한 Gradle 버전을 자동으로 다운로드하고 빌드 작업을 수행합니다.
- 명령어: ./gradlew <task>

#### gradlew.bat:
- Windows 운영체제에서 Gradle Wrapper를 실행하기 위한 배치 파일입니다.
- 역할은 gradlew와 동일하지만, Windows 환경에서 실행할 수 있도록 작성되었습니다.
- 명령어: gradlew.bat <task>

## settings.gradle.kts
역할:
- Gradle 프로젝트의 설정 파일로, 프로젝트의 구조와 설정을 정의합니다.
- 멀티 프로젝트 빌드에서 하위 프로젝트들을 포함시키기 위해 사용됩니다.
- build.gradle.kts 파일과 달리, 프로젝트의 전역 설정을 다룹니다.
- 주요 내용:
  - 프로젝트 이름 설정
  - 포함된 서브 프로젝트 리스트
  - 각종 플러그인 설정

## gradle/wrapper/gradle-wrapper.jar
역할:
- Gradle Wrapper 실행에 필요한 클래스 파일들을 포함한 JAR 파일입니다.
- gradlew 및 gradlew.bat 스크립트가 이 JAR 파일을 사용하여 Gradle Wrapper를 실행합니다.
- Gradle 버전 및 다운로드 경로를 정의하는 gradle-wrapper.properties 파일에 지정된 Gradle 배포판을 다운로드하고 실행합니다.

## gradle/wrapper/gradle-wrapper.properties
역할:
- Gradle Wrapper 설정 파일로, 사용될 Gradle 버전과 다운로드 URL을 지정합니다.
- Gradle Wrapper가 특정 버전을 다운로드하여 사용하도록 지시합니다.
주요 내용:
- distributionUrl: Gradle 배포판이 호스팅되는 URL. 여기에는 Gradle 버전이 포함됩니다.

## 
- app/build.gradle.kts 파일은 Gradle 빌드 스크립트로, 해당 프로젝트 모듈(app)에 대한 빌드 설정과 의존성을 정의합니다. 
- build.gradle.kts 파일은 Kotlin DSL을 사용하여 작성된 Gradle 빌드 스크립트로, Groovy DSL로 작성된 build.gradle 파일과 유사하지만, Kotlin 언어의 장점을 활용합니다.