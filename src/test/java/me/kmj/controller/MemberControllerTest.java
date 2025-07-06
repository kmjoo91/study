package me.kmj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.kmj.entity.Member;
import me.kmj.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllMembersShouldReturnMembers() throws Exception {
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getMemberByIdShouldReturnMember() throws Exception {
        mockMvc.perform(get("/api/members/test1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("test1"))
                .andExpect(jsonPath("$.name").value("김철수"));
    }

    @Test
    void createMemberShouldReturnCreatedMember() throws Exception {
        Member newMember = new Member("test3", "박민수", "남성", "010-5555-5555", "대구시 중구 중앙대로 789");
        
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test3"))
                .andExpect(jsonPath("$.name").value("박민수"));
    }
} 