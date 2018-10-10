package com.example.user.how_about_a_cafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MenuList extends AppCompatActivity {
    public static DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final String PREFS_NAME = "FILE_PREFERENCES";
    private static final String FAVORITES = "ITEM_FAVORITE";
    private int img;
    private ImageButton actionButton;
    private TextView cafe_title;
    private ExpandableListView listView;
    private ImageView image;
    private String url;
    private myGroup side;
    private myGroup drink;
    private boolean click = false;
    private String data;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        Intent intent = getIntent();
        Item item = new Item(intent.getStringExtra("cafe_name"));
        url = intent.getStringExtra("url");
        img = intent.getIntExtra("img", 0);
        data = item.getCafe_name();

        final ArrayList<myGroup> DataList = new ArrayList<myGroup>();
        listView = (ExpandableListView) findViewById(R.id.expanded_menu);
//        image = (ImageView) findViewById(R.id.childImage);
        FloatingActionButton cal_btn = (FloatingActionButton) findViewById(R.id.cal_btn);
        actionButton = findViewById(R.id.favorite_button);

        cafe_title = findViewById(R.id.cafe_name_tittle);
        cafe_title.setText(data);

        Toolbar mytoolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(mytoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final String json = preferences.getString(FAVORITES, null);


        if (json != null && json.contains(data)) {
            actionButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else actionButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);


        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFavo(json, actionButton, img, data, url);
            }
        });

        cal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user == null)
                    Toast.makeText(MenuList.this, "로그인 후 이용해주세요", Toast.LENGTH_SHORT).show();
                else {
                    Cal_Custom_Dialog cal_custom_dialog = new Cal_Custom_Dialog(MenuList.this);
                    cal_custom_dialog.callFunction(data);
                }
            }
        });

        firebaseDatabase.child(data).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                side = new myGroup("사이드 메뉴");

                firebaseDatabase.child(data).child("사이드메뉴").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            mStorageRef.child(data + "/사이드메뉴/" + snapshot.getKey() + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    side.child.add(snapshot.getKey());
                                    side.childPrice.add(snapshot.getValue().toString() + "원");
                                    side.childImage.add(uri);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("sidemenu failed", "Failed");
                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                DataList.add(side);

                drink = new myGroup("음료");
                firebaseDatabase.child(data).child("음료").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getKey().equals("SIZE")) {
                                drink.child.remove("SIZE");
                                drink.childPrice.remove(snapshot.getValue() + "원");
                            }

                            if (snapshot.getKey().equals("SIZE2")) {
                                drink.child.remove("SIZE2");
                                drink.childPrice.remove(snapshot.getValue() + "원");
                            }


                            if (snapshot.getKey().equals("ICE")) {
                                drink.child.remove("ICE");
                                drink.childPrice.remove(snapshot.getValue() + "원");
                            }

                            if (snapshot.getKey().equals("ICE2")) {
                                drink.child.remove("ICE2");
                                drink.childPrice.remove(snapshot.getValue() + "원");
                            }

                            if (snapshot.getKey().equals("SIZE_CNT")) {
                                drink.child.remove("SIZE_CNT");
                                drink.childPrice.remove(snapshot.getValue() + "원");
                            }
                            mStorageRef.child(data + "/음료/" + snapshot.getKey() + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (snapshot.getKey().equals("ICE") || snapshot.getKey().equals("ICE2") || snapshot.getKey().equals("SIZE")) {
                                        return;
                                    } else {
                                        drink.child.add(snapshot.getKey());
                                        drink.childPrice.add(snapshot.getValue().toString() + "원");
                                        drink.childImage.add(uri);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("drink_fail", "Failed");
                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                DataList.add(drink);

                ExpandAdapter adapter = new ExpandAdapter(getApplicationContext(), R.layout.activity_menu_list_group_row, R.layout.activity_menu_list_child_row, DataList);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                CustomDialog customDialog = new CustomDialog(MenuList.this);
                customDialog.callFunction(DataList.get(i).child.get(i1), data, String.valueOf(i), DataList.get(i).childImage.get(i1));
                return true;
            }
        });
    }


    private void addFavo(String json, ImageButton actionButton, int img, String data, String url) {
        ListItem item = new ListItem(img, data, url); // pasang objek yang mau ditambahin ke recyclerview

        /* ngecek apakah itemnya udeh ada di favorit ape belum */
        if (json != null && json.contains(data)) { // andaikata udah ada
            actionButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            Toast.makeText(getApplicationContext(), "즐겨찾기 취소", Toast.LENGTH_SHORT).show();
            SharedPref sharedPref = new SharedPref();
            int position = sharedPref.setIndex(getApplicationContext(), data);
            sharedPref.removeFavorite(getApplicationContext(), position); // maka metodenya adalah remove item
            sharedPref.removeIndex(getApplicationContext(), data); // nah ini remove string single nya buat acuan posisi

        } else {
            actionButton.setImageResource(R.drawable.ic_favorite_black_24dp);
            SharedPref sharedPref = new SharedPref();
            sharedPref.addFavorite(getApplicationContext(), item); // ya tambahin deh
            sharedPref.addIndex(getApplicationContext(), data);

            Toast.makeText(getApplicationContext(), "즐겨찾기 추가", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.evnet:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                break;

            case R.id.review_btn:
                Intent intent1 = new Intent(MenuList.this, ListReview.class);
                intent1.putExtra("cafe_name", data);
                startActivity(intent1);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("refresh", "true");
        setResult(RESULT_OK, intent);
        finish();
    }

}
