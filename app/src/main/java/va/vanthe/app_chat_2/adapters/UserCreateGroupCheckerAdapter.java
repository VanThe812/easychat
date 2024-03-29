package va.vanthe.app_chat_2.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import va.vanthe.app_chat_2.databinding.ItemContainerUserCreateGroupBinding;
import va.vanthe.app_chat_2.databinding.ItemContainerUserCreateGroupCheckerBinding;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.listeners.UserCreateGroupListener;

public class UserCreateGroupCheckerAdapter extends RecyclerView.Adapter<UserCreateGroupCheckerAdapter.UserCreateGroupCheckerViewHolder>{

    public static List<User> users;
    public static UserCreateGroupCheckerListener userCreateGroupCheckerListener;

    public UserCreateGroupCheckerAdapter(List<User> users, UserCreateGroupCheckerListener userCreateGroupCheckerListener) {
        this.users = users;
        this.userCreateGroupCheckerListener = userCreateGroupCheckerListener;
        notifyDataSetChanged();
    }
    public interface UserCreateGroupCheckerListener{
        void onRemoveUser(User user);
    }

    @NonNull
    @Override
    public UserCreateGroupCheckerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserCreateGroupCheckerBinding binding = ItemContainerUserCreateGroupCheckerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserCreateGroupCheckerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCreateGroupCheckerViewHolder holder, int position) {
        holder.setData(users.get(position));


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserCreateGroupCheckerViewHolder extends RecyclerView.ViewHolder {


        ItemContainerUserCreateGroupCheckerBinding binding;

        UserCreateGroupCheckerViewHolder(ItemContainerUserCreateGroupCheckerBinding itemContainerUserCreateGroupCheckerBinding) {
            super(itemContainerUserCreateGroupCheckerBinding.getRoot());
            binding = itemContainerUserCreateGroupCheckerBinding;
        }
        void setData(User user) {
            binding.textName.setText(user.getLastName());
            Picasso.get().cancelRequest(binding.imageProfile);
            if (user.getImage() != null) {
                StorageReference imagesRef = FirebaseStorage.getInstance().getReference()
                        .child("user")
                        .child("avatar")
                        .child(user.getImage());
                imagesRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> Picasso.get().load(uri).resize(50, 50).into(binding.imageProfile))
                        .addOnFailureListener(Throwable::printStackTrace);
            }
            binding.getRoot().setOnClickListener(v -> {
                userCreateGroupCheckerListener.onRemoveUser(user);
            });
        }
    }

}
