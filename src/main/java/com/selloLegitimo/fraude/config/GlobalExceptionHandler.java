package com.selloLegitimo.fraude.config;

import com.selloLegitimo.fraude.excepcion.ExcepcionAccesoDenegado;
import com.selloLegitimo.fraude.excepcion.ExcepcionCadenaRota;
import com.selloLegitimo.fraude.excepcion.ExcepcionCasoYaCerrado;
import com.selloLegitimo.fraude.excepcion.ExcepcionEstadoInmutable;
import com.selloLegitimo.fraude.excepcion.ExcepcionEvidenciaInvalida;
import com.selloLegitimo.fraude.excepcion.ExcepcionFraude;
import com.selloLegitimo.fraude.excepcion.ExcepcionHashNoCoincide;
import com.selloLegitimo.fraude.excepcion.ExcepcionRecursoNoEncontrado;
import java.time.LocalDateTime;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ExcepcionRecursoNoEncontrado.class)
    public ProblemDetail handleNotFound(ExcepcionRecursoNoEncontrado e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }

    @ExceptionHandler(ExcepcionEstadoInmutable.class)
    public ProblemDetail handleInmutable(ExcepcionEstadoInmutable e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }

    @ExceptionHandler({ExcepcionHashNoCoincide.class, ExcepcionEvidenciaInvalida.class})
    public ProblemDetail handleValidation(ExcepcionFraude e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }

    @ExceptionHandler(ExcepcionAccesoDenegado.class)
    public ProblemDetail handleAccessDenied(ExcepcionAccesoDenegado e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }

    @ExceptionHandler(ExcepcionCasoYaCerrado.class)
    public ProblemDetail handleCasoYaCerrado(ExcepcionCasoYaCerrado e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }

    @ExceptionHandler(ExcepcionCadenaRota.class)
    public ProblemDetail handleCadenaRota(ExcepcionCadenaRota e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }

    @ExceptionHandler(ExcepcionFraude.class)
    public ProblemDetail handleFraude(ExcepcionFraude e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Error de validacion");
        pd.setProperty("timestamp", LocalDateTime.now());
        pd.setProperty("errors", e.getBindingResult().getFieldErrors().stream()
            .map(f -> Map.of("field", f.getField(), "message", f.getDefaultMessage()))
            .toList());
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception e) {
        log.error("Error no controlado", e);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }
}
