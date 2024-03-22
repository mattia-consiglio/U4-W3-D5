package mattiaconsiglio.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import mattiaconsiglio.exceptions.RecordNotFoundException;
import mattiaconsiglio.library.Publication;

import java.util.List;

public class PublicationDAO {
    private final EntityManager em;


    public PublicationDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Publication publication) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(publication);
        transaction.commit();

        String publicationType = publication.getClass().getSimpleName();
        System.out.println(publicationType + " saved successfully!");
        System.out.println(publication);
    }

    public Publication getByID(long id) throws RecordNotFoundException {
        Publication publication = em.find(Publication.class, id);
        if (publication == null) {
            throw new RecordNotFoundException("id", id);
        }
        return publication;
    }

    public List<Publication> getPublicationByIsbn(long isbn) throws NoResultException {
        try {
            TypedQuery<Publication> query = em.createNamedQuery("getByISBN", Publication.class);
            query.setParameter("isbn", isbn);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public long getLastPublicationIsbn() {
        TypedQuery<Long> query = em.createQuery("SELECT p.isbn FROM Publication p ORDER BY p.isbn DESC LIMIT 1", Long.class);
        return query.getSingleResult();
    }
}
