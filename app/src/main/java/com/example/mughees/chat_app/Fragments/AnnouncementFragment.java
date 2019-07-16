package com.example.mughees.chat_app.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mughees.chat_app.Adapter.PostUser;
import com.example.mughees.chat_app.Model.post;
import com.example.mughees.chat_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AnnouncementFragment extends Fragment {

    DatabaseReference refrence;
    RecyclerView recyclerView;
    NiceSpinner catogory;
    EditText search_user;
    String finalImage = null;

    private PostUser postUser;
    private List<post> mpost;
    FloatingActionButton floatingActionButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Announvement Fragment");
        View view = inflater.inflate(R.layout.fragment_annousment,container,false);
        recyclerView  = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mpost = new ArrayList<>();

        readPost();

        floatingActionButton = view.findViewById(R.id.add_anous);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writePost();
            }
        });
        search_user = view.findViewById(R.id.search_user);
        search_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return  view;
    }
    private void readPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (search_user.getText().toString().equals("")) {
                    mpost.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        post post = snapshot.getValue(post.class);
                        mpost.add(post);
                    }
                    postUser = new PostUser(getContext(), mpost);
                    recyclerView.setAdapter(postUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void searchUser(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("posts").orderByChild("title")
                .startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mpost.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    post post = snapshot.getValue(post.class);
                    mpost.add(post);

                }

                postUser = new PostUser(getContext(),mpost);
                recyclerView.setAdapter(postUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void writePost() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.write_post,null);
        catogory = (NiceSpinner)view.findViewById(R.id.year);
        final List<String> dataset = new LinkedList<>(Arrays.asList("Sehar", "Iftar"));
        catogory.attachDataSource(dataset);


        String image;
        final EditText title = view.findViewById(R.id.title);
        final EditText description = view.findViewById(R.id.deccript);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.add_anous);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Create Announcement");
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().equals("")||description.getText().toString().equals("")){
                    Toast.makeText(getContext(),"Please enter all fields.",Toast.LENGTH_SHORT).show();
                }
                else if
                        (title.getText().toString().length()>30){
                    title.setError("Title length must be less than 15 characters");
                    title.requestFocus();
                    return;
                }
                else {

                    DatabaseReference reference =  FirebaseDatabase.getInstance().getReference("User");
                    reference.orderByChild("id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                                finalImage= snapshot.child("User_name").toString();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    refrence = FirebaseDatabase.getInstance().getReference("posts");
                    DatabaseReference firebase  = refrence.push();
                    firebase.child("Category").setValue(catogory.getText().toString());
                    firebase.child("title").setValue(title.getText().toString());
                    firebase.child("description").setValue(description.getText().toString());
                    alertDialog.dismiss();
                }
            }
        });
    }

}
