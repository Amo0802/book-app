package com.example.zavrsniprojekat;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddBookActivity extends AppCompatActivity {

    private EditText inputTitle, inputAuthor, inputNotes;
    private Spinner spinnerStatus;
    private LinearLayout extraFieldsContainer;
    private Button buttonAdd;
    private final Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Pročitana", "Trenutno čitam", "Planiram da čitam"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateExtraFields(position);
                setupDatePickers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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
                case 0:
                    book.setStatus(Book.Status.READ);
                    EditText startRead = extraFieldsContainer.findViewById(R.id.input_start_date);
                    EditText endRead = extraFieldsContainer.findViewById(R.id.input_end_date);
                    EditText review = extraFieldsContainer.findViewById(R.id.input_review);

                    book.setStartDate(parseDate(startRead.getText().toString()));
                    book.setEndDate(parseDate(endRead.getText().toString()));

                    if(review.getText().toString().isEmpty()){
                        book.setReview(0);
                    }
                    else{
                        double rating = Double.parseDouble(review.getText().toString());

                        if (rating < 1.0 || rating > 5.0) {
                            Toast.makeText(this, "Ocjena mora biti između 1.0 i 5.0", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        book.setReview(rating);
                    }
                    break;

                case 1:
                    book.setStatus(Book.Status.CURRENTLY_READING);
                    EditText startCurrent = extraFieldsContainer.findViewById(R.id.input_start_date);
                    EditText totalPages = extraFieldsContainer.findViewById(R.id.input_total_pages);
                    EditText pagesRead = extraFieldsContainer.findViewById(R.id.input_pages_read);

                    book.setStartDate(parseDate(startCurrent.getText().toString()));

                    if(totalPages.getText().toString().isEmpty() || pagesRead.getText().toString().isEmpty()){
                        Toast.makeText(this, "Unesite broj strana", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int totalPagesInt = Integer.parseInt(totalPages.getText().toString());
                    int pagesReadInt = Integer.parseInt(pagesRead.getText().toString());
                    if(totalPagesInt <= pagesReadInt){
                        Toast.makeText(this, "Ukupan broj strana mora biti veći od broja pročitanih", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    book.setTotalPages(totalPagesInt);
                    book.setPagesRead(pagesReadInt);
                    break;

                case 2:
                    book.setStatus(Book.Status.TO_READ);
                    break;
            }

            BookLab.get(this).addBook(book);
            Toast.makeText(this, "Knjiga dodana!", Toast.LENGTH_SHORT).show();
            finish();
        });

    }

    private void updateExtraFields(int statusPosition) {
        extraFieldsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        switch (statusPosition) {
            case 0:
                inflater.inflate(R.layout.fields_read, extraFieldsContainer, true);
                break;
            case 1:
                inflater.inflate(R.layout.fields_currently_reading, extraFieldsContainer, true);
                break;
            case 2:
                break;
        }
    }

    private void setupDatePickers() {
        int statusPosition = spinnerStatus.getSelectedItemPosition();

        if (statusPosition == 0) {
            EditText startDateField = extraFieldsContainer.findViewById(R.id.input_start_date);
            EditText endDateField = extraFieldsContainer.findViewById(R.id.input_end_date);

            setupDatePickerForField(startDateField, "Datum početka");
            setupDatePickerForField(endDateField, "Datum završetka");

        } else if (statusPosition == 1) {
            EditText startDateField = extraFieldsContainer.findViewById(R.id.input_start_date);
            setupDatePickerForField(startDateField, "Datum početka");
        }
    }

    private void setupDatePickerForField(EditText dateField, String hint) {
        dateField.setHint(hint);
        dateField.setFocusable(false);
        dateField.setClickable(true);

        dateField.setText(dateFormat.format(calendar.getTime()));

        dateField.setOnClickListener(v -> showDatePicker(dateField));
    }

    private void showDatePicker(final EditText dateField) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateField.setText(dateFormat.format(calendar.getTime()));
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private Date parseDate(String dateStr) {
        try {
            return dateFormat.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}