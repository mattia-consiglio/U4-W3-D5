package mattiaconsiglio;

import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import mattiaconsiglio.dao.LoansDAO;
import mattiaconsiglio.dao.PublicationsDAO;
import mattiaconsiglio.dao.UsersDAO;
import mattiaconsiglio.library.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static mattiaconsiglio.library.Publication.askAndVerifyInt;

public class Application {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("library");
    private static final EntityManager em = emf.createEntityManager();
    private static final Faker faker = new Faker();
    private static final PublicationsDAO publicationsDAO = new PublicationsDAO(em);
    private static final UsersDAO usersDAO = new UsersDAO(em);
    private static final LoansDAO loansDAO = new LoansDAO(em);

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        LibrarySupplier<Book> bookLibrarySupplier = (long isbn) -> {
            int year = new Random().nextInt(1900, LocalDate.now().getYear() + 1);
            int pages = new Random().nextInt(10, 5000);
            return new Book(isbn, faker.book().title(), year, pages, faker.book().author(), faker.book().genre());
        };

        LibrarySupplier<Magazine> magazineLibrarySupplier = (long isbn) -> {
            int year = new Random().nextInt(1900, 2025);
            int pages = new Random().nextInt(10, 30);
            Periodicity periodicity = Periodicity.values()[new Random().nextInt(Periodicity.values().length)];
            return new Magazine(isbn, faker.book().title(), year, pages, periodicity);
        };

        LibrarySupplier<User> userSupplier = (long cardNumber) -> {
            LocalDate birthDay = faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return new User(faker.name().firstName(), faker.name().lastName(), birthDay, cardNumber);
        };

        LoanSupplier loanSupplier = (User user, Publication publication) -> {

            LocalDate startDate = LocalDate.now();
            if (faker.bool().bool()) {
                startDate = startDate.plusDays(faker.number().numberBetween(0, 120));
            } else {
                startDate = startDate.minusDays(faker.number().numberBetween(0, 120));
            }
            LocalDate endDate = startDate;
            if (faker.bool().bool()) {
                endDate = endDate.plusDays(faker.number().numberBetween(1, 120));
            } else {
                endDate = null;
            }

            return new Loan(user, publication, startDate, endDate);
        };

        GenerateData(bookLibrarySupplier, magazineLibrarySupplier, userSupplier, loanSupplier);

        while (true) {
            switch (mainMenu(scanner)) {
                case 0: {
                    System.out.println("Exiting");
                    em.close();
                    return;
                }
                case 1: {
                    if (chooseElement(scanner, "add") == 1) {
                        Book.add(publicationsDAO, scanner, bookLibrarySupplier);
                    } else {
                        Magazine.add(publicationsDAO, scanner, magazineLibrarySupplier);
                    }
                    break;
                }
                case 2: {
                    int isbn = askAndVerifyInt("Insert book ISBN (min 8 digits)", scanner, 10_000_000, 999_999_999);
                    publicationsDAO.findByIsbnAndDelete(isbn);
                    break;
                }
                case 3: {
                    int isbn = askAndVerifyInt("Insert publication ISBN (min 8 digits)", scanner, 10_000_000, 999_999_999);
                    List<Publication> publicationsByIsbn = publicationsDAO.getByIsbn(isbn);
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
                    List<Publication> publicationsByPublishYear = publicationsDAO.getByPublishYear(publishYear);
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
                    List<Book> booksByAuthor = publicationsDAO.getByAuthor(author);
                    if (booksByAuthor.size() == 0) {
                        System.out.println("No books found for the author " + author);
                    } else {
                        System.out.println("Books written by " + author);
                        booksByAuthor.forEach(System.out::println);
                    }
                    break;
                }
                case 6: {
                    System.out.println("Insert a publication title or part of that");
                    String title = scanner.nextLine();
                    List<Publication> publicationsByTitle = publicationsDAO.getByTitle(title);
                    if (publicationsByTitle.size() == 0) {
                        System.out.println("No publication found that contains \"" + title + "\" in the title");
                    } else {
                        System.out.println("Publication that contains \"" + title + "\" in the title");
                        publicationsByTitle.forEach(System.out::println);
                    }
                    break;
                }
                case 7: {
                    int cardNumber = askAndVerifyInt("Insert card number", scanner, 1, 1000);
                    List<Publication> publications = loansDAO.getLoanedPublications(cardNumber);
                    publications.forEach(System.out::println);
                }
                case 8: {
                    List<Loan> loans = loansDAO.getExpiredNotReturnedLoans();
                    loans.forEach(System.out::println);
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
     */
    public static void GenerateAndAddPublications(LibrarySupplier<Book> bookLibrarySupplier, LibrarySupplier<Magazine> magazineLibrarySupplier) {
        int publicationsNumber = 100;
        int currIsbn = 100_000_000; //shortest ISBN possibile

        for (int i = 0; i < publicationsNumber; i++) {
            currIsbn += new Random().nextInt(10, 1000);

            if (i < publicationsNumber / 2) {
                publicationsDAO.save(bookLibrarySupplier.get(currIsbn));
            } else {
                publicationsDAO.save(magazineLibrarySupplier.get(currIsbn));
            }
        }
    }

    /**
     * Generate and add Users (books and Magazines) to database
     *
     * @param userSupplier
     */
    public static void GenerateAndAddUsers(LibrarySupplier<User> userSupplier) {
        int usersNumber = 100;
        for (int i = 0; i < usersNumber; i++) {
            usersDAO.save(userSupplier.get(i + 1));
        }
    }

    public static void GenerateAndAddLoans(LoanSupplier loanSupplier) {
        int loansNumber = 200;
        List<User> users = usersDAO.getAll();
        List<Publication> publications = publicationsDAO.getAll();
        Faker faker = new Faker();
        for (int i = 0; i < loansNumber; i++) {
            User user = users.get(faker.number().numberBetween(0, users.size() - 1));
            Publication publication = publications.get(faker.number().numberBetween(0, publications.size() - 1));
            loansDAO.save(loanSupplier.get(user, publication));
        }
    }

    public static void GenerateData(LibrarySupplier<Book> bookLibrarySupplier, LibrarySupplier<Magazine> magazineLibrarySupplier, LibrarySupplier<User> userSupplier, LoanSupplier loanSupplier) {
        if (publicationsDAO.getFirst().isEmpty()) {
            // Adding random publications to DB
            GenerateAndAddPublications(bookLibrarySupplier, magazineLibrarySupplier);
        }

        if (usersDAO.getFirst().isEmpty()) {
            //Adding random users
            GenerateAndAddUsers(userSupplier);
        }

        if (loansDAO.getFirst().isEmpty()) {
            //Adding random loans
            GenerateAndAddLoans(loanSupplier);
        }
    }

    /**
     * Display main menu
     *
     * @param scanner
     * @return
     */
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
