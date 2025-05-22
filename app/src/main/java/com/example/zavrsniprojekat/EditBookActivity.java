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
import java.util.UUID;

public class EditBookActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK_ID = "edit_book_id";

    private EditText inputTitle, inputAuthor, inputNotes;
    private Spinner spinnerStatus;
    private LinearLayout extraFieldsContainer;
    private Button buttonAdd;
    private Book mBook;
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
        buttonAdd.setText("Sačuvaj izmjene");

        UUID bookId = (UUID) getIntent().getSerializableExtra(EXTRA_BOOK_ID);
        mBook = BookLab.get(this).getBook(bookId);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Pročitana", "Trenutno čitam", "Planiram da čitam"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        int statusPosition = 0;
        switch (mBook.getStatus()) {
            case READ:
                statusPosition = 0;
                break;
            case CURRENTLY_READING:
                statusPosition = 1;
                break;
            case TO_READ:
                statusPosition = 2;
                break;
        }
        spinnerStatus.setSelection(statusPosition);

        inputTitle.setText(mBook.getTitle());
        inputAuthor.setText(mBook.getAuthor());
        if (mBook.getNotes() != null) {
            inputNotes.setText(mBook.getNotes());
        }

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateExtraFields(position);
                setupDatePickers();
                fillExtraFields(position);
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

            mBook.setTitle(title);
            mBook.setAuthor(author);
            mBook.setNotes(notes);

            switch (statusIndex) {
                case 0:
                    mBook.setStatus(Book.Status.READ);
                    EditText startRead = extraFieldsContainer.findViewById(R.id.input_start_date);
                    EditText endRead = extraFieldsContainer.findViewById(R.id.input_end_date);
                    EditText review = extraFieldsContainer.findViewById(R.id.input_review);

                    mBook.setStartDate(parseDate(startRead.getText().toString()));
                    mBook.setEndDate(parseDate(endRead.getText().toString()));

                    if(review.getText().toString().isEmpty()){
                        mBook.setReview(0);
                    }
                    else{
                        double rating = Double.parseDouble(review.getText().toString());

                        if (rating < 1.0 || rating > 5.0) {
                            Toast.makeText(this, "Ocjena mora biti između 1.0 i 5.0", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mBook.setReview(rating);
                    }
                    break;

                case 1:
                    mBook.setStatus(Book.Status.CURRENTLY_READING);
                    EditText startCurrent = extraFieldsContainer.findViewById(R.id.input_start_date);
                    EditText totalPages = extraFieldsContainer.findViewById(R.id.input_total_pages);
                    EditText pagesRead = extraFieldsContainer.findViewById(R.id.input_pages_read);

                    mBook.setStartDate(parseDate(startCurrent.getText().toString()));

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

                    mBook.setTotalPages(totalPagesInt);
                    mBook.setPagesRead(pagesReadInt);
                    break;

                case 2:
                    mBook.setStatus(Book.Status.TO_READ);
                    break;
            }

            Toast.makeText(this, "Knjiga ažurirana!", Toast.LENGTH_SHORT).show();
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
        dateField.setOnClickListener(v -> showDatePicker(dateField));
    }

    private void showDatePicker(final EditText dateField) {
        try {
            String currentDate = dateField.getText().toString();
            if (!currentDate.isEmpty()) {
                Date date = parseDate(currentDate);
                calendar.setTime(date);
            }
        } catch (Exception e) {
            calendar.setTime(new Date());
        }

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

    private void fillExtraFields(int statusPosition) {
        if (statusPosition == 0 && mBook.getStatus() == Book.Status.READ) {
            EditText startDateField = extraFieldsContainer.findViewById(R.id.input_start_date);
            EditText endDateField = extraFieldsContainer.findViewById(R.id.input_end_date);
            EditText reviewField = extraFieldsContainer.findViewById(R.id.input_review);

            if (mBook.getStartDate() != null) {
                startDateField.setText(dateFormat.format(mBook.getStartDate()));
            }

            if (mBook.getEndDate() != null) {
                endDateField.setText(dateFormat.format(mBook.getEndDate()));
            }

            if (mBook.getReview() != 0) {
                reviewField.setText(String.valueOf(mBook.getReview()));
            }
        } else if (statusPosition == 1 && mBook.getStatus() == Book.Status.CURRENTLY_READING) {
            EditText startDateField = extraFieldsContainer.findViewById(R.id.input_start_date);
            EditText totalPagesField = extraFieldsContainer.findViewById(R.id.input_total_pages);
            EditText pagesReadField = extraFieldsContainer.findViewById(R.id.input_pages_read);

            if (mBook.getStartDate() != null) {
                startDateField.setText(dateFormat.format(mBook.getStartDate()));
            }

            totalPagesField.setText(String.valueOf(mBook.getTotalPages()));
            pagesReadField.setText(String.valueOf(mBook.getPagesRead()));
        }
    }

    private Date parseDate(String dateStr) {
        try {
            return dateFormat.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}