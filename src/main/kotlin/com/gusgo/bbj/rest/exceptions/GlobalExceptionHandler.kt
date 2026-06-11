package com.gusgo.bbj.rest.exceptions

import com.gusgo.bbj.rest.resources.ErrorRestResponse
import org.apache.coyote.BadRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import tools.jackson.databind.exc.InvalidFormatException
import java.time.format.DateTimeParseException
import java.util.regex.Pattern
import kotlin.collections.get

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(RestException::class)
    fun handleRestException(exception: RestException): ResponseEntity<ErrorRestResponse<*>> {
        logger.error(exception.message, exception)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorRestResponse(code = HttpStatus.BAD_REQUEST.value(), message = exception.message, null)
        )
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(exception: BusinessException): ResponseEntity<ErrorRestResponse<*>> {
        logger.error(exception.message, exception)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorRestResponse(code = HttpStatus.BAD_REQUEST.value(), message = exception.message, null)
        )
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(e: BadRequestException): ResponseEntity<ErrorRestResponse<*>> {
        logger.error("Bad request", e)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorRestResponse(code = HttpStatus.BAD_REQUEST.value(), message = e.message ?: "bad request", null)
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErrorRestResponse<*>> {
        logger.error("Illegal argument", e)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorRestResponse(code = HttpStatus.BAD_REQUEST.value(), message = e.message ?: "bad request", null)
        )
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(e: DataIntegrityViolationException): ResponseEntity<ErrorRestResponse<*>> {
        logger.error("Data integrity violation", e)
        val message: String = e.message ?: "Data integrity violation"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorRestResponse(code = HttpStatus.BAD_REQUEST.value(), message = message , null)
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException): ResponseEntity<ErrorRestResponse<*>> {
        val fieldErrors : List<FieldError> = exception.bindingResult.fieldErrors
        logger.error("MethodArgumentNotValidException", exception)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(processFieldErrors(fieldErrors))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException): ResponseEntity<ErrorRestResponse<*>> {
        var message = "Request body is missing or with error"
        val root = exception.rootCause
        if (root != null) {
            message = when (root) {
                is InvalidFormatException -> {
                    "Value '${root.value}' is not a valid for '${root.path[0].propertyName}'."
                }
                else -> root.message ?: message
            }
        }
        logger.error(message, exception)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorRestResponse(code = HttpStatus.BAD_REQUEST.value(), message = message, null)
        )
    }

    @ExceptionHandler(DateTimeParseException::class)
    fun handleDateTimeParseException(exception: DateTimeParseException): ResponseEntity<ErrorRestResponse<*>> {
        logger.error(exception.message, exception)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorRestResponse(code = HttpStatus.BAD_REQUEST.value(), message = exception.message ?: "invalid Date", null)
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorRestResponse<*>> {
        logger.error(exception.message, exception)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorRestResponse(code = HttpStatus.INTERNAL_SERVER_ERROR.value(), message = HttpStatus.INTERNAL_SERVER_ERROR.name, null)
        )
    }

    @ExceptionHandler(ChangeSetPersister.NotFoundException::class)
    fun handleNotFoundException(exception: ChangeSetPersister.NotFoundException): ResponseEntity<ErrorRestResponse<*>> {
        logger.error(exception.message, exception)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorRestResponse(code = HttpStatus.NOT_FOUND.value(), message = exception.message ?: "not found", null)
        )
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleMissingRequestHeaderException(exception: MissingRequestHeaderException): ResponseEntity<ErrorRestResponse<*>> {
        logger.error(exception.message, exception)
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(
            ErrorRestResponse(code = HttpStatus.PRECONDITION_FAILED.value(), message = exception.message, null)
        )
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(exception: MissingServletRequestParameterException): ResponseEntity<ErrorRestResponse<*>> {
        logger.error(exception.message, exception)
        return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(
            ErrorRestResponse(code = HttpStatus.PRECONDITION_REQUIRED.value(), message = exception.message, null)
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(exception: MethodArgumentTypeMismatchException): ResponseEntity<ErrorRestResponse<*>> {
        logger.error(exception.message, exception)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorRestResponse(code = HttpStatus.BAD_REQUEST.value(), message = removeJavaLang(exception.message), null)
        )
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(exception: NoHandlerFoundException): ResponseEntity<ErrorRestResponse<*>> {
        logger.error(exception.message, exception)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorRestResponse(code = HttpStatus.NOT_FOUND.value(), message = exception.message ?: "No handler found", null)
        )
    }

    private fun processFieldErrors(fieldErrors: List<FieldError>): ErrorRestResponse<*> {
        val errors = fieldErrors.map { fieldError ->
            ErrorRestResponse.FieldError(field = fieldError.field, message = fieldError.defaultMessage ?: "validation error")
        }
        return ErrorRestResponse(
            code = HttpStatus.BAD_REQUEST.value(),
            message = "validation error",
            details = errors
        )
    }

    private fun removeJavaLang(input: String): String {
        val pattern = Pattern.compile("java.lang.", Pattern.MULTILINE)
        return pattern.matcher(input).replaceAll("")
    }
}