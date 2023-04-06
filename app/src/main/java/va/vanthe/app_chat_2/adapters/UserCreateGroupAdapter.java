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
import va.vanthe.app_chat_2.databinding.ItemContainerUserCreateGroupBinding;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.listeners.UserCreateGroupListener;
import va.vanthe.app_chat_2.listeners.UserListener;

public class UserCreateGroupAdapter extends RecyclerView.Adapter<UserCreateGroupAdapter.UserCreateGroupViewHolder>{

    public static List<User> users;
    public static UserCreateGroupListener userCreateGroupListener;

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
        holder.setData(users.get(position));


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserCreateGroupViewHolder extends RecyclerView.ViewHolder {


        ItemContainerUserCreateGroupBinding binding;

        UserCreateGroupViewHolder(ItemContainerUserCreateGroupBinding itemContainerUserCreateGroupBinding) {
            super(itemContainerUserCreateGroupBinding.getRoot());
            binding = itemContainerUserCreateGroupBinding;
        }
        void setData(User user) {
            binding.textName.setText(user.getLastName());
            binding.imageProfile.setImageBitmap(getUserImage(user.getImage()));
            binding.getRoot().setOnClickListener(v -> {
                if (binding.radioBtnChoose.isChecked())
                    binding.radioBtnChoose.setChecked(false);
                else
                    binding.radioBtnChoose.setChecked(true);
                userCreateGroupListener.onUserClicked(user, binding.radioBtnChoose.isChecked());
            });
        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
