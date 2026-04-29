package com.teachertransfer.controller;

import com.teachertransfer.dto.ApiResponse;
import com.teachertransfer.dto.geography.BlockResponse;
import com.teachertransfer.dto.geography.DistrictResponse;
import com.teachertransfer.service.GeographyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/geography")
@CrossOrigin(origins = "*")
public class GeographyController {

    @Autowired
    private GeographyService geographyService;

    @GetMapping("/districts")
    public ResponseEntity<ApiResponse<List<DistrictResponse>>> getAllDistricts() {
        List<DistrictResponse> districts = geographyService.getAllDistricts();
        return ResponseEntity.ok(ApiResponse.success("Districts retrieved", districts));
    }

    @GetMapping("/districts/search")
    public ResponseEntity<ApiResponse<List<DistrictResponse>>> searchDistricts(@RequestParam String name) {
        List<DistrictResponse> districts = geographyService.searchDistricts(name);
        return ResponseEntity.ok(ApiResponse.success("Districts found", districts));
    }

    @GetMapping("/districts/{districtId}")
    public ResponseEntity<ApiResponse<DistrictResponse>> getDistrictById(@PathVariable Integer districtId) {
        DistrictResponse district = geographyService.getDistrictById(districtId);
        return ResponseEntity.ok(ApiResponse.success("District retrieved", district));
    }

    @GetMapping("/districts/{districtId}/blocks")
    public ResponseEntity<ApiResponse<List<BlockResponse>>> getBlocksByDistrict(@PathVariable Integer districtId) {
        List<BlockResponse> blocks = geographyService.getBlocksByDistrict(districtId);
        return ResponseEntity.ok(ApiResponse.success("Blocks retrieved", blocks));
    }

    @GetMapping("/blocks/search")
    public ResponseEntity<ApiResponse<List<BlockResponse>>> searchBlocks(@RequestParam String name) {
        List<BlockResponse> blocks = geographyService.searchBlocks(name);
        return ResponseEntity.ok(ApiResponse.success("Blocks found", blocks));
    }

    @GetMapping("/blocks/{blockId}")
    public ResponseEntity<ApiResponse<BlockResponse>> getBlockById(@PathVariable Integer blockId) {
        BlockResponse block = geographyService.getBlockById(blockId);
        return ResponseEntity.ok(ApiResponse.success("Block retrieved", block));
    }
}