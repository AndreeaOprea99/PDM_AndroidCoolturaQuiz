package com.example.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText email, password;
    private Button login;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        firebaseAuth =firebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //daca emailul nu este introdus
                if (email.getText().toString().isEmpty()){
                    email.setError("Introdu Email");
                    return;
                }
                else{
                    email.setError(null);
                }
                //daca parola nu e introdusa
                if (password.getText().toString().isEmpty()){
                    password.setError("Introdu parola");
                    return;
                }
                else{
                    password.setError(null);
                }
                firebaseLogin();
            }
        });

        //daca userul e deja logat
        if(firebaseAuth.getCurrentUser() !=null){
            //redirectionare catre categoryActivity
            Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
            startActivity(intent);
            finish();
        }

    }

    //logare la baza de date
    private void firebaseLogin(){
        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "Succes",Toast.LENGTH_SHORT).show();
                            //redirectionare catre categoryActivity
                            Intent intent = new Intent(MainActivity.this,CategoryActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Incorect",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}