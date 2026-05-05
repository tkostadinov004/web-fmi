package bg.sofia.uni.fmi.issuetracker.model.sprint;

// Unfinished ( made it so I can reference it in the tickets and dtos )

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sprints")
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid")
    private String uuid;

    public Sprint() {
    }

    public String getUuid() {
        return uuid;
    }
}
