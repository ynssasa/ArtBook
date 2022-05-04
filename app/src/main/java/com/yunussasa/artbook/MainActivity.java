package com.yunussasa.artbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.yunussasa.artbook.databinding.ActivityMainBinding;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    ArrayList<Art> arrayList;
    ArtAdapter artAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        arrayList=new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artAdapter=new ArtAdapter(arrayList);
        binding.recyclerView.setAdapter(artAdapter);
        getData();

    }


    private void  getData(){
        try {

            SQLiteDatabase sqLiteDatabase=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            Cursor cursor =sqLiteDatabase.rawQuery("Select * from arts",null);
            int nameIndex=cursor.getColumnIndex("artname");
            int idIndex=cursor.getColumnIndex("id");

            while (cursor.moveToNext()){
                String name=cursor.getString(nameIndex);
                int id=cursor.getInt(idIndex);
                Art art=new Art(name,id);
                arrayList.add(art);
            }

            artAdapter.notifyDataSetChanged();
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflaterMenu=getMenuInflater();
        inflaterMenu.inflate(R.menu.art_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.add_Art){
            Intent intent=new Intent(this,ArtActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}