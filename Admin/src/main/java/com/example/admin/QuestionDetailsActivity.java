package com.example.admin;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import static com.example.admin.CategoryActivity.lista_cat;
import static com.example.admin.CategoryActivity.selected_cat_index;
import static com.example.admin.QuestionsActivity.lista_intrebari;
import static com.example.admin.SetsActivity.selected_set_index;
import static com.example.admin.SetsActivity.setsID;

public class QuestionDetailsActivity extends AppCompatActivity {

    private EditText ques, optionA, optionB, optionC, optionD, answer;
    private Button addQB;
    private String qStr, aStr, bStr, cStr, dStr, ansStr;
    private FirebaseFirestore firestore;
    private String action;
    private int qID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);

        Toolbar toolbar = findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ques = findViewById(R.id.question);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        answer = findViewById(R.id.answer);
        addQB = findViewById(R.id.addQB);


        firestore = FirebaseFirestore.getInstance();

        action = getIntent().getStringExtra("ACTION");

        if(action.compareTo("EDIT") == 0)
        {
            qID = getIntent().getIntExtra("Q_ID",0);
            loadData(qID);
            getSupportActionBar().setTitle("Question " + String.valueOf(qID + 1));
            addQB.setText("UPDATE");
        }
        else
        {
            getSupportActionBar().setTitle("Question " + String.valueOf(lista_intrebari.size() + 1));
            addQB.setText("ADD");
        }

        addQB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                qStr = ques.getText().toString();
                aStr = optionA.getText().toString();
                bStr = optionB.getText().toString();
                cStr = optionC.getText().toString();
                dStr = optionD.getText().toString();
                ansStr = answer.getText().toString();

                if(qStr.isEmpty()) {
                    ques.setError("Introduceti intrebarea");
                    return;
                }

                if(aStr.isEmpty()) {
                    optionA.setError("Introduceti optiunea A");
                    return;
                }

                if(bStr.isEmpty()) {
                    optionB.setError("Introduceti optiunea B ");
                    return;
                }
                if(cStr.isEmpty()) {
                    optionC.setError("Introduceti optiunea C");
                    return;
                }
                if(dStr.isEmpty()) {
                    optionD.setError("Introduceti optiunea D");
                    return;
                }
                if(ansStr.isEmpty()) {
                    answer.setError("Introduceti numarul raspunsului corect");
                    return;
                }


                    addNewQuestion();


            }
        });
    }


    private void addNewQuestion()
    {


        Map<String,Object> quesData = new ArrayMap<>();

        quesData.put("QUESTION",qStr);
        quesData.put("A",aStr);
        quesData.put("B",bStr);
        quesData.put("C",cStr);
        quesData.put("D",dStr);
        quesData.put("ANSWER",ansStr);


        final String doc_id = firestore.collection("CoolturaQuiz").document(lista_cat.get(selected_cat_index).getId())
                .collection(setsID.get(selected_set_index)).document().getId();

        firestore.collection("CoolturaQuiz").document(lista_cat.get(selected_cat_index).getId())
                .collection(setsID.get(selected_set_index)).document(doc_id)
                .set(quesData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Map<String,Object> quesDoc = new ArrayMap<>();
                        quesDoc.put("Q" + String.valueOf(lista_intrebari.size() + 1) + "_ID", doc_id);
                        quesDoc.put("COUNT",String.valueOf(lista_intrebari.size() + 1));

                        firestore.collection("CoolturaQuiz").document(lista_cat.get(selected_cat_index).getId())
                                .collection(setsID.get(selected_set_index)).document("QUESTIONS_LIST")
                                .update(quesDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(QuestionDetailsActivity.this, " Intrebare adaugata cu succes", Toast.LENGTH_SHORT).show();

                                        lista_intrebari.add(new QuestionModel(
                                                doc_id,
                                                qStr,aStr,bStr,cStr,dStr, Integer.valueOf(ansStr)
                                        ));


                                        QuestionDetailsActivity.this.finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(QuestionDetailsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuestionDetailsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void loadData(int id)
    {
        ques.setText(lista_intrebari.get(id).getQuestion());
        optionA.setText(lista_intrebari.get(id).getOptionA());
        optionB.setText(lista_intrebari.get(id).getOptionB());
        optionC.setText(lista_intrebari.get(id).getOptionC());
        optionD.setText(lista_intrebari.get(id).getOptionD());
        answer.setText(String.valueOf(lista_intrebari.get(id).getCorrectAnswer()));
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}