package mattiaconsiglio.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import mattiaconsiglio.library.Loan;
import mattiaconsiglio.library.Publication;

import java.time.LocalDate;
import java.util.List;

public class LoansDAO {
    private final EntityManager em;

    public LoansDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Loan loan) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(loan);
        transaction.commit();
        System.out.println("Loan  " + loan.getId() + " saved successfully!");
        System.out.println(loan);
    }

    public List<Publication> getLoanedPublications(long cardNumber) {
        TypedQuery<Publication> query = em.createQuery("SELECT l.publication FROM Loan l " +
                "WHERE l.user.cardNumber = :cardNumber AND l.realEndDate > :now", Publication.class);

        query.setParameter("cardNumber", cardNumber);
        query.setParameter("now", LocalDate.now());
        return query.getResultList();
    }

    public List<Loan> getExpiredNotReturnedLoans() {
        TypedQuery<Loan> query = em.createQuery("SELECT l FROM Loan l WHERE "
                        + "l.realEndDate IS NULL "
                        + "AND :now > l.estimatedEndDate"
                , Loan.class);
        query.setParameter("now", LocalDate.now());
        return query.getResultList();
    }

    public List<Loan> getFirst() {
        TypedQuery<Loan> query = em.createQuery("SELECT l.id FROM Loan l ORDER BY l.id LIMIT 1", Loan.class);
        return query.getResultList();
    }
}
