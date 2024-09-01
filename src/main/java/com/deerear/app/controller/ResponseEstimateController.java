package com.deerear.app.controller;

import com.deerear.app.dto.ResponseEstimateDTO;
import com.deerear.app.service.ResponseEstimateService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/response_estimate")
@RequiredArgsConstructor
@Tag(name = "Test", description = "Test API")
public class ResponseEstimateController {

    // 빈서비스 테스트용으로 주입해놓음.
    private final ResponseEstimateService responseEstimateService;

    @GetMapping
    @Operation(summary = "Get", description = "테스트 API.")
    public ResponseEntity<List<ResponseEstimateDTO>> getAllResponseEstimates() {
        List<ResponseEstimateDTO> responseEstimates = Collections.emptyList();
        return new ResponseEntity<>(responseEstimates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "memberId", description = "멤버 ID"),
            @Parameter(name = "page", description = "페이지 번호"),
            @Parameter(name = "size", description = "(선택적) 페이지당 컨텐츠 개수, 기본 10")
    })
    @Operation(summary = "멤버 ID로 조회하는 API 입니다.", description = "테스트 API.")
    public ResponseEntity<ResponseEstimateDTO> getResponseEstimateById(@PathVariable Long id) {
        ResponseEstimateDTO responseEstimate = ResponseEstimateDTO.builder()
                .id(id)
                .name("Sample Name")
                .description("Sample Description")
                .estimateValue(1234.56)
                .build();
        return new ResponseEntity<>(responseEstimate, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Post", description = "Test API")
    public ResponseEntity<ResponseEstimateDTO> createResponseEstimate(@RequestBody ResponseEstimateDTO responseEstimateDTO) {
        return new ResponseEntity<>(responseEstimateDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Put", description = "Test API")
    public ResponseEntity<ResponseEstimateDTO> updateResponseEstimate(
            @PathVariable Long id,
            @RequestBody ResponseEstimateDTO responseEstimateDTO) {
        return new ResponseEntity<>(responseEstimateDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete", description = "Test API")
    public ResponseEntity<Void> deleteResponseEstimate(@PathVariable Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}