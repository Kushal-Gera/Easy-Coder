package com.example.easycoder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

//    some Constants
    public static final String BOOKS = "books";
    public static final String NAME = "name";
    public static final String LINK = "link";

//again firebase stuff
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseRecyclerOptions<pdf_model> options;
    FirebaseRecyclerAdapter<pdf_model, pdf_viewholder> adapter;

    ProgressDialog pd;
    RecyclerView recyclerView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading Data...");
        pd.show();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference(BOOKS);

        if (auth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        call this to add new books to the lot :
//        generateData(15);

        options = new FirebaseRecyclerOptions.Builder<pdf_model>()
                .setQuery(ref, pdf_model.class).build();

//        make a fireBase adapter and its done !!
        adapter = new FirebaseRecyclerAdapter<pdf_model, pdf_viewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final pdf_viewholder holder, int i, @NonNull pdf_model model) {
                String book_ref = getRef(i).getKey();

                if (book_ref != null){
                    ref.child(book_ref).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                holder.name.setText(String.valueOf(dataSnapshot.child(NAME).getValue()));
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //take to download link / or download pdf
                                        String link = String.valueOf(dataSnapshot.child(LINK).getValue() );
                                        try{
                                            startActivity(new Intent( Intent.ACTION_VIEW, Uri.parse(link) ));
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            Log.e(TAG, "onClick: Error" + e.getMessage() );
                                            Toast.makeText(HomeActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                                pd.dismiss();

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: due to some error " + databaseError.getMessage() );
                        }
                    });
                }

            }

            @NonNull
            @Override
            public pdf_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_list, parent, false);
                return new pdf_viewholder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout){

            //converge all this to a function and include a dialog box
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do You Really Want To Sign Out ?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth.signOut();
                        finish();
                        startActivity(new Intent(HomeActivity.this, MainActivity.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //none
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void generateData(final int x){

        for (int i = 1; i< x; i++){
//            locale.english is totally optional for now.....
            String str = String.format(Locale.ENGLISH, "new_book%d", i);

            ref.child(str).child(LINK).setValue("kankckcsnk");
            ref.child(str).child(NAME).setValue("hahha");

        }

    }





}
