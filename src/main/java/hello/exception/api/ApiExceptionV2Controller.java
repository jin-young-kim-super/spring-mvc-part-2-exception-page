package hello.exception.api;


import hello.exception.exHandler.ErrorResult;
import hello.exception.exception.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
public class ApiExceptionV2Controller {

    // 이 컨트롤러 내에서 IllegalArgumentException 발생 시, 이 메서드에서 예외가 잡히고 API 오류 응답(ErroResult)가 API 오류 응답으로 반환된다.
    // -> 물론 WAS의 재요청 없고, return new ModelAndVIew도 없고, response.getWriter()도 사용한 했고, 우리가 API 오류 응답을 커스터마이징 할 수가 있다.
    // 물론 ExceptionResolver의 최우선 순위인 ExceptionHandlerExceptionResolver가 호출이 돼 처리한다.
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResult illegalExHandler(IllegalArgumentException e) {
        log.error("[exceptionHandler] ex",e);
        return new ErrorResult("BAD",e.getMessage());
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResult> useExHandler(UserException e) {
        log.error("[exceptionHandler ] ex ", e);
        ErrorResult errorResult = new ErrorResult("USR-EX", e.getMessage());
        return new ResponseEntity<>(errorResult,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResult errorResult(Exception e) { // Exception : 예외의 최상위자
        log.error("[exception ex]", e);           // -> 모든 예외의 최상위자이기에 예를 들어 자식 예외인 IllegalArugment, UserException 처리하는
                                                  // @ExceptionHandler가 없으면 여기서 다 잡는다.
        return new ErrorResult("Ex","내부 오류");
    }



    // 만약 이때 런타임 에러가 터지면 HTML 오류 화면이 클라이언트에 제공되어 버린다.
    @GetMapping("/api2/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if(id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }

        if(id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력값");
        }

        if(id.equals("user-ex")){
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id,"hello" + id);
    }


    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }

}
