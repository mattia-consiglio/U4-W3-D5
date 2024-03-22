package mattiaconsiglio.library;

public interface LoanSupplier {
    public Loan get(User user, Publication publication);
}
