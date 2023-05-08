package va.vanthe.app_chat_2.thiandroidNC.hoten_lop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import va.vanthe.app_chat_2.database.ConversationDatabase;
import va.vanthe.app_chat_2.databinding.ActivityDanhSachTaiKhoanBinding;
import va.vanthe.app_chat_2.entity.Conversation;
import va.vanthe.app_chat_2.fragment.MenuChatFragment;

public class DanhSachTaiKhoanActivity extends AppCompatActivity {

    private ActivityDanhSachTaiKhoanBinding binding;
    private TaiKhoanAdapter taiKhoanAdapter;
    private List<TaiKhoan> taiKhoanList = new ArrayList<>();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDanhSachTaiKhoanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListener();
    }
    private void init() {


        getTaiKhoan();
    }
    private void getTaiKhoan() {
        database.collection(Constants.KEY_TAIKHOAN)
                .addSnapshotListener((value, error) -> {
                    if(error != null) {
                        return;
                    }
                    if(value != null) {
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if(documentChange.getType() == DocumentChange.Type.ADDED) {
                                DocumentSnapshot taiKhoanSnapshot = documentChange.getDocument();
                                TaiKhoan taiKhoan = new TaiKhoan();
                                taiKhoan = taiKhoanSnapshot.toObject(TaiKhoan.class);
                                taiKhoan.setId(taiKhoanSnapshot.getId());

                                taiKhoanList.add(taiKhoan);

                            }
                        }
                        TaiKhoanAdapter taiKhoanAdapter = new TaiKhoanAdapter(taiKhoanList);
                        binding.RCVListTaiKhoan.setAdapter(taiKhoanAdapter);
                    }
                });
    }
    private void setListener() {
        binding.buttonAddTaiKhoan.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), TaiKhoanActivity.class);
            startActivity(intent);
        });
    }
}