import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

// ======= Base abstract class =======
abstract class LibraryItem {
    int id;
    String title;
    String author;
    boolean available = true;
    String borrower;
    LocalDateTime dueDate;

    LibraryItem(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public boolean isAvailable() {
        return available;
    }

    public void borrow(String borrower, int days) {
        if (!available) {
            throw new IllegalStateException("Item id " + id + " is not available for borrowing.");
        }
        this.available = false;
        this.borrower = borrower;
        this.dueDate = LocalDateTime.now().plusDays(days);
        System.out.println("Borrowed item: " + this + " by " + borrower + " until " + dueDate);
    }

    public FineRecord returnItem() {
        if (available) {
            System.out.println("Item already available, no need to return.");
            return null;
        }
        available = true;
        long overdueDays = 0;
        FineRecord fine = null;

        if (dueDate != null && LocalDateTime.now().isAfter(dueDate)) {
            overdueDays = ChronoUnit.DAYS.between(dueDate, LocalDateTime.now());
            double fineAmt = overdueDays * 10.0;
            fine = new FineRecord(id, borrower, (int) overdueDays, fineAmt, LocalDateTime.now());
            System.out.println("Fine applied: " + fine);
        }
        System.out.println("Returned item: " + this + " by " + borrower + " at " + LocalDateTime.now());
        borrower = null;
        dueDate = null;
        return fine;
    }

    @Override
    public String toString() {
        String status = available ? "Available" : ("Borrowed by " + borrower + " until " + dueDate);
        return getClass().getSimpleName() + "[id=" + id + ", title='" + title + "', author='" + author + "', status=" + status + "]";
    }
}

// ======= Book class =======
class Book extends LibraryItem {
    int pages;

    Book(int id, String title, String author, int pages) {
        super(id, title, author);
        this.pages = pages;
    }

    @Override
    public String toString() {
        return super.toString() + " (pages=" + pages + ")";
    }
}

// ======= AudioBook class =======
class AudioBook extends LibraryItem {
    double hours;

    AudioBook(int id, String title, String author, double hours) {
        super(id, title, author);
        this.hours = hours;
    }

    public void play() {
        System.out.println("🎧 Playing audiobook '" + title + "' by " + author + " (approx. " + hours + "h)");
    }
}

// ======= EMagazine class =======
class EMagazine extends LibraryItem {
    String issue;
    boolean archived = false;

    EMagazine(int id, String title, String author, String issue) {
        super(id, title, author);
        this.issue = issue;
    }

    public void archive() {
        archived = true;
        System.out.println("📚 Archived E-Magazine '" + title + "' issue #" + issue + " at " + LocalDateTime.now());
    }

    @Override
    public String toString() {
        return super.toString() + " (issue=" + issue + ", archived=" + archived + ")";
    }
}

// ======= FineRecord class =======
class FineRecord {
    int itemId;
    String borrower;
    int daysOverdue;
    double amount;
    LocalDateTime at;

    FineRecord(int itemId, String borrower, int daysOverdue, double amount, LocalDateTime at) {
        this.itemId = itemId;
        this.borrower = borrower;
        this.daysOverdue = daysOverdue;
        this.amount = amount;
        this.at = at;
    }

    @Override
    public String toString() {
        return "FineRecord[itemId=" + itemId + ", borrower='" + borrower + "', daysOverdue=" + daysOverdue + ", amount=" + amount + ", at=" + at + "]";
    }
}

// ======= Main class =======
public class LibraNetDemo {
    static Map<Integer, LibraryItem> items = new HashMap<>();
    static List<FineRecord> fines = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // Initial Items
        items.put(1, new Book(1, "Clean Code", "Robert C. Martin", 464));
        items.put(2, new AudioBook(2, "Effective Java - Audio", "Joshua Bloch", 11.5));
        items.put(3, new EMagazine(3, "Nature Monthly", "Editorial Team", "2025"));
        items.put(4, new Book(4, "Half Girlfriend", "Chetan Bhagat", 260));
        items.put(5, new Book(5, "Wings of Fire", "A.P.J. Abdul Kalam", 180));
        items.put(6, new Book(6, "Malgudi Days", "R.K. Narayan", 245));
        items.put(7, new AudioBook(7, "Panchatantra Ki Kahaniyaan - Audio", "Vishnu Sharma", 5.75));
        items.put(8, new EMagazine(8, "Champak Monthly", "Delhi Press", "202509"));
        items.put(9, new EMagazine(9, "India Today", "Editorial Team", "202509"));

        while (true) {
            System.out.println("\n=== 📖 Pro Library Menu ===");
            System.out.println("1. Show All Items");
            System.out.println("2. Borrow Item");
            System.out.println("3. Return Item");
            System.out.println("4. Play AudioBook");
            System.out.println("5. Archive E-Magazine");
            System.out.println("6. Search by Type/Title");
            System.out.println("7. Show All Fines");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> showAllItems();
                case 2 -> borrowItem();
                case 3 -> returnItem();
                case 4 -> playAudio();
                case 5 -> archiveMag();
                case 6 -> search();
                case 7 -> showFines();
                case 0 -> { System.out.println("🚪 Exiting... Bye!"); return; }
                default -> System.out.println("❌ Invalid choice!");
            }
        }
    }

    static void showAllItems() {
        System.out.println("\n--- All Items ---");
        for (LibraryItem item : items.values()) {
            System.out.println(item);
        }
    }

    static void borrowItem() {
        System.out.print("Enter Item ID to borrow: ");
        int id = sc.nextInt(); sc.nextLine();
        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        System.out.print("Enter days to borrow: ");
        int days = sc.nextInt(); sc.nextLine();

        LibraryItem item = items.get(id);
        if (item == null) { System.out.println("❌ Item not found!"); return; }

        try { item.borrow(name, days); }
        catch (Exception e) { System.out.println("❌ Error: " + e.getMessage()); }
    }

    static void returnItem() {
        System.out.print("Enter Item ID to return: ");
        int id = sc.nextInt(); sc.nextLine();

        LibraryItem item = items.get(id);
        if (item == null) { System.out.println("❌ Item not found!"); return; }

        FineRecord fine = item.returnItem();
        if (fine != null) fines.add(fine);
    }

    static void playAudio() {
        System.out.print("Enter AudioBook ID: ");
        int id = sc.nextInt(); sc.nextLine();

        LibraryItem item = items.get(id);
        if (item instanceof AudioBook ab) ab.play();
        else System.out.println("❌ Not an audiobook!");
    }

    static void archiveMag() {
        System.out.print("Enter E-Magazine ID: ");
        int id = sc.nextInt(); sc.nextLine();

        LibraryItem item = items.get(id);
        if (item instanceof EMagazine mag) mag.archive();
        else System.out.println("❌ Not an e-magazine!");
    }

    static void search() {
        System.out.print("Enter keyword (type/title): ");
        String key = sc.nextLine().toLowerCase();
        for (LibraryItem item : items.values()) {
            if (item.getClass().getSimpleName().toLowerCase().contains(key) || item.title.toLowerCase().contains(key))
                System.out.println(item);
        }
    }

    static void showFines() {
        System.out.println("\n--- All Fines ---");
        if (fines.isEmpty()) System.out.println("✅ No fines recorded.");
        else for (FineRecord fine : fines) System.out.println(fine);
    }
}
