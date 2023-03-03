package va.vanthe.app_chat_2.fragment;

import static va.vanthe.app_chat_2.adapters.UsersAdapter.userListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.activities.ChatActivity;
import va.vanthe.app_chat_2.adapters.RecentConversionsAdapter;
import va.vanthe.app_chat_2.adapters.UserChatAdapter;
import va.vanthe.app_chat_2.adapters.UsersAdapter;
import va.vanthe.app_chat_2.adapters.ViewPagerMenuAdapter;
import va.vanthe.app_chat_2.adapters.ViewPagerSearchAdapter;
import va.vanthe.app_chat_2.databinding.LayoutFragmentChatBinding;
import va.vanthe.app_chat_2.listeners.ConversionListener;
import va.vanthe.app_chat_2.listeners.UserListener;
import va.vanthe.app_chat_2.models.ChatMessage;
import va.vanthe.app_chat_2.models.User;

public class ChatFragment extends Fragment  implements ConversionListener, UserListener {

    private LayoutFragmentChatBinding binding;
    private List<ChatMessage> conversation;
    private RecentConversionsAdapter recentConversionsAdapter;

    private String IMAGE_GROUP_CHAT_DEFAULT = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDABALDA4MChAODQ4SERATGCgaGBYWGDEjJR0oOjM9PDkzODdASFxOQERXRTc4UG1RV19iZ2hnPk1xeXBkeFxlZ2P/2wBDARESEhgVGC8aGi9jQjhCY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2NjY2P/wAARCAA3AJYDASIAAhEBAxEB/8QAGgAAAwEBAQEAAAAAAAAAAAAAAAQFAwYBAv/EADoQAAEDAwMCAwQGCQUAAAAAAAECAxEABCEFEjFBURMiYRRxgaEGFTKRsfAjJDNCYsHR4fE1UmOisv/EABoBAAMBAQEBAAAAAAAAAAAAAAIDBAUBAAb/xAAvEQACAgEEAQEFBwUAAAAAAAABAgADEQQSITETUQUiQXGxMmGRwdHh8BQzQoGh/9oADAMBAAIRAxEAPwDrnn2bdAW+6hpJMArUEifjWlcprK/rTSSoge123mGdoWkwD1gHg/DHNRLY6oxdkF15N0lKUNgO7kpBEpiJBEgY4M8VPUDaNyjIldgFZ2scGd69eoav7e0KFlb4UQY8oCR3rdxRSmUxMgZ99Q2tXVdamhSWQlpCFgFWTnM+n2QPvplD4cfQtax9oZOKTa5rYo3cKtN6hx1KSXEqnIlMz2FehxCjCVpJ9DSDd80244SFEFRIIFe2qgEKUTjckzQC7oQjURyZQopQXyd2UHb3nNLN/SCwc1Y6cFqDswlcDYpUA7QZ5z1jIinowf7MWyle5UoqBqetX1rqT1vb2ja22WfEJWSCRH2hkYBMR6H4bN68wdMYcuHW2Ll9B2JBkTJAPoJ7/wAjTAhPUI1lQGPRlmvNw3Rn7jH30npZcXbFa3CtJPklYXjvuHOZouiDctkGRA499JdyozPBPe25mz76m1AJAIImvUOqUUykZrC6WlCkbjEppOxeWnWrlhx7clxDbrKOdoiFfMDHr76VvO4jMPZ7ucSupxCTBVnr6e/tU7XC77KjYJaKvOR8vh/aoun/AEhGqai4040lhREoSVyTH49/SDXVNfsx8aI5fKHidA8RV+5yRIEHYVAcpBgn3GsG9RbDym2ztJhCiUkkHIKZ4E/Hg03qdkrx3/aNR3NlwlDLbCEeUhQKdwyYkZPbg9ElMWAQtNuHEtg7vMZLZP8AtPIHHPxpaacKpZgSPUD14+sq/qQ5Cjg+hPpz9I0tOxZTuSrsUnBopNGqLtrZK4S4QophaRBkA7gCDyIori+z7nyVH0/MiG2srXgn6/pGbltaGFKUkpA5JGOafa0pbqbW9W6lLzaEKbSlJhcRG455AjA7HpXzrP8Apb3mKeII7yKpLu7dq2bFooOpENILZ3pB4AJHHIGe9UgNTRtTJOf9STUDdYHOOsfz8ZNtkJLKiwlW1CilU8pUMEGOtfThDSCtwhCRypWAKqNrU7Z71bTuSSCnII6Hk9I60sRIg8VIi+cszHnMbW2F2gdRB1TnitobGDlSuw/v/KqDatrOzvFSkN+JdeCu0dQynCYwDzg+gkxBjPGKqAACAIHpTWpBUBDyPunMNuO6V7KzbQyHHUhalgEAiQAa5HU7Ziy+kqrnTXSX3CFBtuFBJUnmOs7uOk+6FtO+nGqKuLW1W3aLSpaGyvYoEiQJ5ifhVlrT22Cp2yuzbOEK3MuN72lzGI6AwJ+VXIooK+7kfH75AVe1WIPMTYutN1JSTqzSnH0+QODckBPIBAIzntU562abcu0exv3DZbJtiJ/QDJyJ480z/U06LFdypZfd9nu95WBIWlwdxJnvg/fNalKnfa2Q08ltcNhZ6xBxiQOkzGBHNU2Om4MnAPfYxz8P2nqUsNTJZyR1zF7TUXkW1nYWiDAEKSvlSjJMxwJOOPWnLJ2/bu0IvWVJCpIHlMAdZGP81n7ErT7a4uLRbSLsIJD60fs0jKjgE8A9DVzQbi21O2F0hTi2lEpQl9MHdmQBJnAnBjnrMK1Lo7YrQEHjJ7z6/wA766iq67E96xiD3j8phcq9oUnlISI7zUp1ppWp21y7qDdupBCFILgBUgeYSMQDBEn0q3ftBm7UEpCUmCAPz3mpeorDFq66VJSIghQBBHXB9KzEosNm3HfH7zRLhat3wAzMri403UNYLjTafa7aSm4aIKXElISQT1iY9I55FNMPLYcCkkxORPNRtFa/WnICJKPMsJA3GRMAYA9P6Va8H+L5V2/SXUvt7xPaayuyvK9GIX1sm5IUpZSsSSoyRHqRkc8jvXgsHLaUK2lfXacVS2oZcbSGi9cK8wG4pAAz7uRXwIuEB9BIDnmAJnr3oTde9Pg+H8/5DWmtbvMBzOQ1BvwbxxoDakEEJ6CQKK11obdVeB/h/wDIor6bT58KZ7wPpMm7+43zMr6o8dQ0ptVklx3e4ApCASRg4IHrHypjQ3VaZpa03LLqHFOkpQpO0kbRnPSuatL65sio2znhlUTgHjjmqbGre3XKU3ig2pWAuSUjsIPHXM9ay9ZpLPGVQZEvTVK4CNxL7WqtOAoW0puRAMyKkaw9fJ1O0Q0VotR5ypAncofuqzxwPj14p76v/wCX/r/ekiSoyoknuaztOrVAgjuWJSH4BxKofSLfxXULQdwSUxJBIn8KDcshIKg8kETJb+7r1qSLlOxVu4FeGVgqIgkAcxPfGfStm9R0tT7DQZebTJSrelMGcSTuxFdZrF5OcfL0ghk3Fc5x6SDqmlfUzdreC5Up4ufo0OMlJITmftHjyiMV2ZqY5v1cl621G5tRuCVNNqAhAnGIiZnkj3wIctLdbFslp99VypMedYA4/PWT61UHLoCx5ia0KEjGBPssNKWVqbSpUgglIkRx+FLWibht1QcaXtUftKd3bQBgR/P755qZbe3+Myhz2nwQtMpVu2gAjpXQU6+pqyBuzB09osBO3EwQ6tTzwU2NqFFIVGY2pPxyTx2qA1da41eXDlu+t5+zUUqAICVpIwdgweDjmSO1dQ4oOsJZWhBQJkR9qeZ70obC18NSEMpaSr7XhS3Pv2xPNKq2oSx5PynbKy4wZ8aNq17q2l+JqDad4XLTqYG9JmRA4ggD1+8n41UKcDTKE7iskx7v81qs2+mWO1K0stpwjdKoJ9Jk1Bs7hlnVjc3GpLfgHzKYgGeickjnt371RUWFnkVc4ibUXxeInGY+209p18ylQLjbhCPFkDtz2M9qrNOoeSVNmQFKTMdQSD8wal3WlnVnU3Kr902ahKGmxA4iZ7zPSelUWLcsW5aacJMeVTgBgxyYiZMk9yTml3WmzBbsRlFQqyF6i97a3d1qDG2EMMIcJK3RtWSkbRCSTz6dK+tL0xOlWymE3BuNyysrKNuSAIiT2p1htRYaQ+pLa0RJbc3hWCIKiJPQzAzWikISpJSdwJODjjn8+6oxqV+zjEMd5M5fVtLvLnUXXWWdyFRB3AdAO9FdIqNxjicUVoJrHVQABFtpUYkkmcCkhJkpCsEQfxr3xFeF4cJ27t07RM+/mPSiitbAmZKDmu3hQ2hpYbCEgEwFFRgZJP5zS6dQuAfMvcO0AUUUAprH+IjDbYRjcfxj1sW7lTWxSwVEJVu4SSenfpVO3srP6xfShsENISlSFCQFGTOfSKKKj9o8lAY72cMGzHrN7w+yH20GG207XU5ymcR0kE/PmkG/pAXnyhu2O0BSioqztAJJjvA4miik0VK1buRyP0jtRc6uFEl3ur3V2uQstIEgIQojHr3os9XurZ4KW4t5H7yFqmfiZiiitLxJt244kfkfO7PM6u3fbuWEvNGUK4kRSl5q9pZuFtwrU4kiUpTkYnriiis2qlWtKHoS+y1lrDDszlLi4eunfEfWVriJPasqKK1QABgTNJJ5MYs7t2yf8VkgGIIIkETMfKnrnX7txw+ApLSATthIkjpMz8qKKBqkY7iIS2MowDNtP191LoRekKbUf2gTBT8ByPnn4V0qVqTxBwRn1oorN1tKDGB3LtK5cENzPFK3GevWiiiowMDAlc//2Q==";


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e("Vanthe", "Chat");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentChatBinding.inflate(inflater, container, false);

