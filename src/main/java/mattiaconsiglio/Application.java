package mattiaconsiglio;

import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import mattiaconsiglio.dao.PublicationDAO;
import mattiaconsiglio.library.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static mattiaconsiglio.library.Publication.askAndVerifyInt;

public class Application {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("library");

    public static void main(String[] args) {
        EntityManager em = emf.createEntityManager();
        Scanner scanner = new Scanner(System.in);
        PublicationDAO publicationDAO = new PublicationDAO(em);


        LibrarySupplier<Book> bookLibrarySupplier = (long isbn) -> {
            Faker faker = new Faker();
            int year = new Random().nextInt(1900, LocalDate.now().getYear() + 1);
            int pages = new Random().nextInt(10, 5000);
            return new Book(isbn, faker.book().title(), year, pages, faker.book().author(), faker.book().genre());
        };

        LibrarySupplier<Magazine> magazineLibrarySupplier = (long isbn) -> {
            Faker faker = new Faker();
            int year = new Random().nextInt(1900, 2025);
            int pages = new Random().nextInt(10, 30);
            Periodicity periodicity = Periodicity.values()[new Random().nextInt(Periodicity.values().length)];
            return new Magazine(isbn, faker.book().title(), year, pages, periodicity);
        };


        // Adding random publications to DB
//        GenerateAndAddPublications(bookLibrarySupplier, magazineLibrarySupplier, publicationDAO);

        while (true) {
            switch (mainMenu(scanner)) {
                case 0: {
                    System.out.println("Exiting");
                    em.close();
                    return;
                }
                case 1: {
                    if (chooseElement(scanner, "add") == 1) {
                        Book.add(publicationDAO, scanner, bookLibrarySupplier);
                    } else {
                        Magazine.add(publicationDAO, scanner, magazineLibrarySupplier);
                    }
                    break;
                }
                case 2: {
                    int isbn = askAndVerifyInt("Insert book ISBN (min 8 digits)", scanner, 10_000_000, 999_999_999);
                    publicationDAO.findByIsbnAndDelete(isbn);
                    break;
                }
                case 3: {
                    int isbn = askAndVerifyInt("Insert publication ISBN (min 8 digits)", scanner, 10_000_000, 999_999_999);
                    List<Publication> publicationsByIsbn = publicationDAO.getByIsbn(isbn);
                    if (publicationsByIsbn.size() == 0) {
                        System.out.println("No publications found for the ISBN " + isbn);
                    } else {
                        System.out.println("Publication(s) with the ISBN " + isbn + " (should be one)");
                        publicationsByIsbn.forEach(System.out::println);
                    }
                    break;
                }
                case 4: {
                    int publishYear = askAndVerifyInt("Insert publish year of publication", scanner, 1900, 2025);
                    List<Publication> publicationsByPublishYear = publicationDAO.getByPublishYear(publishYear);
                    if (publicationsByPublishYear.size() == 0) {
                        System.out.println("No publications found for the year " + publishYear);
                    } else {
                        System.out.println("Publications published in the year " + publishYear);
                        publicationsByPublishYear.forEach(System.out::println);
                    }
                    break;
                }
                case 5: {
                    System.out.println("Insert an author name");
                    String author = scanner.nextLine();
                    List<Book> booksByAuthor = publicationDAO.getByAuthor(author);
                    if (booksByAuthor.size() == 0) {
                        System.out.println("No books found for the author " + author);
                    } else {
                        System.out.println("Books written by " + author);
                        booksByAuthor.forEach(System.out::println);
                    }
                    break;
                }
                default: {
                    continue;
                }
            }
        }


    }

    /**
     * Generate and add Publications (books and Magazines) to database
     *
     * @param bookLibrarySupplier
     * @param magazineLibrarySupplier
     * @param publicationDAO
     */
    public static void GenerateAndAddPublications(LibrarySupplier<Book> bookLibrarySupplier, LibrarySupplier<Magazine> magazineLibrarySupplier, PublicationDAO publicationDAO) {
        int publicationsNumber = 100;
        int currIsbn = 100_000_000; //shortest ISBN possibile

        for (int i = 0; i < publicationsNumber; i++) {
            currIsbn += new Random().nextInt(10, 1000);

            if (i < publicationsNumber / 2) {
                publicationDAO.save(bookLibrarySupplier.get(currIsbn));
            } else {
                publicationDAO.save(magazineLibrarySupplier.get(currIsbn));
            }
        }
    }

    public static int mainMenu(Scanner scanner) {

        while (true) {
            System.out.println("--------- Main menu ---------");
            System.out.println();
            System.out.println("Choose an option number");
            System.out.println("1. Add publication to library");
            System.out.println("2. Remove element by ISBN");
            System.out.println("3. Search by ISBN");
            System.out.println("4. Search by publish year");
            System.out.println("5. Search by author");
            System.out.println("6. Search by title (or partial)");
            System.out.println("7. Search currently loaned publications by card number");
            System.out.println("8. Search expired not returned loans");
            System.out.println("0. Exit");


            String option = scanner.nextLine();

            switch (option) {
                case "0", "1", "2", "3", "4", "5", "6", "7", "8": {
                    return Integer.parseInt(option);
                }
                default:
                    System.err.println("Option not valid. Select a valid option number");
            }
        }
    }

    public static int chooseElement(Scanner scanner, String action) {
        while (true) {
            System.out.println();
            System.out.println("What element you want to" + action + "?");
            System.out.println("1. Book");
            System.out.println("2. Magazine");
            String option = scanner.nextLine();
            if (option.equals("1") || option.equals("2")) {
                return Integer.parseInt(option);
            } else {
                System.err.println("Option not valid. Select a valid option number");
            }
        }
    }
}
