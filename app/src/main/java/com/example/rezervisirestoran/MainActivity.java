package com.example.rezervisirestoran;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    String nazivPoIndeksu = "";
    String idRestorana = "";
    List<String> restoraniID =  new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonPotvrdi = findViewById(R.id.buttonPotvrdi);
        List<String> restoranNazivi =  new ArrayList<String>();

        buttonPotvrdi.setOnClickListener(this);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, restoranNazivi);
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.get("https://restoranirez.herokuapp.com/restorani")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i=0;i<response.length();i++){
                            try {
                                JSONObject objekat = response.getJSONObject(i);
                                String ime = objekat.getString("name");
                                String id = objekat.getString("_id");
                                restoranNazivi.add(ime);
                                restoraniID.add(id);
                                adapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinnerView);
        sItems.setOnItemSelectedListener(this);
        sItems.setAdapter(adapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        nazivPoIndeksu = parent.getItemAtPosition(position).toString();
        idRestorana = restoraniID.get(position);
        Toast.makeText(parent.getContext(), nazivPoIndeksu, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v){
        if(v.getId()==R.id.buttonPotvrdi){
            Intent intent = new Intent(this, Restorani.class);
            intent.putExtra("id", idRestorana);
            startActivity(intent);
        }
    }
}