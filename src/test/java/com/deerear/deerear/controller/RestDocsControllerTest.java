package com.deerear.deerear.controller;

import com.deerear.deerear.base.AbstractRestDocsTests;
import com.deerear.deerear.dto.RestDocsRequestDTO;
import com.deerear.deerear.dto.RestDocsResponseDTO;
import com.deerear.deerear.service.RestDocsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.mockito.BDDMockito.given;
@WebMvcTest(RestDocsController.class)
public class RestDocsControllerTest extends AbstractRestDocsTests {

    @MockBean
    private RestDocsService restDocsService;

    @Test
    void getRestDocsTest() throws Exception {

        RestDocsResponseDTO response = RestDocsResponseDTO.builder()
                .id(1L)
                .name("이름")
                .description("단건 조회")
                .estimateValue(1000.0)
                .build();

        given(restDocsService.getRestDocs(1L)).willReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/restdoc/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcRestDocumentation.document(
                        "{class-name}/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id 값")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("id 값"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름 값"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명 값"),
                                fieldWithPath("estimateValue").type(JsonFieldType.NUMBER).description("추정 값")
                )));
    }

    @Test
    void listRestDocsTest() throws Exception {

        List<RestDocsResponseDTO> response = new ArrayList<>();

        RestDocsResponseDTO testDoc1 = RestDocsResponseDTO.builder()
                .id(1L)
                .name("이름 1")
                .description("다건 조회 1")
                .estimateValue(1000.0)
                .build();

        RestDocsResponseDTO testDoc2 = RestDocsResponseDTO.builder()
                .id(2L)
                .name("이름 2")
                .description("다건 조회 2")
                .estimateValue(1000.0)
                .build();

        response.add(testDoc1);
        response.add(testDoc2);



        given(restDocsService.listRestDocs(10,1)).willReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/restdoc")
                        .param("limit","10")
                        .param("offset","1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcRestDocumentation.document(
                        "{class-name}/{method-name}",
                        queryParameters(
                                parameterWithName("limit").description("제한 값"),
                                parameterWithName("offset").description("오프셋 값")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("id 값"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("이름 값"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("설명 값"),
                                fieldWithPath("[].estimateValue").type(JsonFieldType.NUMBER).description("추정 값")
                        )
                ));
    }

    @Test
    void postRestDocsTest() throws Exception {

        RestDocsRequestDTO request = RestDocsRequestDTO.builder()
                .name("이름")
                .description("Create Test")
                .estimateValue(1000.0)
                .build();

        RestDocsResponseDTO response = RestDocsResponseDTO.builder()
                .id(777L)
                .name("이름")
                .description("Create Test")
                .estimateValue(1000.0)
                .build();

        given(restDocsService.postRestDocs(request)).willReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/restdoc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcRestDocumentation.document(
                        "{class-name}/{method-name}",
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름 값"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명 값"),
                                fieldWithPath("estimateValue").type(JsonFieldType.NUMBER).description("추정 값").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("id 값"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름 값"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명 값"),
                                fieldWithPath("estimateValue").type(JsonFieldType.NUMBER).description("추정 값")
                        )));
    }

    @Test
    void putRestDocsTest() throws Exception {

        RestDocsRequestDTO request = RestDocsRequestDTO.builder()
                .name("이름")
                .description("Put Test")
                .estimateValue(1000.0)
                .build();

        RestDocsResponseDTO response = RestDocsResponseDTO.builder()
                .id(1L)
                .name("이름")
                .description("Put Test")
                .estimateValue(1000.0)
                .build();

        given(restDocsService.putRestDocs(1L, request)).willReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/restdoc/{id}",1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andDo(MockMvcRestDocumentation.document(
                        "{class-name}/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id 값")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름 값"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명 값"),
                                fieldWithPath("estimateValue").type(JsonFieldType.NUMBER).description("추정 값")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("id 값"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름 값"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("설명 값"),
                                fieldWithPath("estimateValue").type(JsonFieldType.NUMBER).description("추정 값")
                )));
    }

    @Test
    void deleteRestDocsTest() throws Exception {

        mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/restdoc/{id}", 1))
                .andExpect(status().isNoContent())
                .andDo(MockMvcRestDocumentation.document(
                        "{class-name}/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id 값")
                        )
                ));
    }
}
