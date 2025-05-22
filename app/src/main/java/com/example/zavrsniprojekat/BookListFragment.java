package com.example.zavrsniprojekat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;

public class BookListFragment extends ListFragment {

    private static final String ARG_STATUS = "book_status";
    private Book.Status mStatus;
    private ArrayList<Book> mFilteredBooks;

    public static BookListFragment newInstance(Book.Status status) {
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status.name());

        BookListFragment fragment = new BookListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStatus = Book.Status.valueOf(getArguments().getString(ARG_STATUS));
        ArrayList<Book> allBooks = BookLab.get(getActivity()).getBooks();

        mFilteredBooks = new ArrayList<>();
        for (Book book : allBooks) {
            if (book.getStatus() == mStatus) {
                mFilteredBooks.add(book);
            }
        }

        BookAdapter adapter = new BookAdapter(getActivity(), mFilteredBooks);
        setListAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener((parent, v, position, id) -> {
            Book clickedBook = mFilteredBooks.get(position);
            Intent intent = new Intent(getActivity(), BookDetailActivity.class);
            intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, clickedBook.getId());
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<Book> allBooks = BookLab.get(getActivity()).getBooks();

        mFilteredBooks.clear();
        for (Book book : allBooks) {
            if (book.getStatus() == mStatus) {
                mFilteredBooks.add(book);
            }
        }

        ((ArrayAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private class BookAdapter extends ArrayAdapter<Book> {
        public BookAdapter(Context context, ArrayList<Book> books) {
            super(context, 0, books);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item_book, parent, false);
            }

            Book book = getItem(position);

            TextView titleTextView = convertView.findViewById(R.id.book_title);
            TextView authorTextView = convertView.findViewById(R.id.book_author);

            titleTextView.setText(book.getTitle());
            authorTextView.setText(book.getAuthor());

            return convertView;
        }
    }
}
