package va.vanthe.app_chat_2.adapters;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import va.vanthe.app_chat_2.databinding.ItemContainerUserSearchBinding;
import va.vanthe.app_chat_2.entity.Contact;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.HelperFunction;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{

    private final List<User> mUsers;
    private IClickItemUser iClickItemUser;

    public interface IClickItemUser {
        void clickUser(User user);
    }


    @SuppressLint("NotifyDataSetChanged")
    public ContactAdapter(List<User> mUsers, IClickItemUser iClickItemUser) {
        this.mUsers = mUsers;
        this.iClickItemUser = iClickItemUser;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserSearchBinding itemContainerUserSearchBinding = ItemContainerUserSearchBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ContactViewHolder(itemContainerUserSearchBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.setData(mUsers.get(position), iClickItemUser);


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {


        ItemContainerUserSearchBinding binding;

        ContactViewHolder(ItemContainerUserSearchBinding itemContainerUserSearchBinding) {
            super(itemContainerUserSearchBinding.getRoot());
            binding = itemContainerUserSearchBinding;
        }
        void setData(User user, IClickItemUser iClickItemUser) {
            binding.textName.setText(user.getLastName());
            if (user.getImage() != null) {
                StorageReference imagesRef = FirebaseStorage.getInstance().getReference()
                        .child("user")
                        .child("avatar")
                        .child(user.getImage());
                imagesRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> Picasso.get().load(uri).resize(100, 100).into(binding.imageProfile))
                        .addOnFailureListener(Throwable::printStackTrace);
            }
            binding.textPhoneNumber.setText(user.getPhoneNumber());
            binding.getRoot().setOnClickListener(v -> {
                iClickItemUser.clickUser(user);
            });
        }
    }

}
