package comment.to.ticket.controller;

import comment.to.ticket.model.Comment;
import comment.to.ticket.service.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CommentsViewController {

    private final CommentsService commentsService;

    @GetMapping("/")
    public String viewComments(
            Model model,
            @RequestParam(value = "submitted", required = false) Boolean submitted
    ) {
        model.addAttribute("newComment", new Comment());
        model.addAttribute("submitted", Boolean.TRUE.equals(submitted));
        return "index";
    }

    @PostMapping("/comments")
    public String addComment(@ModelAttribute Comment newComment) {
        commentsService.addComment(newComment);
        // redirect with ?submitted=true so the success banner shows
        return "redirect:/?submitted=true";
    }
}
