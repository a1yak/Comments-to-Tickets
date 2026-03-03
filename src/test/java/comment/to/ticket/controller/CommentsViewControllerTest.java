package comment.to.ticket.controller;

import static org.junit.jupiter.api.Assertions.*;


import comment.to.ticket.model.Comment;
import comment.to.ticket.service.CommentsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentsViewController.class)
class CommentsViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentsService commentsService;

    @Test
    void getIndex_returnsIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getIndex_addsEmptyNewCommentToModel() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("newComment", instanceOf(Comment.class)));
    }

    @Test
    void getIndex_submittedParamFalse_whenAbsent() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(model().attribute("submitted", false));
    }

    @Test
    void getIndex_submittedParamTrue_whenPresent() throws Exception {
        mockMvc.perform(get("/").param("submitted", "true"))
                .andExpect(model().attribute("submitted", true));
    }

    @Test
    void getIndex_submittedParamFalse_whenExplicitlyFalse() throws Exception {
        mockMvc.perform(get("/").param("submitted", "false"))
                .andExpect(model().attribute("submitted", false));
    }

    @Test
    void postComment_redirectsToRootWithSubmittedFlag() throws Exception {
        mockMvc.perform(post("/comments")
                        .param("commentText", "The login button is broken"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?submitted=true"));
    }

    @Test
    void postComment_callsServiceWithComment() throws Exception {
        mockMvc.perform(post("/comments")
                .param("commentText", "I cannot reset my password"));

        verify(commentsService, times(1)).addComment(any(Comment.class));
    }

    @Test
    void postComment_doesNotCallService_whenCommentTextIsEmpty() throws Exception {
        mockMvc.perform(post("/comments")
                        .param("commentText", ""))
                .andExpect(status().is3xxRedirection());

        verify(commentsService, times(1)).addComment(any(Comment.class));
    }
}