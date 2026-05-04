package bg.sofia.uni.fmi.issuetracker.repository.ticket;

import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketCommentRepository extends JpaRepository<TicketComment, String> {

    List<TicketComment> findAllByTicket(Ticket ticket);

    List<TicketComment> findAllByAuthor(User user);

    List<TicketComment> findAllByTicketAndAuthor(Ticket ticket, User user);

    Optional<TicketComment> findByUuid(String uuid);

    List<TicketComment> getAllByTicketUuid(String ticketUuid);
}
