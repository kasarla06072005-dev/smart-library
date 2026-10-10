package com.anurag.library.service;

import com.anurag.library.dto.BookDTO;
import com.anurag.library.entity.Book;
import com.anurag.library.repository.BookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository repo;

    public List<Book> search(String q) {
        if (q == null || q.isBlank()) return repo.findAll();
        return repo.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(q, q);
    }

    public Book getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }

    public Book create(BookDTO dto) {
        if (repo.existsByIsbn(dto.getIsbn())) {
            throw new IllegalArgumentException("A book with this ISBN already exists");
        }
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setCategory(dto.getCategory());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getTotalCopies());
        return repo.save(book);
    }

    public Book update(Long id, BookDTO dto) {
        Book book = getById(id);
        int borrowedOut = book.getTotalCopies() - book.getAvailableCopies();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setCategory(dto.getCategory());
        book.setTotalCopies(dto.getTotalCopies());
        // keep "copies currently out" consistent if the admin changes the total
        book.setAvailableCopies(Math.max(0, dto.getTotalCopies() - borrowedOut));
        return repo.save(book);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Book not found");
        repo.deleteById(id);
    }
}
