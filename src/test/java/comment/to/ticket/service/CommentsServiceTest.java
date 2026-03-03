package comment.to.ticket.service;

import comment.to.ticket.model.Comment;
import comment.to.ticket.model.TicketDecision;
import comment.to.ticket.repository.CommentsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentsServiceTest {

    @Mock
    private CommentsRepository commentsRepository;

    @Mock
    private AIAnalysisService aiAnalysisService;

    @Mock
    private TicketsService ticketsService;

    @InjectMocks
    private CommentsService commentsService;

    @Test
    void getAllComments_shouldReturnAllComments() {

        Comment comment1 = new Comment();
        comment1.setCommentText("First");

        Comment comment2 = new Comment();
        comment2.setCommentText("Second");

        when(commentsRepository.findAll())
                .thenReturn(List.of(comment1, comment2));

        List<Comment> result = commentsService.getAllComments();

        assertEquals(2, result.size());
        verify(commentsRepository, times(1)).findAll();
    }

    @Test
    void addComment_shouldSaveComment() {

        Comment input = new Comment();
        input.setCommentText("Test comment");

        Comment saved = new Comment();
        saved.setCommentText("Test comment");

        when(commentsRepository.save(input)).thenReturn(saved);

        TicketDecision decision = new TicketDecision();
        decision.setCreateTicket(false);

        when(aiAnalysisService.analyze("Test comment"))
                .thenReturn(decision);

        Comment result = commentsService.addComment(input);

        assertEquals("Test comment", result.getCommentText());

        verify(commentsRepository).save(input);
        verify(aiAnalysisService).analyze("Test comment");
        verify(ticketsService, never()).createTicket(any());
    }

    @Test
    void addComment_shouldCreateTicketWhenDecisionTrue() {

        Comment input = new Comment();
        input.setCommentText("App not working");

        Comment saved = new Comment();
        saved.setCommentText("App not working");

        when(commentsRepository.save(input)).thenReturn(saved);

        TicketDecision decision = new TicketDecision();
        decision.setCreateTicket(true);

        when(aiAnalysisService.analyze("App not working"))
                .thenReturn(decision);

        commentsService.addComment(input);

        verify(commentsRepository).save(input);
        verify(aiAnalysisService).analyze("App not working");
        verify(ticketsService, times(1)).createTicket(decision);
    }
}