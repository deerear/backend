package com.deerear.deerear.controller;

import com.deerear.deerear.base.AbstractRestDocsTests;
import com.deerear.deerear.dto.RestDocsDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestDocsController.class)
public class RestDocsTestController extends AbstractRestDocsTests {


    @Test
    void getRestDocsTest() throws Exception {
        mockMvc.perform(get("/restdoc/1")).andExpect(status().isOk());
    }

    @Test
    void postRestDocsTest() throws Exception {

        RestDocsDto input = RestDocsDto.builder()
                .id((long) 777)
                .description("test")
                .build();

        mockMvc.perform(
                post("/restdoc/rest", input)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    void putRestDocsTest() throws Exception {

        RestDocsDto input = RestDocsDto.builder()
                .id((long) 777)
                .description("test")
                .build();

        mockMvc.perform(
                        put("/restdoc/1", input)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteRestDocsTest() throws Exception {

        mockMvc.perform(
                        delete("/restdoc/1"))
                .andExpect(status().isOk());
    }
}
