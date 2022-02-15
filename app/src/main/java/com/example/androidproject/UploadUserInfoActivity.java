package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;

import com.example.androidproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UploadUserInfoActivity extends AppCompatActivity {

    GridView listViewData;
    ArrayAdapter<String> adapter;
    String[] categories = {"FPS", "RPG", "MMO", "Fantasy", "Action", "Puzzle", "Adventure", "Sports", "Online", "CO-OP", "XBox", "PC", "PlayStation"};

    EditText countryInput;
    EditText userNameInput;

    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_user_info);


        listViewData = findViewById(R.id.listView_preferences_categories_on_user_info);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, categories);
        listViewData.setAdapter(adapter);

        countryInput = findViewById(R.id.country_input_user_info_act);
        userNameInput = findViewById(R.id.username_input_user_info_act);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(currentUser.getUid());
    }


    public void submitData(View view) {
        // לקבל את הנתונים של היוזר מהדאטה בייס
        User user = new User();

        // לקבל את כל מה שהוא בחר מהרשימה
        ArrayList<String> selectedCategories = new ArrayList<>();
        for (int i = 0; i < listViewData.getCount(); i++) {
            if (listViewData.isItemChecked(i)) {
                selectedCategories.add(listViewData.getItemAtPosition(i).toString());
            }
        }

        String country = countryInput.getText().toString();
        String userName = userNameInput.getText().toString();

        // בדיקה אם באמת הכניסו לי את השדות
        if (country.trim().length() > 0) user.setCountry(country);
        if (userName.trim().length() > 0) user.setUserName(userName);

        user.setCategories(selectedCategories);

        myRef.setValue(user);
    }

    // TODO: להפוך את זה לאסינכרוני
    public User getUserData() {
        // כדי שנוכל לגשת לתוך המחלקה הפנימית זה צריך להיות ב final
        // כדי שנוכל לשנות את הערך למה שקיבלנו נשים את זה במערך
        final User[] user = {null};
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                user[0] = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        return user[0];
    }
}