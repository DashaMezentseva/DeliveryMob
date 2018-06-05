package mezentseva.com.android.delivery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import mezentseva.com.android.delivery.Common.Common;
import mezentseva.com.android.delivery.Database.Database;
import mezentseva.com.android.delivery.Interface.ItemClickListener;
import mezentseva.com.android.delivery.Model.Food;
import mezentseva.com.android.delivery.ViewHolder.FoodViewHolder;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;
    String categoryId="";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //Search functionality
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //Favorites
    Database localDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Food");

        //Local DB
        localDB = new Database(this);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Get intent here
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty() && categoryId != null){
            if (Common.isConnectedToInternet(getBaseContext()))
                loadListFood(categoryId);
            else {
                Toast.makeText(FoodList.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //Search
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");
        //materialSearchBar.setSpeechMode(false);
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<String>();
                for(String search:suggestList){
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When searh bar is close
                //Restore original adapter
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //When search finish
                //Show result of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("Name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey()); // Send FoodId to new Activity
                        startActivity(foodDetail);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        foodList.orderByChild("MenuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName()); //Add name of food to suggest list
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("MenuId").equalTo(categoryId)
                ){

            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {
                viewHolder.food_name.setText(model.getName());
                viewHolder.food_price.setText(String.format("$ %s",model.getPrice().toString()));
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);

                //Add Favorites
                if (localDB.isFavorite(adapter.getRef(position).getKey()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                //Click to change state of Favorites
                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localDB.isFavorite(adapter.getRef(position).getKey())){
                            localDB.addToFavorite(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, ""+model.getName()+" was added to Favorites", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            localDB.removeFromFavorite(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this, ""+model.getName()+" was removed from Favorites", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey()); // Send FoodId to new Activity
                        startActivity(foodDetail);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);

    }
}
