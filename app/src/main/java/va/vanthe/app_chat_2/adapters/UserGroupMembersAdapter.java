package va.vanthe.app_chat_2.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import va.vanthe.app_chat_2.databinding.ItemContainerUserCreateGroupBinding;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.listeners.UserCreateGroupListener;
import va.vanthe.app_chat_2.ulitilies.HelperFunction;

public class UserGroupMembersAdapter extends RecyclerView.Adapter<UserGroupMembersAdapter.UserGroupMembersViewHolder>{

    public static List<User> users;
    public static UserCreateGroupListener userCreateGroupListener;

    public UserGroupMembersAdapter(List<User> users, UserCreateGroupListener userCreateGroupListener) {
        this.users = users;
        this.userCreateGroupListener = userCreateGroupListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserGroupMembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserCreateGroupBinding binding = ItemContainerUserCreateGroupBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserGroupMembersViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserGroupMembersViewHolder holder, int position) {
        holder.setData(users.get(position));


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserGroupMembersViewHolder extends RecyclerView.ViewHolder {


        ItemContainerUserCreateGroupBinding binding;

        UserGroupMembersViewHolder(ItemContainerUserCreateGroupBinding itemContainerUserCreateGroupBinding) {
            super(itemContainerUserCreateGroupBinding.getRoot());
            binding = itemContainerUserCreateGroupBinding;
        }
        void setData(User user) {
            binding.textName.setText(user.getLastName());
            if (user.getImage() != null) {
                StorageReference imagesRef = FirebaseStorage.getInstance().getReference()
                        .child("user")
                        .child("avatar")
                        .child(user.getImage());
                imagesRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> Picasso.get().load(uri).into(binding.imageProfile))
                        .addOnFailureListener(Throwable::printStackTrace);
            }
            binding.radioBtnChoose.setVisibility(View.GONE);
        }
    }

}
