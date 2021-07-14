# 디디고 ERP 업무 자동화 프로젝트

Selenium 을 이용한 웹 크롤링 프로젝트입니다.

## Tech

- Spring Boot
- Spring JPA
- Spring Batch
- Selenium
- Jxls
- Gradle  
- H2
- Vue

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

> 빌드 시 resources의 application.yaml 파일과 deploy 폴더는 제외 됩니다.

### Front-end
단일 페이지로 구성되어 있으며 위치는 ``/resources/static/index.html`` 입니다.

추후 화면 구성이 복잡해지면 단일 HTML 에서 Vue CLI 으로 전환을 고려해야 합니다.

### Database
해당 시스템을 이용하는 유저가 1~2명으로 매우 소수이기 때문에 H2 DB를 이용하고 있습니다.

추후 시스템이 복잡해지고 사용 유저수가 증가하면 다른 RDBMS로 전환을 고려해야 합니다.

## Deployment
> Selenium IE Driver 때문에 현재 배포는 Windows 만을 대상으로 하고 있습니다.

배포 경로는 ``C:\excel\`` 입니다.

1. 프로젝트 내 ``resources/deploy`` 폴더를 배포 경로에 넣습니다.
2. 빌드 된 jar 파일을 ``convert.jar`` 으로 이름 변경 후 배포 경로에 넣습니다.
3. ``application.yaml`` 파일의 설정 내용을 확인 합니다.
4. 실행 ``convert.bat``
5. 사이트 접속 확인 http://localhost:8080
