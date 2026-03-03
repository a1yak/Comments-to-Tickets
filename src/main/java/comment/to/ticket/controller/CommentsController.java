package comment.to.ticket.controller;

import comment.to.ticket.model.Comment;
import comment.to.ticket.service.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comments")
public class CommentsController {

    private final CommentsService commentsService;

    @GetMapping()
    public ResponseEntity<List<Comment>> getComments(){

        List<Comment> comments = commentsService.getAllComments();
        return ResponseEntity.ok(comments);

    }

    @PostMapping()
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment){

        Comment savedComment = commentsService.addComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);

    }
}
