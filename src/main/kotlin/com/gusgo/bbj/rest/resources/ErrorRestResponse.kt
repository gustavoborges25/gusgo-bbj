package com.gusgo.bbj.rest.resources

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorRestResponse<T>(
    val code: Int,
    val message: String,
    val details: T? = null
) {
    data class FieldError(
        val field: String,
        val message: String
    )
}
