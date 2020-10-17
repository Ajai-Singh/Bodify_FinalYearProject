package com.example.bodify;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.Random;

public class DashBoard extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ArrayList<String> motivatingMessages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayMessage();
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_dash_board);
        getSupportActionBar().setTitle("Welcome To Bodify");
    }

    public void displayMessage() {
        motivatingMessages = new ArrayList<>();
        Random random = new Random();
        motivatingMessages.add("YOU’RE THE ONLY ONE WHO CAN MAKE THE DIFFERENCE. WHATEVER YOUR DREAM IS, GO FOR IT. – MAGIC JOHNSON.");
        motivatingMessages.add("EXERCISE SHOULD BE REGARDED AS TRIBUTE TO THE HEART. – GENE TUNNEY.");
        motivatingMessages.add("EXERCISE IS KING. NUTRITION IS QUEEN. PUT THEM TOGETHER AND YOU’VE GOT A KINGDOM. – JACK LALANNE.");
        int index = random.nextInt(motivatingMessages.size());
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(DashBoard.this);
        dlgAlert.setMessage(motivatingMessages.get(index));
        dlgAlert.setTitle("Welcome To Bodify");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logOut) {
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), LogIn.class));
        }
        return true;
    }
}