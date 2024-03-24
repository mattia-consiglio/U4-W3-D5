package mattiaconsiglio.library;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "estimated_end_date", nullable = false)
    private LocalDate estimatedEndDate;
    @Column(name = "real_end_date")
    private LocalDate realEndDate;


    public Loan() {
    }

    public Loan(User user, Publication publication, LocalDate startDate, LocalDate realEndDate) {
        this.user = user;
        this.publication = publication;
        this.startDate = startDate;
        this.estimatedEndDate = startDate.plusDays(30);
        this.realEndDate = realEndDate;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Publication getPublication() {
        return publication;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEstimatedEndDate() {
        return estimatedEndDate;
    }

    public LocalDate getRealEndDate() {
        return realEndDate;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", user=" + user +
                ", publication=" + publication +
                ", startDate=" + startDate +
                ", estimatedEndDate=" + estimatedEndDate +
                ", realEndDate=" + realEndDate +
                '}';
    }
}
