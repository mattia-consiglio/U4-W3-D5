package mattiaconsiglio.library;

@FunctionalInterface
public interface LibrarySupplier<T> {

    public T get(int isbn);
}
