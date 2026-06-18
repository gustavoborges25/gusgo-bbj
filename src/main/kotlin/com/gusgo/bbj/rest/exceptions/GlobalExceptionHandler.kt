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
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException
import tools.jackson.databind.exc.InvalidNullException
import java.time.format.DateTimeParseException
import java.util.regex.Pattern

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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(processFieldErrors(fieldErrors, "One or more fields are invalid."))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException): ResponseEntity<ErrorRestResponse<*>> {
        var message = "Request body is missing or with error"
        val cause = exception.cause
        val fieldErrors = mutableListOf<ErrorRestResponse.FieldError>()

        if (cause is InvalidNullException && cause.javaClass.simpleName == "KotlinInvalidNullException") {
            val fieldName = cause.path.joinToString(".") { ref ->
                if (ref.index > -1) "[${ref.index}]" else ref.propertyName
            }
            message = "One or more required fields were not submitted."
            fieldErrors.add(
                ErrorRestResponse.FieldError(
                    field = fieldName,
                    message = "This field is mandatory."
                )
            )
        }

        val response = ErrorRestResponse(
            code = HttpStatus.BAD_REQUEST.value(),
            message = message,
            details = fieldErrors.ifEmpty { null }
        )
        logger.error(message, exception)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
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


    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<ErrorRestResponse<*>> {
        val status = ex.statusCode
        val errorMessage = ex.reason ?: "An unexpected error occurred."
        return ResponseEntity.status(status.value()).body(
            ErrorRestResponse(
                code = status.value(),
                message = errorMessage,
                null
            )
        )
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleResponseStatusException(ex: NoResourceFoundException): ResponseEntity<ErrorRestResponse<*>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorRestResponse(
                code = HttpStatus.NOT_FOUND.value(),
                message = "The requested resource or endpoint does not exist.",
                null
            )
        )
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(
        ex: AuthorizationDeniedException
    ): ResponseEntity<ErrorRestResponse<*>> {

        val errorBody = ErrorRestResponse(
            code = HttpStatus.FORBIDDEN.value(),
            message = "Access Denied: You do not have the required permissions to access this resource.",
            null
        )

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorBody)
    }

    private fun processFieldErrors(fieldErrors: List<FieldError>): ErrorRestResponse<*> {
        return processFieldErrors(fieldErrors, null)
    }

    private fun processFieldErrors(fieldErrors: List<FieldError>, message: String?): ErrorRestResponse<*> {
        val finalMessage = message?.takeUnless { it.isBlank() } ?: "validation error"
        val errors = fieldErrors.map { fieldError ->
            ErrorRestResponse.FieldError(
                field = fieldError.field,
                message = fieldError.defaultMessage?.takeUnless { it.isBlank() } ?: "validation error"
            )
        }
        return ErrorRestResponse(
            code = HttpStatus.BAD_REQUEST.value(),
            message = finalMessage,
            details = errors
        )
    }

    private fun removeJavaLang(input: String): String {
        val pattern = Pattern.compile("java.lang.", Pattern.MULTILINE)
        return pattern.matcher(input).replaceAll("")
    }
}