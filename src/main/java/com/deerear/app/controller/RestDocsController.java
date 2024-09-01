package com.deerear.app.controller;

import com.deerear.app.dto.RestDocsRequestDTO;
import com.deerear.app.dto.RestDocsResponseDTO;
import com.deerear.app.service.RestDocsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/restdoc")
@RequiredArgsConstructor
@RestController
public class RestDocsController {

    private final RestDocsService restDocsService;

    @GetMapping("/{id}")
    public ResponseEntity<RestDocsResponseDTO> getRestDocs(@PathVariable("id") Long id){

        RestDocsResponseDTO response = restDocsService.getRestDocs(id);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<RestDocsResponseDTO>> listRestDocs(@RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset ){

        List<RestDocsResponseDTO> response = restDocsService.listRestDocs(limit, offset);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<RestDocsResponseDTO> postRestDocs(@RequestBody RestDocsRequestDTO request){

        RestDocsResponseDTO response = restDocsService.postRestDocs(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestDocsResponseDTO> putRestDocs(@PathVariable("id") Long id, @RequestBody RestDocsRequestDTO request){

        RestDocsResponseDTO response = restDocsService.putRestDocs(id, request);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestDocsResponseDTO> deleteRestDocs(@PathVariable("id") Long id){

        restDocsService.deleteRestDocs(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