        init();
        loadUserDetails();
        setListeners();
        listenConversations();
        dataTest();
        return binding.getRoot();

    }

    private void init() {
        conversation = new ArrayList<>();
        // khởi tạo rcv
        recentConversionsAdapter = new RecentConversionsAdapter(conversation, this);
        binding.conversionsRCV.setAdapter(recentConversionsAdapter);

    }
    private void loadUserDetails() {
        byte[] bytes = Base64.decode(IMAGE_GROUP_CHAT_DEFAULT, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }
    private void setListeners() {
        binding.textviewCancel.setOnClickListener(view -> {
            binding.imageProfile.setVisibility(View.VISIBLE);
            binding.textName.setVisibility(View.VISIBLE);
            binding.imageCreateGroupChat.setVisibility(View.VISIBLE);

            binding.userChatRCV.setVisibility(View.VISIBLE);
            binding.conversionsRCV.setVisibility(View.VISIBLE);

            binding.textviewCancel.setVisibility(View.GONE);
            binding.viewpagerSearch.setVisibility(View.GONE);
            binding.inputSearch.clearFocus();
            binding.inputSearch.setText("");
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        });
        binding.inputSearch.setOnFocusChangeListener((view, b) -> {
            if(b) {
                binding.imageProfile.setVisibility(View.GONE);
                binding.textName.setVisibility(View.GONE);
                binding.imageCreateGroupChat.setVisibility(View.GONE);

                binding.userChatRCV.setVisibility(View.GONE);
                binding.conversionsRCV.setVisibility(View.GONE);

                binding.textviewCancel.setVisibility(View.VISIBLE);

                binding.viewpagerSearch.setVisibility(View.VISIBLE);
                ViewPagerSearchAdapter adapter = new ViewPagerSearchAdapter(getActivity().getSupportFragmentManager());
                binding.viewpagerSearch.setAdapter(adapter);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.conversionsRCV.setLayoutManager(layoutManager);

        binding.conversionsRCV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (layoutManager.findFirstVisibleItemPosition() == 0) {
                        binding.layout1.setVisibility(View.VISIBLE);
                    }

                } else {
                    if (layoutManager.findFirstVisibleItemPosition() == 0) {
                        binding.layout1.setVisibility(View.GONE);
                    }

                }
            }
        });
        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            Bundle bundle = new Bundle();
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String textSearch = editable.toString().trim();
                if (textSearch.matches("")) {
                    SearchFragment.binding.layoutRecent.setVisibility(View.VISIBLE);
                    SearchFragment.binding.layoutSuggest.setVisibility(View.VISIBLE);
                    SearchFragment.binding.searchRCV.setVisibility(View.GONE);

                } else {
                    SearchFragment.binding.layoutRecent.setVisibility(View.GONE);
                    SearchFragment.binding.layoutSuggest.setVisibility(View.GONE);
                    SearchFragment.binding.searchRCV.setVisibility(View.VISIBLE);

                    List<User> users = new ArrayList<>();
                    for (int i = 0; i<8; i++) {
                        User user = new User();
                        user.name = "Vu Van The" + (i+1);
                        user.image = IMAGE_GROUP_CHAT_DEFAULT;
                        user.id = ""+i;
                        users.add(user);
                    }
                    UsersAdapter usersAdapter = new UsersAdapter(users, userListener);
                    SearchFragment.binding.searchRCV.setAdapter(usersAdapter);
                    SearchFragment.binding.searchRCV.setVisibility(View.VISIBLE);

                }


            }
        });

    }

    private void listenConversations() {
        for (int i = 0; i<19; i++) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.receiverNickname = "Test";
            chatMessage.conversionImage = IMAGE_GROUP_CHAT_DEFAULT;
            chatMessage.message = "Tin nhan " + i;
            conversation.add(chatMessage);
        }

//        Collections.sort(conversation, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
        recentConversionsAdapter.notifyDataSetChanged();
        binding.conversionsRCV.smoothScrollToPosition(0);
        binding.conversionsRCV.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }
    private void dataTest() {
        List<User> users = new ArrayList<>();
        User empty = new User();
        empty.image = "";
        empty.id = "-1";
        users.add(empty);
        for (int i = 0; i<10; i++) {
            User user = new User();
            user.name = "Vu Van The" + (i+1);
            user.image = IMAGE_GROUP_CHAT_DEFAULT;
            user.id = ""+i;
            users.add(user);
        }
        UserChatAdapter userChatAdapter = new UserChatAdapter(users, getContext());
        binding.userChatRCV.setAdapter(userChatAdapter);
        binding.userChatRCV.setVisibility(View.VISIBLE);
    }
    @Override
    public void onConversionCLicked(User user) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        // chuyền thông tin của ng muốn nhắn tin qua activity chat
//        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }

    @Override
    public void onUserClicked(User user) {
        Toast.makeText(getContext(), "Teext", Toast.LENGTH_SHORT).show();
    }
}
