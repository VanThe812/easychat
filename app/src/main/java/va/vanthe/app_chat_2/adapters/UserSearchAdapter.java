package va.vanthe.app_chat_2.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import va.vanthe.app_chat_2.activities.ChatMessageActivity;
import va.vanthe.app_chat_2.databinding.ItemContainerUserBinding;
import va.vanthe.app_chat_2.databinding.ItemContainerUserSearchBinding;
import va.vanthe.app_chat_2.entity.Conversation;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.listeners.UserListener;
import va.vanthe.app_chat_2.ulitilies.Constants;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserSearchViewHolder>{

    public static Context mContext;
    public static User mUser;
    private IClickItemUserSearch iClickItemUserSearch;

    public interface IClickItemUserSearch {
        void clickUser(User user, boolean isNewChat);
    }
    public UserSearchAdapter(Context mContext, User user) {
        this.mContext = mContext;
        this.mUser = user;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserSearchBinding itemContainerUserSearchBinding = ItemContainerUserSearchBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserSearchViewHolder(itemContainerUserSearchBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserSearchViewHolder holder, int position) {
        holder.setUserData(mUser);


    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class UserSearchViewHolder extends RecyclerView.ViewHolder {


        ItemContainerUserSearchBinding binding;

        UserSearchViewHolder(ItemContainerUserSearchBinding itemContainerUserSearchBinding) {
            super(itemContainerUserSearchBinding.getRoot());
            binding = itemContainerUserSearchBinding;
        }
        void setUserData(User user) {
            binding.textName.setText(user.getLastName());
            binding.imageProfile.setImageBitmap(getUserImage(user.getImage()));
            binding.textPhoneNumber.setText(user.getPhoneNumber());
            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ChatMessageActivity.class);
                intent.putExtra(Constants.KEY_USER, user);
                intent.putExtra(Constants.KEY_TYPE, Constants.KEY_TYPE_CHAT_SINGLE);
                mContext.startActivity(intent);
            });
        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
