package com.deerear.deerear.controller;

import com.deerear.deerear.dto.RestDocsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/restdoc")
@RestController
public class RestDocsController {

    @GetMapping("/{id}")
    public ResponseEntity<RestDocsDTO> getRestDocs(@PathVariable Long id){
        RestDocsDTO restDocs = RestDocsDTO.builder()
                .id(id)
                .name("Test User")
                .description("This is Test User")
                .estimateValue((double) 0)
                .build();
        return new ResponseEntity<>(restDocs, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<RestDocsDTO>> listRestDocs(@RequestParam Integer limit, @RequestParam Integer offset ){
        List<RestDocsDTO> restDocsList = new ArrayList<RestDocsDTO>();

        RestDocsDTO testDoc1 = RestDocsDTO.builder()
                .id((long) 1)
                .name("Test User 1")
                .description("List Test")
                .estimateValue((double) 0)
                .build();

        RestDocsDTO testDoc2 = RestDocsDTO.builder()
                .id((long) 2)
                .name("Test User 2")
                .description("List Test")
                .estimateValue((double) 0)
                .build();

        restDocsList.add(testDoc1);
        restDocsList.add(testDoc2);

        return new ResponseEntity<>(restDocsList, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<RestDocsDTO> postRestDocs(@RequestBody RestDocsDTO input){
        RestDocsDTO restDocs = RestDocsDTO.builder()
                .id((long)777)
                .name(input.getName())
                .description(input.getDescription())
                .estimateValue((double) 0)
                .build();
        return new ResponseEntity<>(restDocs, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestDocsDTO> putRestDocs(@PathVariable Long id, @RequestBody RestDocsDTO input){
        RestDocsDTO restDocs = RestDocsDTO.builder()
                .id(id)
                .name("Test User")
                .description("Put Test")
                .estimateValue((double) 0)
                .build();
        return new ResponseEntity<>(restDocs, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestDocsDTO> deleteRestDocs(@PathVariable Long id){
        RestDocsDTO restDocs = RestDocsDTO.builder()
                .id(id)
                .name("Test User")
                .description("Delete Test")
                .estimateValue((double) 0)
                .build();
        return new ResponseEntity<>(restDocs, HttpStatus.OK);
    }
}

