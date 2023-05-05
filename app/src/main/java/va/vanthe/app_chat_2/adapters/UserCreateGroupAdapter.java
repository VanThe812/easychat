package va.vanthe.app_chat_2.adapters;

import android.view.LayoutInflater;
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

public class UserCreateGroupAdapter extends RecyclerView.Adapter<UserCreateGroupAdapter.UserCreateGroupViewHolder>{

    public List<User> users;
    public UserCreateGroupListener userCreateGroupListener;

    public UserCreateGroupAdapter(List<User> users, UserCreateGroupListener userCreateGroupListener) {
        this.users = users;
        this.userCreateGroupListener = userCreateGroupListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserCreateGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserCreateGroupBinding binding = ItemContainerUserCreateGroupBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserCreateGroupViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCreateGroupViewHolder holder, int position) {
        holder.setData(users.get(position), userCreateGroupListener);


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserCreateGroupViewHolder extends RecyclerView.ViewHolder {


        ItemContainerUserCreateGroupBinding binding;

        UserCreateGroupViewHolder(ItemContainerUserCreateGroupBinding itemContainerUserCreateGroupBinding) {
            super(itemContainerUserCreateGroupBinding.getRoot());
            binding = itemContainerUserCreateGroupBinding;
        }
        void setData(User user, UserCreateGroupListener userCreateGroupListener) {
            binding.textName.setText(user.getLastName());
            Picasso.get().cancelRequest(binding.imageProfile);
            if (user.getImage() != null) {
                StorageReference imagesRef = FirebaseStorage.getInstance().getReference()
                        .child("user")
                        .child("avatar")
                        .child(user.getImage());
                imagesRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> Picasso.get().load(uri).into(binding.imageProfile))
                        .addOnFailureListener(Throwable::printStackTrace);
            }
            binding.getRoot().setOnClickListener(v -> {
                binding.radioBtnChoose.setChecked(!binding.radioBtnChoose.isChecked());
                userCreateGroupListener.onUserClicked(user, binding.radioBtnChoose.isChecked());
            });
        }
    }

}
