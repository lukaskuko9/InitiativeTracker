package com.example.initiativetracker;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static MainActivity Instance;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    List<RecyclerEntity> entityList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK)  //open file
        {
            Uri selectedfile = data.getData(); //The uri with the location of the file
            //Toast.makeText(getBaseContext(),selectedfile.getPath(),Toast.LENGTH_LONG).show();

            try {
               // ContentResolver cr = getApplicationContext().getContentResolver();
                //InputStream is = cr.openInputStream(selectedfile);
                InputStream is;
                VirtualFileToInputStream vs = new VirtualFileToInputStream(this);

                if (vs.IsVirtualFile(selectedfile)) {
                    //is = vs.GetInputStreamForVirtualFile(selectedfile);
                    Toast.makeText(getBaseContext(),"cant open virtual files",Toast.LENGTH_LONG).show();
                    return;
                } else {
                    is = getContentResolver().openInputStream(selectedfile);
                }

                if (is!=null) {
                    recyclerAdapter.clear();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String str="";

                    while ((str = reader.readLine()) != null) {
                        try {
                            entityList.add(RecyclerEntity.GetInstance(str));
                            recyclerAdapter.notifyDataSetChanged();
                        }
                        catch (Exception e){}
                    }
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else if(requestCode == 1 && resultCode == Activity.RESULT_OK) //save file
        {
            OutputStream os = null;
            try {
                Uri uri = data.getData();
                os = getContentResolver().openOutputStream(uri);

                for(RecyclerEntity re : recyclerAdapter.recyclerEntities)
                    os.write(re.GetTextData().getBytes());

                os.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.menu_nextTurn:
                recyclerAdapter.nextTurn();
                return true;

            case R.id.menu_savefile:

                intent = new Intent(Intent.ACTION_CREATE_DOCUMENT)
                        .setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
               // intent.setType("*/.dnd"); //not needed, but maybe usefull
                startActivityForResult(intent, 1);
                break;

            case R.id.menu_openfile:
                //can user select directories or not
                intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
                break;
            case R.id.menu_clear:
                new AlertDialog.Builder(this)
                        .setTitle("Clear")
                        .setMessage("Are you sure you want to clear data?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                recyclerAdapter.clear();
                            }
                        })

                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Instance = this;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                // View mView = view;
                View mView = MainActivity.Instance.getLayoutInflater().inflate(R.layout.dialogcreatureedit, null);
                //View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);
                final EditText name = (EditText) mView.findViewById(R.id.nameEditText);
                final EditText roll = (EditText) mView.findViewById(R.id.rollEditText);

                TextView textView = mView.findViewById(R.id.textView);
                TextView rowCountTextView = mView.findViewById(R.id.rowCountTextView);


                Button confirm = (Button) mView.findViewById(R.id.btnConfirm);
                Button delete = (Button) mView.findViewById(R.id.btnDelete);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!name.getText().toString().equals(""))
                        {
                            if(roll.getText().toString().equals(""))
                            {
                                roll.setText("0");
                            }
                            entityList.add(new RecyclerEntity(name.getText().toString(),roll.getText().toString()));
                            recyclerAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                        else
                            Toast.makeText(getBaseContext(), "Name can't be empty!", Toast.LENGTH_SHORT).show();
                    }
                });

                delete.setText("Cancel");

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        entityList = new ArrayList<>();


        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(entityList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(entityList, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }
    };
}
