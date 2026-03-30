package com.pidulgi.server.common.exception

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log: Logger by lazy { LoggerFactory.getLogger("GlobalExceptionHandler") }

    @ExceptionHandler(CustomException::class)
    fun customException(
        e: CustomException,
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        showWarningLog(servletRequest, e.message)
        return ResponseEntity.badRequest().body(ErrorResponse(e.message))
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun httpRequestMethodNotSupportedException(
        e: HttpRequestMethodNotSupportedException,
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        showWarningLog(servletRequest, e.message)
        return ResponseEntity.badRequest().body(ErrorResponse(e.message))
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun noResourceException(
        e: NoResourceFoundException,
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        showWarningLog(servletRequest, e.message)
        return ResponseEntity.badRequest().body(ErrorResponse(e.message))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun httpMessageNotReadableException(
        e: HttpMessageNotReadableException,
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        showWarningLog(servletRequest, e.message)
        return ResponseEntity.badRequest().body(ErrorResponse(e.message))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        e: IllegalArgumentException,
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        showWarningLog(servletRequest, e.message)
        return ResponseEntity.badRequest().body(ErrorResponse(e.message))
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun responseStatusException(
        e: ResponseStatusException,
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        showWarningLog(servletRequest, e.message)
        return ResponseEntity.status(e.statusCode).body(ErrorResponse(e.reason))
    }

    @ExceptionHandler(Exception::class)
    fun exception(
        e: Exception,
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        log.error(
            "exception method = {}, uri = {}, message = {}",
            servletRequest.method,
            servletRequest.requestURI,
            e.message
        )
        return ResponseEntity.internalServerError().body(ErrorResponse(e.message))
    }

    private fun showWarningLog(servletRequest: HttpServletRequest, message: String?) {
        log.warn(
            "exception method = {}, uri = {}, message = {}",
            servletRequest.method,
            servletRequest.requestURI,
            message
        )
    }
}