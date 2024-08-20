package com.deerear.deerear.controller;

import com.deerear.deerear.dto.RestDocsDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequestMapping("/restdoc")
@RestController
public class RestDocsController {

    @GetMapping("/{id}")
    public ResponseEntity<RestDocsDto> getRestDocs(@PathVariable Long id){
        RestDocsDto restDocs = RestDocsDto.builder()
                .id(id)
                .name("Test User")
                .description("This is Test User")
                .build();
        return new ResponseEntity<>(restDocs, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<RestDocsDto>> listRestDocs(@RequestParam Integer limit, @RequestParam Integer offset ){
        List<RestDocsDto> restDocsList = new ArrayList<RestDocsDto>();

        RestDocsDto testDoc1 = RestDocsDto.builder()
                .id((long) 1)
                .name("Test User 1")
                .description("List Test")
                .build();

        RestDocsDto testDoc2 = RestDocsDto.builder()
                .id((long) 2)
                .name("Test User 2")
                .description("List Test")
                .build();

        restDocsList.add(testDoc1);
        restDocsList.add(testDoc2);

        return new ResponseEntity<>(restDocsList, HttpStatus.OK);
    }

    @PostMapping("/rest")
    public ResponseEntity<RestDocsDto> postRestDocs(@RequestBody RestDocsDto input){
        RestDocsDto restDocs = RestDocsDto.builder()
                .id((long)777)
                .name(input.getName())
                .description(input.getDescription())
                .build();
        return new ResponseEntity<>(restDocs, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestDocsDto> putRestDocs(@PathVariable Long id, @RequestBody RestDocsDto input){
        RestDocsDto restDocs = RestDocsDto.builder()
                .id(id)
                .name("Test User")
                .description("Put Test")
                .build();
        return new ResponseEntity<>(restDocs, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestDocsDto> deleteRestDocs(@PathVariable Long id){
        RestDocsDto restDocs = RestDocsDto.builder()
                .id(id)
                .name("Test User")
                .description("Delete Test")
                .build();
        return new ResponseEntity<>(restDocs, HttpStatus.OK);
    }
}

