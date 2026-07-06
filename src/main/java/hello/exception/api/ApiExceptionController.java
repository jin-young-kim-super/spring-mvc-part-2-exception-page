package hello.exception.api;


import hello.exception.exception.BadRequestException;
import hello.exception.exception.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class ApiExceptionController {

    // 만약 이때 런타임 에러가 터지면 HTML 오류 화면이 클라이언트에 제공되어 버린다.
    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if(id.equals("ex")) {
            // 런타임 에러 발생 시 WebServerCustomizer 클래스에 등록된 오류 페이지 컨트롤러가 호출된다.
            // -> 이 시점에서는 아직은 서블릿 방식으로 예외를 처리하고 있음에 주의하자
            throw new RuntimeException("잘못된 사용자");
        }

        // 이 예외 객체가 WAS에 그대로 전달이 되면 500으로 처리돼 버린다
        // -> ExceptionHandler를 적용하지 않았다는 가정 하에!
        if(id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력값");
        }

        if(id.equals("user-ex")){
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id,"hello" + id);
    }

    @GetMapping("/api/response-status-ex1")
    public String responseStatusEx1() {
        throw new BadRequestException();
    }

    @GetMapping("/api/response-status-ex2")
    public String responseStatusEx2() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"error.bad",new IllegalArgumentException());
    }

    @GetMapping("/api/default-handler-ex")
    public String defaultException(@RequestParam Integer data) {
        return "ok";
    }


    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
