package mattiaconsiglio.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import mattiaconsiglio.library.Loan;

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


}
