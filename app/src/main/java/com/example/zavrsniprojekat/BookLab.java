package com.example.zavrsniprojekat;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class BookLab {

    private static BookLab sBookLab;
    private ArrayList<Book> mBooks;

    private BookLab(Context context) {
        mBooks = new ArrayList<>();

        // Primjeri knjiga
        Book b1 = new Book();
        b1.setTitle("1984");
        b1.setAuthor("George Orwell");
        b1.setStatus(Book.Status.READ);
        b1.setStartDate(new Date(123, 0, 1)); // 1.1.2023
        b1.setEndDate(new Date(123, 0, 15));
        b1.setReview("Vrlo zanimljivo i mračno.");
        b1.setNotes("Pročitati ponovo kasnije.");
        mBooks.add(b1);

        Book b2 = new Book();
        b2.setTitle("Atomic Habits");
        b2.setAuthor("James Clear");
        b2.setStatus(Book.Status.CURRENTLY_READING);
        b2.setStartDate(new Date());
        b2.setTotalPages(250);
        b2.setPagesRead(90);
        b2.setNotes("Vrlo korisna knjiga o navikama.");
        mBooks.add(b2);

        Book b3 = new Book();
        b3.setTitle("Sapiens");
        b3.setAuthor("Yuval Noah Harari");
        b3.setStatus(Book.Status.TO_READ);
        b3.setNotes("Dobili preporuku da se pročita.");
        mBooks.add(b3);
    }

    public static BookLab get(Context context) {
        if (sBookLab == null) {
            sBookLab = new BookLab(context);
        }
        return sBookLab;
    }

    public ArrayList<Book> getBooks() {
        return mBooks;
    }

    public Book getBook(UUID id) {
        for (Book book : mBooks) {
            if (book.getId().equals(id)) {
                return book;
            }
        }
        return null;
    }

    public void addBook(Book book) {
        mBooks.add(book);
    }

    public void deleteBook(Book book) {
        mBooks.remove(book);
    }
}
