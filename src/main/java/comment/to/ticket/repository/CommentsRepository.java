package comment.to.ticket.repository;

import comment.to.ticket.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Integer> {

}
