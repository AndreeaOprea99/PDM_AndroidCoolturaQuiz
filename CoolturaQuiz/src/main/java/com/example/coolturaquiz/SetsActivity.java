package com.example.coolturaquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static com.example.coolturaquiz.SplashActivity.catList;
import static com.example.coolturaquiz.SplashActivity.selected_category_index;

public class SetsActivity extends AppCompatActivity {

    private GridView sets_grid;
    private FirebaseFirestore firestore;
    //public static int category_id;
    private Dialog loadingDialog;

    public static List<String> setsID = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        Toolbar toolbar = findViewById(R.id.set_toolbar);
        setSupportActionBar(toolbar);

        //String title = getIntent().getStringExtra("CATEGORY");
        //category_id = getIntent().getIntExtra("CATEGORY_ID",1);
        getSupportActionBar().setTitle(catList.get(selected_category_index).getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sets_grid = findViewById(R.id.sets_gridview);


        //Loading page
       /* loadingDialog = new Dialog(SetsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

*/

        //FireStore
        firestore = FirebaseFirestore.getInstance();

        loadSets();

       // SetsAdapter adapter = new SetsAdapter(6);
       //  sets_grid.setAdapter(adapter);



    }


    public void loadSets() {

        setsID.clear();

        firestore.collection("CoolturaQuiz").document(catList.get(selected_category_index).getId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                long nrSets = (long)documentSnapshot.get("SETS");

                for(int i=1; i <= nrSets; i++)
                {
                    setsID.add(documentSnapshot.getString("SET" + String.valueOf(i) + "_ID"));
                }


                SetsAdapter adapter = new SetsAdapter(setsID.size());
                sets_grid.setAdapter(adapter);



            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SetsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });





    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            SetsActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}