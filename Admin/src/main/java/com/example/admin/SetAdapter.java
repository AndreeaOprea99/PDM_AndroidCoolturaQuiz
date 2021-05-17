package com.example.admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import static com.example.admin.CategoryActivity.lista_cat;
import static com.example.admin.CategoryActivity.selected_cat_index;
import static com.example.admin.SetsActivity.selected_set_index;

import java.util.List;
import java.util.Map;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.ViewHolder>{


    private List<String> setIDs;

    public SetAdapter(List<String> setIDs) {
        this.setIDs = setIDs;
    }


    @NonNull
    @Override
    public SetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_item_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetAdapter.ViewHolder viewHolder, int i) {

        String setID = setIDs.get(i);
        viewHolder.setData(i, setID, this);
    }

    @Override
    public int getItemCount() {
        return setIDs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView setName;
        private ImageView deleteSetB;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            setName = itemView.findViewById(R.id.textView);
            deleteSetB = itemView.findViewById(R.id.delete);


        }

        private void setData(final int pos, final String setID, final SetAdapter adapter)
        {
            setName.setText("SET " + String.valueOf(pos + 1));


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selected_set_index = pos;

                    Intent intent = new Intent(itemView.getContext(),QuestionsActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });

            deleteSetB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete Set")
                            .setMessage("Sunteti sigur ca vreti sa stergeti acest set?")
                            .setPositiveButton("Sterge", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteSet(pos, setID,itemView.getContext(), adapter);
                                }
                            })
                            .setNegativeButton("Anuleaza",null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    dialog.getButton(dialog.BUTTON_POSITIVE).setBackgroundColor(Color.LTGRAY);
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(Color.LTGRAY);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,0,50,0);
                    dialog.getButton(dialog.BUTTON_NEGATIVE).setLayoutParams(params);


                }
            });
        }


        private void deleteSet(final int pos, String setID, final Context context, final SetAdapter adapter)
        {


            final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("CoolturaQuiz").document(lista_cat.get(selected_cat_index).getId())
                    .collection(setID).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            WriteBatch batch = firestore.batch();

                            for(QueryDocumentSnapshot doc : queryDocumentSnapshots)
                            {
                                batch.delete(doc.getReference());
                            }

                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Map<String, Object> catDoc = new ArrayMap<>();
                                    int index=1;
                                    for(int i=0; i< setIDs.size();  i++)
                                    {
                                        if(i != pos)
                                        {
                                            catDoc.put("SET" + String.valueOf(index) + "_ID", setIDs.get(i));
                                            index++;
                                        }
                                    }

                                    catDoc.put("SETS", index-1);

                                    firestore.collection("CoolturaQuiz").document(lista_cat.get(selected_cat_index).getId())
                                            .update(catDoc)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(context,"Set eliminat cu succes",Toast.LENGTH_SHORT).show();

                                                    SetsActivity.setsID.remove(pos);

                                                    lista_cat.get(selected_cat_index).setNrSets(String.valueOf(SetsActivity.setsID.size()));

                                                    adapter.notifyDataSetChanged();



                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();

                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });

        }

    }
}