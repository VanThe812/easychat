package va.vanthe.app_chat_2.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import va.vanthe.app_chat_2.databinding.ItemContainerUserBinding;
import va.vanthe.app_chat_2.listeners.UserListener;
import va.vanthe.app_chat_2.entity.User;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewholder>{

    public static List<User> users;
    public static UserListener userListener;

    public UsersAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UsersViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UsersViewholder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewholder holder, int position) {
        holder.setUserData(users.get(position));


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UsersViewholder extends RecyclerView.ViewHolder {


        ItemContainerUserBinding binding;

        UsersViewholder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }
        void setUserData(User user) {
            binding.textName.setText(user.getLastName());
            binding.imageProfile.setImageBitmap(getUserImage(user.getImage()));
            binding.getRoot().setOnClickListener(v -> {
                userListener.onUserClicked(user);
            });
        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
