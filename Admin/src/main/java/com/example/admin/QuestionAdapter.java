package com.example.admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import java.util.List;
import java.util.Map;

import static com.example.admin.CategoryActivity.lista_cat;
import static com.example.admin.CategoryActivity.selected_cat_index;
import static com.example.admin.QuestionsActivity.lista_intrebari;
import static com.example.admin.SetsActivity.selected_set_index;
import static com.example.admin.SetsActivity.setsID;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<QuestionModel> ques_list;

    public QuestionAdapter(List<QuestionModel> ques_list) {
        this.ques_list = ques_list;
    }

    @NonNull
    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_item_layout, viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.ViewHolder viewHolder, int pos) {
        viewHolder.setData(pos, this);
    }

    @Override
    public int getItemCount() {
        return ques_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView deleteB;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textView);
            deleteB  = itemView.findViewById(R.id.delete);


        }

        private void setData(final int pos, final QuestionAdapter adapter)
        {
            title.setText("QUESTION " +  String.valueOf(pos+1));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(),QuestionDetailsActivity.class);
                    intent.putExtra("ACTION","EDIT");
                    intent.putExtra("Q_ID", pos);
                    itemView.getContext().startActivity(intent);
                }
            });

            deleteB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Sterge intrebarea")
                            .setMessage("Doriti sa stergeti intrebarea??")
                            .setPositiveButton("Sterge", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    deleteQuestion(pos, itemView.getContext(), adapter);
                                }
                            })
                            .setNegativeButton("Cancel",null)
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

        private void deleteQuestion(final int pos, final Context context, final QuestionAdapter adapter)
        {

            final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("CoolturaQuiz").document(lista_cat.get(selected_cat_index).getId())
                    .collection(setsID.get(selected_set_index)).document(lista_intrebari.get(pos).getQuesID())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Map<String,Object> quesDoc = new ArrayMap<>();
                            int index=1;
                            for(int i=0; i< lista_intrebari.size(); i++)
                            {
                                if(i != pos)
                                {
                                    quesDoc.put("Q" + String.valueOf(index) + "_ID", lista_intrebari.get(i).getQuesID());
                                    index++;
                                }
                            }

                            quesDoc.put("COUNT", String.valueOf(index - 1));

                            firestore.collection("Coolturaquiz").document(lista_cat.get(selected_cat_index).getId())
                                    .collection(setsID.get(selected_set_index)).document("QUESTIONS_LIST")
                                    .set(quesDoc)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context,"Intrebare eliminata cu succes",Toast.LENGTH_SHORT).show();

                                            lista_intrebari.remove(pos);
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



    }
}