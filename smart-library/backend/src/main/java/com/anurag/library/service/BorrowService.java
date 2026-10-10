package com.anurag.library.service;

import com.anurag.library.entity.Book;
import com.anurag.library.entity.BorrowRecord;
import com.anurag.library.entity.Student;
import com.anurag.library.enums.Role;
import com.anurag.library.repository.BookRepository;
import com.anurag.library.repository.BorrowRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowService {

    @Autowired private BorrowRecordRepository recordRepo;
    @Autowired private BookRepository bookRepo;
    @Autowired private SecurityUtil securityUtil;

    // The current user borrows a book for themselves. There is no "borrow for
    // someone else" path for students - only admins managing records directly
    // in the database could do that, by design.
    public BorrowRecord borrow(Long bookId) {
        Student me = securityUtil.currentStudent();
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalArgumentException("No copies of this book are available right now");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepo.save(book);

        BorrowRecord record = new BorrowRecord();
        record.setBook(book);
        record.setStudent(me);
        return recordRepo.save(record);
    }

    // A student may only return their OWN record; an admin may return any record.
    // The check is done here, server-side, rather than trusted from the client.
    public BorrowRecord returnBook(Long recordId) {
        Student me = securityUtil.currentStudent();
        BorrowRecord record = recordRepo.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Borrow record not found"));

        boolean isOwner = record.getStudent().getId().equals(me.getId());
        boolean isAdmin = me.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("You can only return your own borrowed books");
        }
        if (record.isReturned()) {
            throw new IllegalArgumentException("This book was already returned");
        }

        record.setReturned(true);
        record.setReturnedAt(LocalDateTime.now());
        recordRepo.save(record);

        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepo.save(book);

        return record;
    }

    // "my" history - studentId comes from the token via SecurityUtil, never from
    // a URL parameter, so one student can never query another student's records.
    public List<BorrowRecord> myHistory() {
        Student me = securityUtil.currentStudent();
        return recordRepo.findByStudentIdOrderByBorrowedAtDesc(me.getId());
    }

    public List<BorrowRecord> allRecords() {
        return recordRepo.findAllByOrderByBorrowedAtDesc();
    }
}
