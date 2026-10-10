package com.anurag.library.seed;

import com.anurag.library.entity.Book;
import com.anurag.library.repository.BookRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class BookSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BookSeeder.class);

    private final BookRepository bookRepository;

    public BookSeeder(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) {
        if (bookRepository.count() > 0) {
            return;
        }

        List<Book> initialBooks = List.of(
            createBook("Clean Code: A Handbook of Agile Software Craftsmanship", "Robert C. Martin", "978-0132350884", "Computer Science", 5, 5),
            createBook("The Pragmatic Programmer", "David Thomas, Andrew Hunt", "978-0135957059", "Computer Science", 4, 4),
            createBook("Design Patterns: Elements of Reusable Object-Oriented Software", "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", "978-0201633610", "Computer Science", 3, 3),
            createBook("Introduction to Algorithms", "Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Clifford Stein", "978-0262046305", "Computer Science", 6, 6),
            createBook("Artificial Intelligence: A Modern Approach", "Stuart Russell, Peter Norvig", "978-0134610993", "Artificial Intelligence", 4, 4),
            createBook("To Kill a Mockingbird", "Harper Lee", "978-0061120084", "Classic Literature", 5, 5),
            createBook("1984", "George Orwell", "978-0451524935", "Classic Literature", 7, 7),
            createBook("The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", "Classic Literature", 4, 4),
            createBook("A Brief History of Time", "Stephen Hawking", "978-0553380163", "Science", 5, 5),
            createBook("Sapiens: A Brief History of Humankind", "Yuval Noah Harari", "978-0062316097", "History", 6, 6)
        );

        bookRepository.saveAll(initialBooks);
        log.info("Seeded {} initial books into the library catalog.", initialBooks.size());
    }

    private Book createBook(String title, String author, String isbn, String category, int total, int available) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setCategory(category);
        book.setTotalCopies(total);
        book.setAvailableCopies(available);
        return book;
    }
}
