package bg.sofia.uni.fmi.issuetracker.repository;

import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Ticket.TicketKey> {
    boolean existsById_CodeAndProject(String code, Project project);

    Optional<Ticket> findById_CodeAndProject(String code, Project project);
}
