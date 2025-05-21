package com.example.zavrsniprojekat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddBookActivity extends AppCompatActivity {

    private EditText inputTitle, inputAuthor, inputNotes;
    private Spinner spinnerStatus;
    private LinearLayout extraFieldsContainer;
    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        inputTitle = findViewById(R.id.input_title);
        inputAuthor = findViewById(R.id.input_author);
        inputNotes = findViewById(R.id.input_notes);
        spinnerStatus = findViewById(R.id.spinner_status);
        extraFieldsContainer = findViewById(R.id.extra_fields_container);
        buttonAdd = findViewById(R.id.button_add);

        // Postavi spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Pročitana", "Trenutno čitam", "Planiram da čitam"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Promjena tipa knjige → mijenja polja
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateExtraFields(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Dugme za dodavanje – za sada samo test Toast
        buttonAdd.setOnClickListener(v -> {
            String title = inputTitle.getText().toString().trim();
            String author = inputAuthor.getText().toString().trim();
            String notes = inputNotes.getText().toString().trim();
            int statusIndex = spinnerStatus.getSelectedItemPosition();

            if (title.isEmpty() || author.isEmpty()) {
                Toast.makeText(this, "Unesite naslov i autora", Toast.LENGTH_SHORT).show();
                return;
            }

            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setNotes(notes);

            switch (statusIndex) {
                case 0: // Pročitana
                    book.setStatus(Book.Status.READ);
                    EditText startRead = extraFieldsContainer.findViewById(R.id.input_start_date);
                    EditText endRead = extraFieldsContainer.findViewById(R.id.input_end_date);
                    EditText review = extraFieldsContainer.findViewById(R.id.input_review);

                    book.setStartDate(parseDate(startRead.getText().toString()));
                    book.setEndDate(parseDate(endRead.getText().toString()));

                    float rating = 0;
                    try {
                        rating = Float.parseFloat(review.getText().toString());
                        if (rating < 1.0f || rating > 5.0f) {
                            Toast.makeText(this, "Ocjena mora biti između 1.0 i 5.0", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        book.setReview(String.valueOf(rating));
                    } catch (Exception e) {
                        Toast.makeText(this, "Neispravna ocjena", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;

                case 1: // Trenutno čitam
                    book.setStatus(Book.Status.CURRENTLY_READING);
                    EditText startCurrent = extraFieldsContainer.findViewById(R.id.input_start_date);
                    EditText totalPages = extraFieldsContainer.findViewById(R.id.input_total_pages);
                    EditText pagesRead = extraFieldsContainer.findViewById(R.id.input_pages_read);

                    book.setStartDate(parseDate(startCurrent.getText().toString()));

                    try {
                        book.setTotalPages(Integer.parseInt(totalPages.getText().toString()));
                        book.setPagesRead(Integer.parseInt(pagesRead.getText().toString()));
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Unesite važeće brojeve strana", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;

                case 2: // Planiram da čitam
                    book.setStatus(Book.Status.TO_READ);
                    break;
            }

            BookLab.get(this).addBook(book);
            Toast.makeText(this, "Knjiga dodana!", Toast.LENGTH_SHORT).show();
            finish(); // Vrati se u MainActivity
        });

    }

    private void updateExtraFields(int statusPosition) {
        extraFieldsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        switch (statusPosition) {
            case 0: // Pročitana
                inflater.inflate(R.layout.fields_read, extraFieldsContainer, true);
                break;
            case 1: // Trenutno čitam
                inflater.inflate(R.layout.fields_currently_reading, extraFieldsContainer, true);
                break;
            case 2: // Planiram da čitam
                // nema dodatnih polja
                break;
        }
    }

    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

}
