package com.gusgo.bbj.rest.resources

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RestResponse<T>(
    val data: T? = null,

    val total: Long? = null,
    val page: Int? = null,
    val size: Int? = null
)
