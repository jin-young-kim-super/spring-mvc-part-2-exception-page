package hello.exception.servlet;

import org.springframework.boot.web.error.ErrorPage;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 지금은 스프링 부트를 [통해서] 서블릿 컨테이너를 실행하기 때문에, 스프링 부트가 제공하는 기능을 사용해서 서블릿 오
 * 류 페이지를 등록하면 된다.
 */

@Component
public class WebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        // 404 에러 발생 시, "/error-page/404" 컨트롤러로 이동해라
        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error-page/404");
        // 500 에러 발생 시, "/error-page/500" 컨트롤러로 이동해라
        ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500");
        // RuntimeException 또는 그것의 자식 예외 발생 시, "/error-page/500" 컨트롤러로 이동해라
        ErrorPage errorPageEx = new ErrorPage(RuntimeException.class, "/error-page/500");

        // 에러 페이지 등록
        factory.addErrorPages(errorPage404,errorPage500,errorPageEx);
    }
}

/**
 * 여기에서 중요한 점이 있다.
 * -> 이전까지는 Exception 발생이던 sendError이던 WAS에서 오류 페이지를 자동으로 만들어 준 다음에 그걸 클라이언트에 응답을 내렸다.
 * 그러나 여기서 Exception 발생 혹은 sendError 발생 시, WAS에서 클라이언트로 바로 응답을 보내는 것이 아니라, WAS -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(에러 페이지 용)
 * 순으로 다시 컨트롤러를 호출한다는 것에 주의하자
 */