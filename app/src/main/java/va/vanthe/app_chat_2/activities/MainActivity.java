package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.adapters.RecentConversionsAdapter;
import va.vanthe.app_chat_2.databinding.ActivityMainBinding;
import va.vanthe.app_chat_2.adapters.ViewPagerMenuAdapter;
import va.vanthe.app_chat_2.listeners.ConversionListener;
import va.vanthe.app_chat_2.models.ChatMessage;
import va.vanthe.app_chat_2.models.User;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;

    private ViewPagerMenuAdapter viewPagerMenuAdapter;
    private PreferenceManager account; //Bảng tài khoản trong db của máy

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        checkLogin();
        setListeners();
//        listenConversations();
//        dataTest();


    }
    private void init() {
        account = new PreferenceManager(getApplicationContext());

    }

    private void checkLogin() {
        if(!account.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }
    }



    private void setListeners() {
        InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        binding.bottomNav.setSelectedItemId(R.id.action_chat);

        viewPagerMenuAdapter = new ViewPagerMenuAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewPagerMenu.setAdapter(viewPagerMenuAdapter);
        binding.viewPagerMenu.setOffscreenPageLimit(2);
        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_chat:
                    imm2.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    binding.viewPagerMenu.setCurrentItem(0);
                    break;
                case R.id.action_friend:
                    imm2.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    binding.viewPagerMenu.setCurrentItem(1);
                    break;
                case R.id.action_setting:
                    imm2.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    binding.viewPagerMenu.setCurrentItem(2);
                    break;
            }
            return  true;
        });

        binding.viewPagerMenu.setOnTouchListener((view, motionEvent) -> {
            return true;
        });
        binding.viewPagerMenu.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        binding.bottomNav.getMenu().findItem(R.id.action_chat).setChecked(true);
                        break;
                    case 1:
                        binding.bottomNav.getMenu().findItem(R.id.action_friend).setChecked(true);
                        break;
                    case 2:
                        binding.bottomNav.getMenu().findItem(R.id.action_setting).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }






}