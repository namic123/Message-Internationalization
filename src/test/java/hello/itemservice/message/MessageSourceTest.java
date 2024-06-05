package hello.itemservice.message;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class MessageSourceTest {

    @Autowired
    MessageSource messageSource;

    // 메세지 소스 설정
    @Test
    void helloMessage(){
        String result = messageSource.getMessage("hello", null, null);
        assertThat(result).isEqualTo("안녕");
    }

    // 메세지 없는 경우, 예외 처리
    @Test
    void notFoundMessageCode(){
        assertThatThrownBy(()-> messageSource.getMessage("no_code",null,null))
                .isInstanceOf(NoSuchMessageException.class);
    }

    // 메세지 없는 경우, 기본값 설정
    @Test
    void notFoundMessageCodeDefaultMessage(){
        String result = messageSource.getMessage("no_code", null,"기본 메세지", null);
        assertThat(result).isEqualTo("기본 메세지");
    }

    // 인자로 값할당
    @Test
    void argumentMessage(){
        String message = messageSource.getMessage("hello.name", new Object[]{"Spring"}, null);
        assertThat(message).isEqualTo("안녕 Spring");
    }

    @Test
    void defaultLang(){
        assertThat(messageSource.getMessage("hello", null, null)).isEqualTo("안녕");
        assertThat(messageSource.getMessage("hello", null, Locale.KOREA)).isEqualTo("안녕");
    }

    @Test
    void enLang(){
        assertThat(messageSource.getMessage("hello", null, Locale.ENGLISH)).isEqualTo("hello");
    }
}
