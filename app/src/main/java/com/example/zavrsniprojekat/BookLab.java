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

        Book b1 = new Book();
        b1.setTitle("1984");
        b1.setAuthor("George Orwell");
        b1.setStatus(Book.Status.READ);
        b1.setStartDate(new Date(123, 0, 1));
        b1.setEndDate(new Date(123, 0, 15));
        b1.setReview(3.2);
        b1.setNotes("Pročitati ponovo kasnije.");
        mBooks.add(b1);

        Book b2 = new Book();
        b2.setTitle("To Kill a Mockingbird");
        b2.setAuthor("Harper Lee");
        b2.setStatus(Book.Status.READ);
        b2.setStartDate(new Date(122, 2, 10));
        b2.setEndDate(new Date(122, 2, 20));
        b2.setReview(4.5);
        b2.setNotes("Emotivna i poučna priča.");
        mBooks.add(b2);

        Book b3 = new Book();
        b3.setTitle("The Great Gatsby");
        b3.setAuthor("F. Scott Fitzgerald");
        b3.setStatus(Book.Status.READ);
        b3.setStartDate(new Date(121, 5, 1));
        b3.setEndDate(new Date(121, 5, 10));
        b3.setReview(4.0);
        b3.setNotes("Zanimljiv prikaz američkog sna.");
        mBooks.add(b3);

        Book b4 = new Book();
        b4.setTitle("Atomic Habits");
        b4.setAuthor("James Clear");
        b4.setStatus(Book.Status.CURRENTLY_READING);
        b4.setStartDate(new Date());
        b4.setTotalPages(250);
        b4.setPagesRead(90);
        b4.setNotes("Vrlo korisna knjiga o navikama.");
        mBooks.add(b4);

        Book b5 = new Book();
        b5.setTitle("Deep Work");
        b5.setAuthor("Cal Newport");
        b5.setStatus(Book.Status.CURRENTLY_READING);
        b5.setStartDate(new Date());
        b5.setTotalPages(300);
        b5.setPagesRead(150);
        b5.setNotes("Odlična za fokusiranje na važne zadatke.");
        mBooks.add(b5);

        Book b6 = new Book();
        b6.setTitle("Clean Code");
        b6.setAuthor("Robert C. Martin");
        b6.setStatus(Book.Status.CURRENTLY_READING);
        b6.setStartDate(new Date());
        b6.setTotalPages(450);
        b6.setPagesRead(210);
        b6.setNotes("Obavezna lektira za programere.");
        mBooks.add(b6);

        Book b7 = new Book();
        b7.setTitle("Sapiens");
        b7.setAuthor("Yuval Noah Harari");
        b7.setStatus(Book.Status.TO_READ);
        b7.setNotes("Dobili preporuku da se pročita.");
        mBooks.add(b7);

        Book b8 = new Book();
        b8.setTitle("Thinking, Fast and Slow");
        b8.setAuthor("Daniel Kahneman");
        b8.setStatus(Book.Status.TO_READ);
        b8.setNotes("Zanimljiva psihološka knjiga.");
        mBooks.add(b8);

        Book b9 = new Book();
        b9.setTitle("The Pragmatic Programmer");
        b9.setAuthor("Andrew Hunt i David Thomas");
        b9.setStatus(Book.Status.TO_READ);
        b9.setNotes("Klasik za softverske inženjere.");
        mBooks.add(b9);
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
