# 메시지 국제화 정리
### - Spring에서 메시지와 국제화를 쉽게 관리할 수 있는 기능을 제공
- 메시지와 국제화는 다국어 지원 애플리케이션을 개발 가능케 함.

## 1. 메시지 소스 설정
- Spring에서 메시지를 관리하기 위해 MessageSource 인터페이스를 사용
- 일반적으로 ResourceBundelMessageSource를 빈으로 설정하여 사용

### 예) application.properties 설정

- properties
```properties
spring.messages.basename=message s
spring.messages.encoding=UTF-8
```
- Java
```java
@Configuration
public class AppConfig {
    
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
```

## 2. 메시지 파일작성
- 메시지 파일은 기본적으로 'src/main/resources' 디렉토리에 위치하며, 각 언어별로 별도의 파일을 작성

- 예) messages.properties (기본)
```properties
hello=Hello
goodbye=Goodbye
```
- 예시) messages_ko.properties (한국어)
```properties
hello=안녕
goodbye=안녕히 가세요
```
## 3. 메시지 사용
- 코드에서 메시지를 사용하기 위해 MessageSource를 주입받아 사용한다.

-Java (서비스에서 메시지 사용)
```java
@Service
public class MessageService {

    @Autowired
    private MessageSource messageSource;

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.getDefault());
    }

    public String getMessage(String code, Locale locale) {
        return messageSource.getMessage(code, null, locale);
    }
}
```

## 4. 컨트롤러에서 메시지 사용
- 컨트롤러에서 메시지를 사용하여 국제화된 응답을 제공
- Java(컨트롤러에서 사용)
```java
@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/hello")
    public String hello(@RequestParam(name = "lang", required = false) String lang) {
        Locale locale = lang != null ? new Locale(lang) : Locale.getDefault();
        return messageSource.getMessage("hello", null, locale);
    }
}
```

## 5. 웹 애플리케이션에서의 메시지
- Spring MVC에서는 'LocaleResolver'를 사용하여 사용자 요청에 따른 적절한 로케일 설정이 가능
- 일반적으로 'SessionLocaleResolver' 또는 'AcceptHeaderLocaleResolver'를 사용

- 예) Locale 설정

```java
import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /* LocaleResolver Bean을 정의
     * 여기서는 AcceptHeaderLocaleResolver를 사용하여, 사용자의 Accept-Language 헤더에 따라 로케일 결정
     * */
    @Bean
    public LocaleResolver localeResolver() {
        // 기본 로케일을 한국으로 설정
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.KOREA);
        return localeResolver;
    }

    /* LocaleChangeInterceptor 빈을 정의. 
     * 인터셉터는 요청 파라미터를 통해 로케일을 변경할 수 있도록 함.
     * */
    
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        // lang이라는 파라미터를 통해 로케일을 변경할 수 있도록 설정
        interceptor.setParamName("lang");
        return interceptor;
    }
    // 정의된 인터셉터를 Spring MVC 인터셉터 체인에 추가
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
```

## 구성 클래스 설명 정리
### AcceptHeaderLocaleResolver
- 사용자의 HTTP 요청 헤더 중 'Accept-Language' 헤더를 기반으로 로케일을 결정
- 기본 로케일을 설정할 수 있으며, 사용자가 지정한 로케일을 자동 감지 설정

### LocaleChangeInterceptor
- 특정 요청 파라미터(기보적으로 lang)를 통해 로케일을 변경할 수 있도록함.
- 예를 들어, `?lang=ko`와 같은 요청을 통해 로케일을 한국어로 변경할 수 있음

### 요청 예시
- 기본 로케일: 'http://localhost:8080/api/hello'
    - 사용자의 Accept-Language 헤더가 없으면 기본 로케일이 사용된다.
- 로케일 변경: 'http://localhost:8080/api/hello?lang=ko'
    - lang=ko 파라미터를 통해 로케일을 한국어로 변경