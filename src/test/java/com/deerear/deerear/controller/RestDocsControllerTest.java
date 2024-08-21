package com.deerear.deerear.controller;

import com.deerear.deerear.base.AbstractRestDocsTests;
import com.deerear.deerear.dto.RestDocsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

@WebMvcTest(RestDocsController.class)
public class RestDocsControllerTest extends AbstractRestDocsTests {


    @Test
    void getRestDocsTest() throws Exception {

        ResultActions actions = mockMvc.perform(get("/restdoc/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}", responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("id"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("description"),
                        fieldWithPath("estimateValue").type(JsonFieldType.NUMBER).description("estimateValue")
                )));
    }

    @Test
    void listRestDocsTest() throws Exception {



        ResultActions actions = mockMvc.perform(get("/restdoc?limit=10&offset=1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        actions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void postRestDocsTest() throws Exception {

        RestDocsDTO input = RestDocsDTO.builder()
                .id((long) 777)
                .description("test")
                .build();

        mockMvc.perform(
                post("/restdoc", input)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk());
    }

    @Test
    void putRestDocsTest() throws Exception {

        RestDocsDTO input = RestDocsDTO.builder()
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
