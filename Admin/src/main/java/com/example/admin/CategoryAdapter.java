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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    private List<ModelCat> lista_cat;

    public CategoryAdapter(List<ModelCat> lista_cat) {
        this.lista_cat = lista_cat;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup vg, int viewType) {
        View view = LayoutInflater.from(vg.getContext()).inflate(R.layout.category_item_layout,vg,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        String title = lista_cat.get(position).getName();
        holder.setData(title,position,this);
    }

    @Override
    public int getItemCount() {
        return lista_cat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView delete;
        private TextView nume_cat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nume_cat = itemView.findViewById(R.id.textView);
            delete = itemView.findViewById(R.id.delete);
        }
        private void setData(String title, int position, CategoryAdapter adapter){
            nume_cat.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CategoryActivity.selected_cat_index= position;
                    Intent intent = new Intent(itemView.getContext(),SetsActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });



            delete.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Sterge Categorie")
                            .setMessage("Sunteti sigur ca doriti sa stergeti categoria?")
                            .setPositiveButton("Sterge", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteCategory(position,itemView.getContext(),adapter);
                                }
                            })
                            .setNegativeButton("Anulare", null)
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

        private void deleteCategory(final int id,final Context context, CategoryAdapter adapter)  {


            FirebaseFirestore firestore = FirebaseFirestore.getInstance();


            Map<String,Object> catDoc = new ArrayMap<>();
            int index=1;
            for(int i=0; i < lista_cat.size(); i++)
            {
                if( i !=id )
                {
                    catDoc.put("CAT" + String.valueOf(index) + "_ID", lista_cat.get(i).getId());
                    catDoc.put("CAT" + String.valueOf(index) + "_NAME", lista_cat.get(i).getName());
                    index++;
                }

            }

            catDoc.put("COUNT", index - 1);

            firestore.collection("CoolturaQuiz").document("Categories")
                    .set(catDoc)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(context,"Categorie stearsa cu succes",Toast.LENGTH_SHORT).show();

                            CategoryActivity.lista_cat.remove(id);

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

    }
}
