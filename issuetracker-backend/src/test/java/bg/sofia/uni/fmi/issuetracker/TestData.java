package bg.sofia.uni.fmi.issuetracker;

import bg.sofia.uni.fmi.issuetracker.model.FeatureFlag;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auth.Token;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketComment;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class TestData {
    public static final User TEST_USER = User.UserBuilder.newBuilder()
            .firstName("FirstName")
            .lastName("LastName")
            .username("user")
            .password("encryptedPass")
            .email("email@email.com")
            .build();
    public static final User TEST_USER_2 = User.UserBuilder.newBuilder()
            .firstName("FirstName2")
            .lastName("LastName2")
            .username("user2")
            .password("encryptedPass2")
            .email("email2@email.com")
            .build();
    public static final Project TEST_PROJECT =
            new Project(UUID.randomUUID().toString(), "testProject", new HashSet<>(), new ArrayList<>());
    public static final Project TEST_PROJECT_2 =
            new Project(UUID.randomUUID().toString(), "testProject2", new HashSet<>(), new ArrayList<>());
    public static final Token TEST_TOKEN =
            new Token("testToken", TEST_USER);
    public static final FeatureFlag TEST_FEATURE_FLAG_1 = new FeatureFlag("FF_1", true);
    public static final FeatureFlag TEST_FEATURE_FLAG_2 = new FeatureFlag("FF_2", false);
    public static final List<FeatureFlag> FEATURE_FLAGS = List.of(TEST_FEATURE_FLAG_1, TEST_FEATURE_FLAG_2);

    public static final Ticket TEST_TICKET =
            new Ticket("Ticket-1", "testTicket", "testDescription", TicketPriority.HIGH,
                    TicketStatus.IN_PROGRESS, LocalDateTime.now().plusDays(1), TEST_PROJECT, TEST_USER, new ArrayList<>());

    public static final TicketComment TEST_TICKET_COMMENT =
            new TicketComment(UUID.randomUUID().toString(), TEST_TICKET, TEST_USER, "content", LocalDateTime.now().minusHours(2));

    static {
        TEST_PROJECT.addTicket(TEST_TICKET);

        TEST_TICKET.addTicketComment(TEST_TICKET_COMMENT);
    }
}
