package com.example.coolturaquiz;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class exDialog extends AppCompatDialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Informatii")
                .setMessage("Aplicatia CoolturaQuiz va indeamna sa va imbogatiti cultura generala. Modul de utilizare al acesteia este simplu:apasati butonul de START, apoi alegeti Categoria si Setul de intrebari dorit. Intrebarile vor fi afisate pe rand si veti avea 10 secunde la dispozitie pentru fiecare")
                .setPositiveButton("ok",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int i){

                    }
                });
        return builder.create();
    }

}
