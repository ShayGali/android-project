package com.example.androidproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.androidproject.LoadingAlert;
import com.example.androidproject.R;
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

    LoadingAlert loadingAlert = new LoadingAlert(this);

    ListView listViewData; // the list of the the categories checkboxes
    ArrayAdapter<String> adapter; // the adapter of the data
    // the categories
    String[] categories = {"FPS", "RPG", "MMO", "Fantasy", "Action", "Puzzle", "Adventure", "Sports", "Online", "CO-OP", "XBox", "PC", "PlayStation"};

    EditText countryInput;
    EditText userNameInput;

    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef; // reference to the path that the user data is save on the database
    User userModel; // the user model

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

        // כדי לאתחל את ה user על ההתחלה- אם לא הוא יהיה ב null בלחיצה הראשונה
        // displayUserInfoToTheInputFields והוא גם ייקרא ל

        //start loading alert
        // will dismiss when the thread end
        loadingAlert.startLoadingDialog();
        Thread thread = new Thread(this::getUserData);
        thread.start();

    }

    /**
     * will the the user info in the database
     * @param view the button that pressed
     */
    public void submitData(View view) {
        // לקבל את הנתונים של היוזר מהדאטה בייס
        Thread thread = new Thread(() -> {
            getUserData();
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
            if (country.trim().length() > 0) userModel.setCountry(country);
            if (userName.trim().length() > 0) userModel.setUserName(userName);

            userModel.setCategories(selectedCategories);

            myRef.setValue(userModel);
            showToast("The data was successfully saved in the database");
        });
        thread.start();

    }


    /**
     *  get the user data from the data base
     *  if the user have already data it will put the data in the input field and he can change it
     *  else he will initialize the variable with all the fields to be null
     */
    public void getUserData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()) {
                    userModel = dataSnapshot.getValue(User.class);
                    displayUserInfoToTheInputFields();
                } else
                    userModel = new User();

                // dismiss the loading alert
                loadingAlert.dismissDialog();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //TODO לראות מה לעשות אם לא הצלחנו לגשת לנתונים
                userModel = new User();
            }
        });
    }


    /**
     * this method display the user data to the input field if there is any data save on the data
     */
    void displayUserInfoToTheInputFields() {
        if (userModel.getCategories() != null) {
            for (int i = 0; i < listViewData.getCount(); i++) {
                if (userModel.getCategories().contains(listViewData.getItemAtPosition(i).toString()))
                    listViewData.setItemChecked(i, true);
            }
        }
        if (userModel.getUserName() != null)
            userNameInput.setText(userModel.getUserName());
        if (userModel.getCountry() != null)
            countryInput.setText(userModel.getCountry());
    }

    /**
     * help me make a Toast from thread
     * @param displayMsg the message that will be display
     */
    public void showToast(final String displayMsg) {
        runOnUiThread(() -> Toast.makeText(this, displayMsg, Toast.LENGTH_SHORT).show());
    }
}