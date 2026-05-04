package bg.sofia.uni.fmi.issuetracker.repository.ticket;

import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, String> {

    List<Ticket> findAllByProject(Project project);

    List<Ticket> findAllByAssignee(User assignee);

    List<Ticket> findAllByTicketStatus(TicketStatus ticketStatus);

    List<Ticket> findAllByTicketPriority(TicketPriority ticketPriority);

    List<Ticket> findAllByProjectAndTicketPriority(Project project, TicketPriority ticketPriority);

    List<Ticket> findAllByProjectAndTicketStatus(Project project, TicketStatus ticketStatus);

    List<Ticket> findAllByProjectAndAssignee(Project project, User assignee);

    Optional<Ticket> findByUuid(String uuid);

    Optional<Ticket> findByUuidAndProjectUuid(String uuid, String project_uuid);

    @Query("""
            SELECT t FROM Ticket t
            WHERE t.project.uuid = :projectUuid
              AND (:status IS NULL OR t.ticketStatus = :status)
              AND (:priority IS NULL OR t.ticketPriority = :priority)
              AND (:assigneeUsername IS NULL OR t.assignee.username = :assigneeUsername)
        """)
    List<Ticket> findByTicketStatusAndTicketPriorityAndAssigneeUsername(
        TicketStatus ticketStatus, TicketPriority ticketPriority, String assigneeUsername);
}
