package hello.exception.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * 예외 객체 발생 시 이 ExcpetionResolver를 통해서 예외를 먹어 버린다.
 * -> 그 목적은 예외 객체 발생 시 정상 흐름으로써 WAS에 전달하기 위해
 */

@Slf4j
public class MyHandlerExcpetionResolver implements HandlerExceptionResolver {
    @Override
    public @Nullable ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {

        if(ex instanceof IllegalArgumentException) {
            log.info("IllegalException resolver to 400");
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,ex.getMessage()); // 여기서 예외 객체를 먹어 버린다.
                return new ModelAndView(); // ModelAndView를 반환하는 이유는 오류 페이지 반환하는 것이 목적이 아닌 정상적인 객체(ModelAndView)를
                                           // 반환함으로써 WAS에 예외 객체 상황을 정상 요청 흐름으로 바꾸기 위한 ExceptionResolver의 스펙이다.
            } catch (IOException e) {      // 이건 memo 폴더의 "@ExceptionHandler" 부분에 스펙을 자세히 적어 놓음.
                log.error("resolver ex",e);
            }
        }

        // 만약 null이 리턴되면 예외 객체가 먹혀 버리지 않고, 비정상 흐름으로써 예외객체가 WAS에 전달
        return null;





    }
}
