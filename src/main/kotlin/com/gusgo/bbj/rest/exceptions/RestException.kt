package com.gusgo.bbj.rest.exceptions

class RestException(
    override val message: String,
) : RuntimeException(message) {
    companion object {
        private const val serialVersionUID = 1L
    }
}