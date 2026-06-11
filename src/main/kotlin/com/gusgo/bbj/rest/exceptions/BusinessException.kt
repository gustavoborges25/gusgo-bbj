package com.gusgo.bbj.rest.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BusinessException(
    override val message: String,
) : RuntimeException(message) {
    companion object {
        private const val serialVersionUID = 1L
    }
}