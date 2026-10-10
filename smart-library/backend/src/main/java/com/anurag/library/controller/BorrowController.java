package com.anurag.library.controller;

import com.anurag.library.entity.BorrowRecord;
import com.anurag.library.service.BorrowService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private BorrowService service;

    @PostMapping("/{bookId}")
    public BorrowRecord borrow(@PathVariable Long bookId) {
        return service.borrow(bookId);
    }

    @PostMapping("/{recordId}/return")
    public BorrowRecord returnBook(@PathVariable Long recordId) {
        return service.returnBook(recordId);
    }

    @GetMapping("/my")
    public List<BorrowRecord> myHistory() {
        return service.myHistory();
    }

    @GetMapping("/all")
    public List<BorrowRecord> allRecords() {
        return service.allRecords();
    }
}
