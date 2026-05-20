package bg.sofia.uni.fmi.issuetracker.repository.ticket;

import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    List<Ticket> findAllByAssigneeAndTicketStatusAndTicketPriority(User assignee, TicketStatus ticketStatus, TicketPriority ticketPriority);

    boolean existsByProjectAndCode(Project project, String code);
}
