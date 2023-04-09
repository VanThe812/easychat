package va.vanthe.app_chat_2.fragment;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.adapters.ChatAdapter;
import va.vanthe.app_chat_2.adapters.UserCreateGroupAdapter;
import va.vanthe.app_chat_2.adapters.UserCreateGroupCheckerAdapter;
import va.vanthe.app_chat_2.database.ConversationDatabase;
import va.vanthe.app_chat_2.database.GroupMemberDatabase;
import va.vanthe.app_chat_2.database.UserDatabase;
import va.vanthe.app_chat_2.databinding.LayoutFragmentCreateAGroupBinding;
import va.vanthe.app_chat_2.databinding.LayoutFragmentViewImageBinding;
import va.vanthe.app_chat_2.entity.Conversation;
import va.vanthe.app_chat_2.entity.GroupMember;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class DialogViewImageFragment extends androidx.fragment.app.DialogFragment {

    private LayoutFragmentViewImageBinding binding;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Tạo dialog với style không có title
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Set layout cho dialog
        dialog.setContentView(R.layout.layout_fragment_view_image);

        // Set background cho dialog
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Thiết lập kích thước dialog
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());

        // Lấy kích thước màn hình
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        lp.width = width;
        lp.height = height;
        lp.gravity = Gravity.BOTTOM;

        window.setAttributes(lp);

        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = LayoutFragmentViewImageBinding.inflate(inflater, container, false);

        String imagePath = getArguments().getString("image_path");

        Picasso.get().load(imagePath).into(binding.imageViewImage);

        binding.imageBack.setOnClickListener(view -> {
            // Đóng Dialog
            Dialog dialog = getDialog();
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        return binding.getRoot();
    }


}
