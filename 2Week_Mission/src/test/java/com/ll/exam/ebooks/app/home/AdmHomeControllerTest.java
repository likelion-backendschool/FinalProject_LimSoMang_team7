package com.ll.exam.ebooks.app.home;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AdmHomeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithUserDetails("user1")
    @DisplayName("관리자 메인 페이지(index)")
    void showIndex() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(get("/adm/"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(AdmHomeController.class))
                .andExpect(handler().methodName("showIndex"))
                .andExpect(redirectedUrl("/adm/home/main"));
    }

    @Test
    @WithUserDetails("user1")
    @DisplayName("관리자 메인 페이지")
    void showMain() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(get("/adm/home/main"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(AdmHomeController.class))
                .andExpect(handler().methodName("showMain"))
                .andExpect(content().string(containsString("관리자")));
    }

}