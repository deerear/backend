package com.deerear.deerear.service;

import com.deerear.deerear.dto.RestDocsRequestDTO;
import com.deerear.deerear.dto.RestDocsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestDocsService {

    public RestDocsResponseDTO getRestDocs(Long id){

        // TODO Using Repository
        return RestDocsResponseDTO.builder()
                .id(id)
                .name("이름")
                .description("단건 조회")
                .estimateValue(1000.0)
                .build();
    }

    public List<RestDocsResponseDTO> listRestDocs(Integer limit, Integer offset) {

        // TODO Using Repository, Pagination
        List<RestDocsResponseDTO> response = new ArrayList<RestDocsResponseDTO>();

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

        return response;
    }

    public RestDocsResponseDTO postRestDocs(RestDocsRequestDTO request) {

        // TODO Using Repository
        return RestDocsResponseDTO.builder()
                .id(777L)
                .name(request.getName())
                .description(request.getDescription())
                .estimateValue(request.getEstimateValue())
                .build();
    }

    public RestDocsResponseDTO putRestDocs(Long id, RestDocsRequestDTO request) {

        // TODO Using Repository
        return RestDocsResponseDTO.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .estimateValue(request.getEstimateValue())
                .build();
    }

    public void deleteRestDocs(Long id){

        // TODO Discuss about return value
    }

}
