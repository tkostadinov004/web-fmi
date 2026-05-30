package bg.sofia.uni.fmi.issuetracker.repository;

import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketCommentRepository extends JpaRepository<TicketComment, String> {
    List<TicketComment> findAllByAuthor(User user);

    List<TicketComment> findAllByTicketAndAuthor(Ticket ticket, User user);

    Page<TicketComment> findAllByTicket(Ticket ticket, Pageable pageable);
}
