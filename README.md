# 디디고 ERP 업무 자동화 프로젝트

Selenium 을 이용한 웹 크롤링 프로젝트입니다.

## Tech

- Spring Boot
- Spring JPA
- Spring Batch
- Selenium
- Jxls
- Gradle
- MySQL
- Vue
- yarn

## Process

1. 디디고 ERP 에서 품목 정보와 거래처 정보를 수집합니다.
2. 자동화 대상 사이트(디디고 몰, KD ERP, Cozy) 에서 데이터를 수집 합니다.
3. 각 수집된 데이터를 조합하여 디디고 ERP 데이터 형식에 맞게 변환합니다.
4. 디디고 ERP에 변환된 Excel 파일을 업로드 합니다.
5. 작업에 사용된 파일들을 백업합니다.

## Development

이 프로젝트는 JDK 11 에서 개발되었습니다.

### Build

```sh
$ ./gradlew bootJar
```

or use IDE Gradle Plugin

> 빌드 시 resources의 deploy 폴더는 제외 됩니다.

### Excel Convert

Jxls를 이용하여 수행 됩니다.

변환에 필요한 템플릿과 매핑 파일은 ``/resources/deploy/template/`` 에 있습니다.

### Automations

Selenium을 이용하여 수행 됩니다.

브라우저 드라이버는 ``/resources/deploy/driver/`` 에 있습니다.

드라이버는 서버 브라우저 환경에 맞게 구성해야 합니다.

### Front-end
Vue 폴더에 소스가 위치하고 있으며, 빌드시 ``/resources/static`` 경로에 파일이 생성 됩니다.

### Database
192.168.11.158 서버의 MySql 사용하고 있습니다.

## Deployment

> Selenium IE Driver 때문에 현재 배포는 Windows 만을 대상으로 하고 있습니다.

배포 경로는 ``C:\excel\`` 입니다.

1. 프로젝트 내 ``resources/deploy`` 폴더를 배포 경로에 넣습니다.
2. 빌드 된 jar 파일을 ``convert.jar`` 으로 이름 변경 후 배포 경로에 넣습니다.
3. ``application.yaml`` 파일의 설정 내용을 확인 합니다.
4. 실행 ``convert.bat``
5. 사이트 접속 확인 http://localhost:8080
 
