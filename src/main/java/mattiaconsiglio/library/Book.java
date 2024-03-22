package mattiaconsiglio.library;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import mattiaconsiglio.dao.PublicationDAO;

import java.time.LocalDate;
import java.util.*;

@Entity
@DiscriminatorValue("book")
public class Book extends Publication {
    private String author;
    private String genre;

    public Book() {
    }

    public Book(long isbn, String title, int publishYear, int pages, String author, String genre) {
        super(isbn, title, publishYear, pages);
        this.author = author;
        this.genre = genre;
    }


    public static void add(PublicationDAO publicationDAO, Scanner scanner, LibrarySupplier<Book> bookLibrarySupplier) {
        Book book = null;
        System.out.println("Do you want to :");
        System.out.println("1. Add a book manually");
        System.out.println("2. Add a book automatically with random values");

        int option = askAndVerifyInt("Choose an option", scanner, 1, 2);
        if (option == 1) {
            int isbn = askAndVerifyInt("Insert book ISBN (min 8 digits)", scanner, 10_000_000, 999_999_999);

            while (true) {

                long finalIsbn = isbn;
                List<Publication> publication = publicationDAO.getByIsbn(finalIsbn);
                if (!publication.isEmpty()) {
                    System.err.println("Error: ISBN already present in the library");
                    isbn = askAndVerifyInt("Insert book ISBN", scanner, 10_000_000, 999_999_999);
                } else {
                    break;
                }
            }

            System.out.println("Insert book title:");
            String tile = scanner.nextLine();

            int year = askAndVerifyInt("Insert book publish year", scanner, 1900, LocalDate.now().getYear() + 1);

            int pages = askAndVerifyInt("Insert book pages", scanner, 1, 10000);
            String author = "";
            while (Objects.equals(author, "")) {
                System.out.println("Insert book author:");
                author = scanner.nextLine();
            }
            String genre = "";
            while (Objects.equals(genre, "")) {
                System.out.println("Insert book genre:");
                genre = scanner.nextLine();
            }


            book = new Book(isbn, tile, year, pages, author, genre);
        }
        if (option == 2) {
            long newIsbn = publicationDAO.getLastIsbn() + new Random().nextInt(10, 1000);
            book = bookLibrarySupplier.get(newIsbn);
        }
        publicationDAO.save(book);


    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public static void remove(Set<Publication> library, Scanner scanner) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(author, book.author) && Objects.equals(genre, book.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, genre);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn=" + isbn +
                ", title='" + title + '\'' +
                ", publishYear=" + publishYear +
                ", pages=" + pages +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                '}';
    }
}
