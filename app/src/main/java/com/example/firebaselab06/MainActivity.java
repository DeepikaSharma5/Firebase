package com.example.firebaselab06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText txtID,txtName,txtAdd,txtConNo,txtid;
    Button btnSave,btnShow,btnUpdate,btnDelete;
    DatabaseReference dbRef;
    Student std;

    String searchID,idToBeRemoved,idToBeUpdate;

    private void clearControls(){
        txtID.setText("");
        txtName.setText("");
        txtAdd.setText("");
        txtConNo.setText("");
        txtid.setText("");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtID = findViewById(R.id.EtID);
        txtName = findViewById(R.id.EtName);
        txtAdd = findViewById(R.id.EtAddress);
        txtConNo = findViewById(R.id.EtConNo);
        txtid = findViewById(R.id.search);


        btnSave = findViewById(R.id.BtnSave);
        btnShow = findViewById(R.id.BtnShow);
        btnUpdate = findViewById(R.id.BtnUpdate);
        btnDelete = findViewById(R.id.BtnDelete);

        std = new Student();


    }

    protected void onResume(){
        super.onResume();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbRef = FirebaseDatabase.getInstance().getReference().child("Student");
                try{
                    if(TextUtils.isEmpty(txtID.getText().toString()))
                        Toast.makeText(getApplicationContext(),"Please enter an ID",Toast.LENGTH_SHORT).show();
                    else if(TextUtils.isEmpty(txtName.getText().toString()))
                        Toast.makeText(getApplicationContext(),"Please enter a Name",Toast.LENGTH_SHORT).show();
                    else if (TextUtils.isEmpty(txtAdd.getText().toString()))
                        Toast.makeText(getApplicationContext(),"Please enter an Address",Toast.LENGTH_SHORT).show();

                    else {
                        std.setID(txtID.getText().toString().trim());
                        std.setName(txtName.getText().toString().trim());
                        std.setAddress(txtAdd.getText().toString().trim());
                        std.setConNo(txtConNo.getText().toString().trim());

                        dbRef.push().setValue(std);

                        Toast.makeText(getApplicationContext(),"Data Saved Successfully",Toast.LENGTH_SHORT).show();
                        clearControls();
                    }
                }
                catch (NumberFormatException e){
                    Toast.makeText(getApplicationContext(),"Invalid Contact Number",Toast.LENGTH_SHORT).show();

                }
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchID = txtid.getText().toString().trim();
                DatabaseReference readRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference dref = readRef.child("Student");
                Query query = dref.orderByChild("id").equalTo(searchID);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                txtID.setText((ds.child("id").getValue().toString()));
                                txtName.setText((ds.child("name").getValue().toString()));
                                txtAdd.setText((ds.child("address").getValue().toString()));
                                txtConNo.setText((ds.child("conNo").getValue().toString()));
                            }
                        }
                        else
                            Toast.makeText(getApplicationContext(),"No Source to Display",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("TAG", databaseError.getMessage()); //Don't ignore potential errors!
                    }
                });
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idToBeUpdate = txtid.getText().toString().trim();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference studentRef = rootRef.child("Student");
                Query query = studentRef.orderByChild("id").equalTo(idToBeUpdate);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            //ds.getRef().updateChildren(std,s);
                            clearControls();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("TAG", databaseError.getMessage()); //Don't ignore potential errors!
                    }
                });


            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idToBeRemoved = txtid.getText().toString().trim();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference studentRef = rootRef.child("Student");
                Query query = studentRef.orderByChild("id").equalTo(idToBeRemoved);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            ds.getRef().removeValue();
                            clearControls();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("TAG", databaseError.getMessage()); //Don't ignore potential errors!
                    }
                });

            }
        });

    }

}