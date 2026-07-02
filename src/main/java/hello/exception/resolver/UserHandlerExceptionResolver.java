package hello.exception.resolver;

import hello.exception.exception.UserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    // ExceptionResolver 단계에서는 JSON 변환을 위한 ReturnValueResolver를 사용하지 못하므로
    // ObjectMapper를 통해서 API 오류 응답을 JSON으로 변환시켜줘야 한다.
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public @Nullable ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {

        try{
            if(ex instanceof UserException) {
                log.info("UserException resolver to 400");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                String acceptHeader = request.getHeader("accept");
                if("application/json".equals(acceptHeader)) {
                    Map<String,Object> errorResult = new HashMap<>();
                    errorResult.put("ex",ex.getClass());
                    errorResult.put("message",ex.getMessage());

                    // JSON 변환 : API 응답 오류
                    String result = objectMapper.writeValueAsString(errorResult);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(result);
                    return new ModelAndView();
                } else {
                    // accept : text/html 등 : 오류 화면 페이지 응답
                    return new ModelAndView("/error/500");
                }
            }
        }catch (IOException e){
            log.error("resolver ex",e);
        }
        return null;
    }
}
