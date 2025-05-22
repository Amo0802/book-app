package com.example.zavrsniprojekat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

public class BookDetailActivity extends FragmentActivity {

    public static final String EXTRA_BOOK_ID = "book_id_key";
    private Book mBook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        UUID bookId = (UUID) getIntent().getSerializableExtra(EXTRA_BOOK_ID);
        mBook = BookLab.get(this).getBook(bookId);

        TextView titleText = findViewById(R.id.detail_title);
        TextView authorText = findViewById(R.id.detail_author);
        TextView notesText = findViewById(R.id.detail_notes);
        TextView statusText = findViewById(R.id.detail_status);
        TextView extraInfoText = findViewById(R.id.detail_extra);
        Button editButton = findViewById(R.id.button_edit);
        Button deleteButton = findViewById(R.id.button_delete);

        titleText.setText(mBook.getTitle());
        authorText.setText("Autor: " + mBook.getAuthor());
        notesText.setText("Bilješke: " + (mBook.getNotes() != null ? mBook.getNotes() : "-"));
        statusText.setText("Status: " + statusToString(mBook.getStatus()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        StringBuilder extra = new StringBuilder();

        switch (mBook.getStatus()) {
            case READ:
                if (mBook.getStartDate() != null)
                    extra.append("Datum početka: ").append(sdf.format(mBook.getStartDate())).append("\n\n");
                if (mBook.getEndDate() != null)
                    extra.append("Datum završetka: ").append(sdf.format(mBook.getEndDate())).append("\n\n");
                extra.append("Ocjena: ").append(mBook.getReview() != 0 ? mBook.getReview() : "N/A");
                break;

            case CURRENTLY_READING:
                if (mBook.getStartDate() != null)
                    extra.append("Datum početka: ").append(sdf.format(mBook.getStartDate())).append("\n\n");
                extra.append("Ukupan broj strana: ").append(mBook.getTotalPages()).append("\n\n");
                extra.append("Broj pročitanih strana: ").append(mBook.getPagesRead());
                break;

            case TO_READ:
                break;
        }

        extraInfoText.setText(extra.toString());

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailActivity.this, EditBookActivity.class);
            intent.putExtra(EditBookActivity.EXTRA_BOOK_ID, mBook.getId());
            startActivity(intent);
        });

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Brisanje knjige")
                    .setMessage("Da li ste sigurni da želite da obrišete ovu knjigu?")
                    .setPositiveButton("Da", (dialog, which) -> {
                        BookLab.get(this).getBooks().remove(mBook);
                        Toast.makeText(this, "Knjiga obrisana", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Ne", null)
                    .show();
        });
    }

    private String statusToString(Book.Status status) {
        switch (status) {
            case READ:
                return "Pročitana";
            case CURRENTLY_READING:
                return "Trenutno čitam";
            case TO_READ:
                return "Planiram da čitam";
            default:
                return "Nepoznato";
        }
    }
}
