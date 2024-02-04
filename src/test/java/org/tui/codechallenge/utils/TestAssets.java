package org.tui.codechallenge.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.tui.codechallenge.services.pojo.Owner;
import org.tui.codechallenge.services.pojo.RepositoryDto;

import java.util.Collections;
import java.util.List;

public class TestAssets {

    public static List<RepositoryDto> testEmptyList(){
       return Collections.emptyList();

    }

    public static RepositoryDto getResponseDto( String username){
        Owner owner =new Owner();
        owner.setLogin(username);
        RepositoryDto dto = new RepositoryDto(); // Set fields as necessary
        dto.setName("non-forked-repo");
        dto.setOwner(owner);

        return dto;
    }

    public static String getValidResponseEntityString(){
        return "[{\"id\":1,\"name\":\"non-forked-repo\",\"full_name\":\"testUser/non-forked-repo\",\"fork\":false,\"owner\":{\"login\":\"testUser\"}}]";

    }

    public static String getValidResponseEntityString2(){
        return "[{\"name\":\"repo1\",\"fork\":false}]";
    }






}
