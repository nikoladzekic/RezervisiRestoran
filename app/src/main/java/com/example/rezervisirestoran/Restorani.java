package com.example.rezervisirestoran;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Restorani extends AppCompatActivity implements  View.OnClickListener, AdapterView.OnItemSelectedListener{

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private TextView mDisplayTime;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private String stoKodText;
    private Boolean rez = false;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rezervacija);
        mDisplayDate = (TextView) findViewById(R.id.datumDialogPicker);
        mDisplayTime = (TextView) findViewById(R.id.timeDialogPicker);
        TextView imeRestorana = findViewById(R.id.imeRestorana);
        TextView radnoVreme= findViewById(R.id.radnoVreme);
        TextView lokacija = findViewById(R.id.lokacija);
        TextView brStolova = findViewById(R.id.brStolova);
        TextView brStolica = findViewById(R.id.brStolica);
        ImageView slika = findViewById(R.id.imageView);
        List<String> stolovi =  new ArrayList<String>();
        Button btnRezervisi = findViewById(R.id.buttonRezervisi);
        builder = new AlertDialog.Builder(this);

        btnRezervisi.setOnClickListener(this);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, stolovi);

        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.get("https://restoranirez.herokuapp.com/restorani/{id}")
                            .addPathParameter("id", id)
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        imeRestorana.setText(response.getString("name"));
                                        radnoVreme.setText(response.getString("workHours"));
                                        lokacija.setText(response.getString("location"));
                                        brStolova.setText(response.getString("numTables"));
                                        brStolica.setText(response.getString("numTotalSeats"));
                                        Picasso.get().load(response.getString("image")).into(slika);
                                        getSupportActionBar().setTitle(imeRestorana.getText());
                                        JSONArray stoloviJson = response.getJSONArray("tables");
                                        for(int i=0;i<stoloviJson.length();i++){
                                            JSONObject sto = stoloviJson.getJSONObject(i);
                                            if(sto.getString("reserved")=="false")
                                                stolovi.add(sto.getString("name"));
                                        }

                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    adapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.d("Greska", String.valueOf(anError));
                                }


                            });
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner sItems = (Spinner) findViewById(R.id.spinnerStolovi);
            sItems.setOnItemSelectedListener(this);
            sItems.setAdapter(adapter);


        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int godina = cal.get(Calendar.YEAR);
                int mesec = cal.get(Calendar.MONTH);
                int dan = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(Restorani.this, android.R.style.Theme_Holo_Dialog_MinWidth, onDateSetListener, godina,mesec,dan);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int godina, int mesec, int dan) {
                mesec = mesec+1; //jer mesec ide od 0 po default-u
                String datum = dan+"/"+mesec+"/"+godina;
                mDisplayDate.setText(datum);
            }
        };

        mDisplayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                final int sat = cal.get(Calendar.HOUR_OF_DAY);
                final int minut = cal.get(Calendar.MINUTE);;
                TimePickerDialog dialog = new TimePickerDialog(Restorani.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mDisplayTime.setText(hourOfDay+":"+minute);
                    }
                }, sat, minut, android.text.format.DateFormat.is24HourFormat(Restorani.this));
                dialog.show();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        stoKodText = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onClick(View v) {
        EditText ime = findViewById(R.id.imeInput);
        TextView datum = findViewById(R.id.datumDialogPicker);
        TextView vreme = findViewById(R.id.timeDialogPicker);
        builder.setMessage("Rezervacija").setTitle("Rezervacija uspesna");
        builder.setMessage("Uspesno ste poslali rezervaciju.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(v.getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Rezervacija");
            alert.show();

        if(v.getId()==R.id.buttonRezervisi){
            Log.d("Podaci", (ime.getText().toString()+","+stoKodText+","+vreme.getText().toString()+","+datum.getText().toString()));
            AndroidNetworking.post("https://restoranirez.herokuapp.com/rezervacije/")
                    .addBodyParameter("name", ime.getText().toString())
                    .addBodyParameter("tableCode", stoKodText)
                    .addBodyParameter("time", vreme.getText().toString())
                    .addBodyParameter("date", datum.getText().toString())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Poruka", String.valueOf(response));
                            rez = true;
                            openDialog(rez);
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("Greska", anError.getErrorBody());
                        }


                    });
        }

    }

    void openDialog(Boolean response){

    }
}
