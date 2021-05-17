package com.example.admin;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button addButton;
    public static List<ModelCat> lista_cat = new ArrayList<>();
    private FirebaseFirestore firestore;
    private Dialog addDialog;
    private EditText dialogNumeCat;
    private Button adaugare;
    private CategoryAdapter adapter;

    public static int selected_cat_index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categori");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler);
        addButton = findViewById(R.id.addCatButton);

       // List<String> lista_cat = new ArrayList<>();
        //lista_cat.add("Cat1");
        //lista_cat.add("Cat2");
        //lista_cat.add("Cat3");


        addDialog = new Dialog(CategoryActivity.this);
        addDialog.setContentView(R.layout.dialog_add_category);
        addDialog.setCancelable(true);
        addDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogNumeCat = addDialog.findViewById(R.id.nume);
        adaugare = addDialog.findViewById(R.id.adauga);

        firestore = FirebaseFirestore.getInstance();

        //adaugarea unei noi categori
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNumeCat.getText().clear();

                addDialog.show();
            }
        });
        adaugare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogNumeCat.getText().toString().isEmpty()){//daca numele nu e completat
                    dialogNumeCat.setError("Introduceti numele categoriei");
                    return;
                }
                addNewCategory(dialogNumeCat.getText().toString());
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        loadData();




    }
    private void loadData() {
        lista_cat.clear();

        firestore.collection("CoolturaQuiz").document("Categories")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();

                    if(doc.exists())
                    {
                        long count = (long)doc.get("COUNT");

                        for(int i=1;i<=count;i++)
                        {
                            String catName = doc.getString("CAT"+String.valueOf(i)+ "_NAME");
                            String categoryId = doc.getString("CAT"+String.valueOf(i)+ "_ID");
                            lista_cat.add(new ModelCat(categoryId,catName,"0","1"));//initial orice categorie noua are 0 seturi de intrebari
                        }

                         adapter = new CategoryAdapter(lista_cat);
                        recyclerView.setAdapter(adapter);
                    }
                    else{
                        finish();
                        Toast.makeText(CategoryActivity.this,"Nu exista Category Doc!",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(CategoryActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void addNewCategory(String title){
        addDialog.dismiss();

        final Map<String,Object> categoryData = new ArrayMap<>();
        categoryData.put("NAME",title);
        categoryData.put("SETS",0);
        categoryData.put("COUNTER","1");

        String documentId = firestore.collection("CoolturaQuiz").document().getId();

        firestore.collection("CoolturaQuiz").document(documentId)
                .set(categoryData)
                .addOnSuccessListener(new OnSuccessListener<Void>(){

                    @Override
                    public void onSuccess(Void aVoid) {

                        Map<String,Object> catDoc = new ArrayMap<>();
                        catDoc.put("CAT"+String.valueOf(lista_cat.size()+1)+"_NAME",title);
                        catDoc.put("CAT"+String.valueOf(lista_cat.size()+1)+"_ID",documentId);
                        catDoc.put("COUNT",lista_cat.size()+1);
                        firestore.collection("CoolturaQuiz").document("Categories")
                                .update(catDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>(){

                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(CategoryActivity.this, "Categorie adaugata cu succes!",Toast.LENGTH_SHORT).show();

                                        lista_cat.add(new ModelCat(documentId,title,"0","1"));

                                        adapter.notifyItemInserted(lista_cat.size());


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener(){

                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CategoryActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener(){

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CategoryActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }



}