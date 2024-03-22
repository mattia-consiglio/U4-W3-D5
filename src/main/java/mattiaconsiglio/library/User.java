package mattiaconsiglio.library;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "first_name")
    private String fisrtName;
    @Column(name = "last_name")
    private String lastName;
    private LocalDate birthday;
    @Column(name = "card_number")
    private long cardNumber;

    @OneToMany(mappedBy = "user")
    private List<Loan> loans;


    public User() {
    }

    public User(String fisrtName, String lastName, LocalDate birthday, long cardNumber) {
        this.fisrtName = fisrtName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.cardNumber = cardNumber;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fisrtName='" + fisrtName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthday=" + birthday +
                ", cardNumber=" + cardNumber +
                '}';
    }

    public String getFisrtName() {
        return fisrtName;
    }

    public void setFisrtName(String fisrtName) {
        this.fisrtName = fisrtName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(long cardNumber) {
        this.cardNumber = cardNumber;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }
}
