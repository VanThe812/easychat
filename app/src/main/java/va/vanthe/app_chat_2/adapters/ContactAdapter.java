package va.vanthe.app_chat_2.adapters;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import va.vanthe.app_chat_2.databinding.ItemContainerUserSearchBinding;
import va.vanthe.app_chat_2.entity.Contact;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{

    private final List<Contact> mContacts;
//    private IClickItemUserSearch iClickItemUserSearch;
//
//    public interface IClickItemUserSearch {
//        void clickUser(User user, boolean isNewChat);
//    }


    @SuppressLint("NotifyDataSetChanged")
    public ContactAdapter(List<Contact> mContacts) {
        this.mContacts = mContacts;
        notifyDataSetChanged();
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
        holder.setData(mContacts.get(position));


    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {


        ItemContainerUserSearchBinding binding;

        ContactViewHolder(ItemContainerUserSearchBinding itemContainerUserSearchBinding) {
            super(itemContainerUserSearchBinding.getRoot());
            binding = itemContainerUserSearchBinding;
        }
        void setData(Contact contact) {
            binding.textName.setText(contact.getName());
//            binding.imageProfile.setImageBitmap(getUserImage(user.getImage()));
            binding.textPhoneNumber.setText(contact.getPhone());
            binding.getRoot().setOnClickListener(v -> {

            });
        }
    }

}
