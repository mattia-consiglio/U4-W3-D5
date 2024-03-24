package mattiaconsiglio.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import mattiaconsiglio.library.Book;
import mattiaconsiglio.library.Publication;

import java.util.List;

public class PublicationsDAO {
    private final EntityManager em;


    public PublicationsDAO(EntityManager em) {
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

    public List<Publication> getAll() {

        TypedQuery<Publication> query = em.createQuery("SELECT p FROM Publication p", Publication.class);
        return query.getResultList();
    }

    public List<Publication> getByIsbn(long isbn) {

        TypedQuery<Publication> query = em.createNamedQuery("getByISBN", Publication.class);
        query.setParameter("isbn", isbn);
        return query.getResultList();
    }

    public List<Publication> getByPublishYear(int publishYear) {

        TypedQuery<Publication> query = em.createQuery("SELECT p FROM Publication p WHERE publishYear = :publishYear", Publication.class);
        query.setParameter("publishYear", publishYear);
        return query.getResultList();
    }

    public long getLastIsbn() {
        TypedQuery<Long> query = em.createQuery("SELECT p.isbn FROM Publication p ORDER BY p.isbn DESC LIMIT 1", Long.class);
        return query.getSingleResult();
    }

    public void findByIsbnAndDelete(int isbn) {
        List<Publication> publicationList = getByIsbn(isbn);
        if (publicationList.size() == 0) {
            System.err.println("No publication found with the ISBN " + isbn);
        } else {
            Publication publication = publicationList.get(0);
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            em.remove(publication);
            transaction.commit();
            System.out.println("Publication with ISBN " + isbn + " deleted");
        }
    }

    public List<Book> getByAuthor(String author) {
        TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b WHERE author = :author", Book.class);
        query.setParameter("author", author);
        return query.getResultList();
    }

    public List<Publication> getByTitle(String title) {
        TypedQuery<Publication> query = em.createQuery("SELECT p FROM Publication p WHERE LOWER(p.title) LIKE LOWER(:title)", Publication.class);
        query.setParameter("title", "%" + title + "%");
        return query.getResultList();
    }

    public List<Publication> getFirst() {
        TypedQuery<Publication> query = em.createQuery("SELECT p.id FROM Publication p ORDER BY p.id LIMIT 1", Publication.class);
        return query.getResultList();
    }
}
