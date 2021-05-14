package com.example.mannaprototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.mannaprototype.models.ResidentModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {
    private DrawerLayout drawer;
    private Button btn;
    private NavigationView navigationView;
    private ResidentModel resident;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerlayout);

        drawer = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentTransaction transaction;
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.myframelayout, new HomeFragment());
        transaction.commit();

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            if(menuItem.getItemId() == R.id.btnhome){
                setFragment(new HomeFragment());
            }else if(menuItem.getItemId() == R.id.btnprofile){
                setFragment(new ProfileFragment());
            }else if(menuItem.getItemId() == R.id.btnregistration){
                setFragment(new RegistrationFragment());
            }else if(menuItem.getItemId() == R.id.btnscan){
                setFragment(new ScanFragment());
            }else if(menuItem.getItemId() == R.id.btnresident){
                setFragment(new ResidentFragment());
            }else if(menuItem.getItemId() == R.id.btnvisitor){
                setFragment(new VisitorFragment());
            }else if(menuItem.getItemId() == R.id.btnlogout){
                FirebaseAuth.getInstance().signOut();
                finish();
            }else{
                setFragment(new HomeFragment());
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        Menu menu = navigationView.getMenu();

        if (resident.getUserType().equalsIgnoreCase("resident")) {
            menu.findItem(R.id.btnregistration).setVisible(false);
            menu.findItem(R.id.btnscan).setVisible(false);
            menu.findItem(R.id.btnresident).setVisible(false);
            menu.findItem(R.id.btnvisitor).setVisible(false);
        }else if (resident.getUserType().equalsIgnoreCase("guard")) {
            menu.findItem(R.id.btnprofile).setVisible(false);
        }

        resident = (ResidentModel) getIntent().getSerializableExtra("resident");

    }

    public void openMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(!drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.openDrawer(GravityCompat.START);
                }else{
                    drawer.closeDrawer(GravityCompat.START);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    protected void setFragment(Fragment fragment) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.myframelayout, fragment);
        t.commit();
    }
}