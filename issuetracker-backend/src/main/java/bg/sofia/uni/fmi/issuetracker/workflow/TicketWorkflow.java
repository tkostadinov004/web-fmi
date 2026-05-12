package bg.sofia.uni.fmi.issuetracker.workflow;

import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;

import java.util.Map;
import java.util.Set;

public class TicketWorkflow {

    private static final Map<TicketStatus, Set<TicketStatus>> ALLOWED_TRANSITIONS =
        Map.of(
            TicketStatus.TO_DO,
            Set.of(TicketStatus.IN_PROGRESS),

            TicketStatus.IN_PROGRESS,
            Set.of(TicketStatus.TO_DO, TicketStatus.IN_REVIEW),

            TicketStatus.IN_REVIEW,
            Set.of(TicketStatus.IN_PROGRESS, TicketStatus.DONE),

            TicketStatus.DONE,
            Set.of()
        );

    private TicketWorkflow() {
    }

    public static boolean isTransitionAllowed(TicketStatus from, TicketStatus to) {
        return ALLOWED_TRANSITIONS
            .getOrDefault(from, Set.of())
            .contains(to);
    }
}
