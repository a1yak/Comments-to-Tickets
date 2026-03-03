package comment.to.ticket.service;

import comment.to.ticket.model.*;
import comment.to.ticket.repository.CommentsRepository;
import comment.to.ticket.repository.TicketsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final AIAnalysisService aiAnalysisService;
    private final TicketsService ticketsService;

    public List<Comment> getAllComments(){
        return commentsRepository.findAll();
    }

    public Comment addComment(Comment comment){
        Comment savedComment = commentsRepository.save(comment);
        TicketDecision decision = aiAnalysisService.analyze(savedComment.getCommentText());
        if(decision.isCreateTicket()){
            ticketsService.createTicket(decision);
        }

        return savedComment;
    }
}
