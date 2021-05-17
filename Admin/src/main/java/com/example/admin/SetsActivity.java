package com.example.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.admin.CategoryActivity.lista_cat;
import static com.example.admin.CategoryActivity.selected_cat_index;

public class SetsActivity extends AppCompatActivity {

    private RecyclerView setsView;
    private Button addSet;

    private SetAdapter adapter;

    private FirebaseFirestore firestore;


    public static List<String> setsID = new ArrayList<>();
    public static int selected_set_index=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        Toolbar toolbar = findViewById(R.id.toolbar_sets);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Seturi de intrebari");

        setsView = findViewById(R.id.recycler_set);
        addSet = findViewById(R.id.addSetButton);

        addSet.setText("Adauga un set nou");

        addSet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    addNewSet();
            }
        });
        firestore = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setsView.setLayoutManager(layoutManager);

        loadSets();

    }
    private void loadSets() {

        setsID.clear();
        firestore.collection("CoolturaQuiz").document(lista_cat.get(selected_cat_index).getId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                long nrSets = (long)documentSnapshot.get("SETS");

                for(int i=1; i <= nrSets; i++)
                {
                    setsID.add(documentSnapshot.getString("SET" + String.valueOf(i) + "_ID"));
                }

                lista_cat.get(selected_cat_index).setSetCounter(documentSnapshot.getString("COUNTER"));
                lista_cat.get(selected_cat_index).setNrSets(String.valueOf(nrSets));

                adapter = new SetAdapter(setsID);
                setsView.setAdapter(adapter);



            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SetsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });



        //adapter = new SetAdapter(setsID);
        //setsView.setAdapter(adapter);

    }

    private void addNewSet()
    {

        final String current_cat_id = lista_cat.get(selected_cat_index).getId();
        final String current_counter = lista_cat.get(selected_cat_index).getSetCounter();

        Map<String,Object> qData = new ArrayMap<>();
        qData.put("COUNT","0");

        firestore.collection("CoolturaQuiz").document(current_cat_id)
                .collection(current_counter).document("QUESTIONS_LIST")
                .set(qData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Map<String,Object> catDoc = new ArrayMap<>();
                        catDoc.put("COUNTER", String.valueOf(Integer.valueOf(current_counter) + 1)  );
                        catDoc.put("SET" + String.valueOf(setsID.size() + 1) + "_ID", current_counter);
                        catDoc.put("SETS", setsID.size() + 1);

                        firestore.collection("CoolturaQuiz").document(current_cat_id)
                                .update(catDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(SetsActivity.this, " Set Added Successfully",Toast.LENGTH_SHORT).show();

                                        setsID.add(current_counter);
                                        lista_cat.get(selected_cat_index).setNrSets(String.valueOf(setsID.size()));
                                        lista_cat.get(selected_cat_index).setSetCounter(String.valueOf(Integer.valueOf(current_counter) + 1));

                                        adapter.notifyItemInserted(setsID.size());


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SetsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SetsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

    }


}