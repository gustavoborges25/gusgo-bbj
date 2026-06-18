package com.gusgo.bbj.rest.controllers.pedagogy

import com.gusgo.bbj.application.dtos.pedagogy.BeltResponse
import com.gusgo.bbj.application.services.pedagogy.BeltService
import com.gusgo.bbj.rest.resources.RestResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/belts")
class BeltController (
    private val beltService: BeltService
) {
    @GetMapping
    fun getAll(): ResponseEntity<RestResponse<List<BeltResponse>>> {
        return ResponseEntity
            .ok(RestResponse(beltService.findAll()))
    }
}