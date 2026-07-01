package com.gusgo.bbj.rest.controllers.methodology

import com.gusgo.bbj.application.dtos.methodology.BeltResponse
import com.gusgo.bbj.application.dtos.methodology.ModuleResponse
import com.gusgo.bbj.application.services.methodology.BeltService
import com.gusgo.bbj.application.services.methodology.ModuleService
import com.gusgo.bbj.rest.resources.RestResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/modules")
class ModuleController (
    private val moduleService: ModuleService
) {
    @GetMapping
    fun getAll(): ResponseEntity<RestResponse<List<ModuleResponse>>> {
        return ResponseEntity
            .ok(RestResponse(moduleService.findAll()))
    }
}