package com.example.firebaselab06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText txtID,txtName,txtAdd,txtConNo,txtid;
    Button btnSave,btnShow,btnUpdate,btnDelete;
    DatabaseReference dbRef;
    Student std;
    ListView listview;

    ArrayList<String> student = new ArrayList<>();
    ArrayAdapter myAdapter1;

    Integer indexVal;
    String item;

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

        listview = (ListView)findViewById(R.id.listview);


        btnSave = findViewById(R.id.BtnSave);
        btnShow = findViewById(R.id.BtnShow);
        btnUpdate = findViewById(R.id.BtnUpdate);
        btnDelete = findViewById(R.id.BtnDelete);

        std = new Student();

        myAdapter1 = new ArrayAdapter(this,android.R.layout.simple_list_item_1,student);

        listview.setAdapter(myAdapter1);


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

                        String v1 = txtID.getText().toString();
                        String v2 = txtName.getText().toString();
                        String v3 = txtAdd.getText().toString();
                        String v4 = txtConNo.getText().toString();

                        dbRef.push().setValue(std);
                        student.add(v1);
                        student.add(v2);
                        student.add(v3);
                        student.add(v4);
                        myAdapter1.notifyDataSetChanged();

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

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString() + "has been updated";
                indexVal=i;
                Toast.makeText(MainActivity.this,item,Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String v1 = txtID.getText().toString();
                String v2 = txtName.getText().toString();
                String v3 = txtAdd.getText().toString();
                String v4 = txtConNo.getText().toString();
                student.set(indexVal,v1);
                student.set(indexVal,v2);
                student.set(indexVal,v3);
                student.set(indexVal,v4);
                myAdapter1.notifyDataSetChanged();



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