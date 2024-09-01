package com.deerear.deerear.controller;

import com.deerear.app.controller.RestDocsController;
import com.deerear.deerear.base.AbstractRestDocsTests;
import com.deerear.app.dto.RestDocsRequestDTO;
import com.deerear.app.dto.RestDocsResponseDTO;
import com.deerear.app.service.RestDocsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.mockito.BDDMockito.given;
@WebMvcTest(RestDocsController.class)
public class RestDocsControllerTest extends AbstractRestDocsTests {

    @MockBean
    private RestDocsService restDocsService;

    @Test
    void getRestDocsTest() throws Exception {

        Long id = 1L;
        String name = "이름";
        String description = "단건 조회";
        Double estimateValue = 1000.0;


        RestDocsResponseDTO response = RestDocsResponseDTO.builder()
                .id(id)
                .name(name)
                .description(description)
                .estimateValue(estimateValue)
                .build();

        given(restDocsService.getRestDocs(id)).willReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/restdoc/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("description").value(description))
                .andExpect(jsonPath("estimateValue").value(estimateValue))
                .andDo(print())
                .andDo(MockMvcRestDocumentation.document(
                        "{class-name}/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id 값")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("id 값"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("estimateValue").type(JsonFieldType.NUMBER).description("추정 값")
                )));

    }

    @Test
    void getRestDocsWithHeadersTest() throws Exception {

        Long id = 1L;
        String name = "이름";
        String description = "단건 조회";
        Double estimateValue = 1000.0;

        RestDocsResponseDTO response = RestDocsResponseDTO.builder()
                .id(id)
                .name(name)
                .description(description)
                .estimateValue(estimateValue)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer TOKEN");

        given(restDocsService.getRestDocs(id)).willReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/restdoc/{id}", 1)
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("description").value(description))
                .andExpect(jsonPath("estimateValue").value(estimateValue))
                .andDo(print())
                .andDo(MockMvcRestDocumentation.document(
                        "{class-name}/{method-name}",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer TOKEN")
                        ),
                        pathParameters(
                                parameterWithName("id").description("id 값")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("id 값"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("estimateValue").type(JsonFieldType.NUMBER).description("추정 값")
                        )));
    }

    @Test
    void listRestDocsTest() throws Exception {

        Long id = 1L;
        String name = "이름 1";
        String description = "다건 조회 1";
        Double estimateValue = 1000.0;

        List<RestDocsResponseDTO> response = new ArrayList<>();

        RestDocsResponseDTO testDoc1 = RestDocsResponseDTO.builder()
                .id(id)
                .name(name)
                .description(description)
                .estimateValue(estimateValue)
                .build();

        RestDocsResponseDTO testDoc2 = RestDocsResponseDTO.builder()
                .id(2L)
                .name("이름 2")
                .description("다건 조회 2")
                .estimateValue(estimateValue)
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
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].description").value(description))
                .andExpect(jsonPath("$[0].estimateValue").value(estimateValue))
                .andDo(print())
                .andDo(MockMvcRestDocumentation.document(
                        "{class-name}/{method-name}",
                        queryParameters(
                                parameterWithName("limit").description("페이지 제한"),
                                parameterWithName("offset").description("페이지 오프셋")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("id 값"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("[].estimateValue").type(JsonFieldType.NUMBER).description("추정 값")
                        )
                ));
    }

    @Test
    void postRestDocsTest() throws Exception {

        String name = "이름";
        String description = "생성";
        Double estimateValue = 1000.0;

        RestDocsRequestDTO request = RestDocsRequestDTO.builder()
                .name(name)
                .description(description)
                .estimateValue(estimateValue)
                .build();

        RestDocsResponseDTO response = RestDocsResponseDTO.builder()
                .id(777L)
                .name(name)
                .description(description)
                .estimateValue(estimateValue)
                .build();

        given(restDocsService.postRestDocs(request)).willReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/restdoc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("id").value(response.getId()))
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("description").value(description))
                .andExpect(jsonPath("estimateValue").value(estimateValue))
                .andDo(print())
                .andDo(MockMvcRestDocumentation.document(
                        "{class-name}/{method-name}",
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("estimateValue").type(JsonFieldType.NUMBER).description("추정 값")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("id 값"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("estimateValue").type(JsonFieldType.NUMBER).description("추정 값")
                        )));
    }

    @Test
    void putRestDocsTest() throws Exception {

        Long id = 1L;
        String name = "이름";
        String description = "수정";
        Double estimateValue = 1000.0;

        RestDocsRequestDTO request = RestDocsRequestDTO.builder()
                .name(name)
                .description(description)
                .estimateValue(estimateValue)
                .build();

        RestDocsResponseDTO response = RestDocsResponseDTO.builder()
                .id(id)
                .name(name)
                .description(description)
                .estimateValue(estimateValue)
                .build();

        given(restDocsService.putRestDocs(1L, request)).willReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/restdoc/{id}",1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(jsonPath("id").value(response.getId()))
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("description").value(description))
                .andExpect(jsonPath("estimateValue").value(estimateValue))
                .andDo(print())
                .andDo(MockMvcRestDocumentation.document(
                        "{class-name}/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id 값")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("estimateValue").type(JsonFieldType.NUMBER).description("추정 값")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("id 값"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상세 설명"),
                                fieldWithPath("estimateValue").type(JsonFieldType.NUMBER).description("추정 값")
                )));
    }

    @Test
    void deleteRestDocsTest() throws Exception {

        mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/restdoc/{id}", 1))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(MockMvcRestDocumentation.document(
                        "{class-name}/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id 값")
                        )
                ));
    }
}
