package sharafi.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sharafi.dto.ResponseDTO;

import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseDTO onMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        List<String> messages = new ArrayList<>();

        ex.getGlobalErrors().forEach(e -> messages.add(e.getDefaultMessage()));
        ex.getBindingResult().getFieldErrors().forEach(e -> messages.add(e.getDefaultMessage()));

        return routine("Method Argument Not Valid Exception", messages.toString());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseDTO onIllegalArgumentException(IllegalArgumentException e) {
        return routine("Illegal Argument Exception", e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    ResponseDTO onUsernameNotFoundException(UsernameNotFoundException e) {
        return routine("Username Not Found Exception", e.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseDTO onInvalidCredentialsException(InvalidCredentialsException e) {
        return routine("Invalid Credentials Exception", e.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    ResponseDTO onInsufficientBalanceException(InsufficientBalanceException e) {
        return routine("Insufficient Balance Exception", e.getMessage());
    }

    @ExceptionHandler(RecordNotFoundException.class)
    ResponseDTO onRecordNotFoundException(RecordNotFoundException e) {
        return routine("Record Not Found Exception", e.getMessage());
    }

    @ExceptionHandler(ClosedChannelException.class)
    ResponseDTO onClosedChannelException(ClosedChannelException e) {
        return routine("Closed Channel Exception", "SERVICE NOT AVAILABLE NOW");
    }

    private static ResponseDTO routine(String exception, String message) {

        log.error("{}: {}", exception, message);

        return new ResponseDTO(false, message);
    }
}
