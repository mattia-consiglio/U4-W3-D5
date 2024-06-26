package mattiaconsiglio.library;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import mattiaconsiglio.dao.PublicationsDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

@Entity
@DiscriminatorValue("magazine")
public class Magazine extends Publication<Magazine> {
    @Enumerated(EnumType.STRING)
    private Periodicity periodicity;


    public Magazine() {
    }

    public Magazine(long isbn, String title, int publishYear, int pages, Periodicity periodicity) {
        super(isbn, title, publishYear, pages);
        this.periodicity = periodicity;
    }


    public static void add(PublicationsDAO publicationsDAO, Scanner scanner, LibrarySupplier<Magazine> magazineLibrarySupplier) {
        Magazine magazine = null;

        System.out.println("Do you want to :");
        System.out.println("1. Add a book manually");
        System.out.println("2. Add a book automatically with random values");

        int option = askAndVerifyInt("Choose an option", scanner, 1, 2);
        if (option == 1) {
            int isbn = askAndVerifyInt("Insert book ISBN (min 8 digits)", scanner, 10_000_000, 999_999_999);

            while (true) {

                int finalIsbn = isbn;
                List<Publication> publication = publicationsDAO.getByIsbn(finalIsbn);
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
            Periodicity periodicity;


            System.out.println("Choose magazine periodicity:");
            for (int i = 0; i < Periodicity.values().length; i++) {
                System.out.println(i + 1 + ". " + Periodicity.values()[i]);
            }
            int n = askAndVerifyInt("Insert periodicity number", scanner, 1, Periodicity.values().length);
            periodicity = Periodicity.values()[n - 1];

            magazine = new Magazine(isbn, tile, year, pages, periodicity);
        }
        if (option == 2) {
            long newIsbn = publicationsDAO.getLastIsbn() + new Random().nextInt(10, 1000);
            magazine = magazineLibrarySupplier.get(newIsbn);

        }
        publicationsDAO.save(magazine);


    }

    public Periodicity getPeriodicity() {
        return periodicity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Magazine magazine)) return false;
        return isbn == magazine.isbn && Objects.equals(title, magazine.title) && publishYear == magazine.publishYear && pages == magazine.pages && periodicity == magazine.periodicity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodicity);
    }

    @Override
    public String toString() {
        return "Magazine{" +
                "id=" + id +
                ", isbn=" + isbn +
                ", title='" + title + '\'' +
                ", publishYear=" + publishYear +
                ", pages=" + pages +
                ", periodicity=" + periodicity +
                '}';
    }
}
