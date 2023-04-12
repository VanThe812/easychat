package va.vanthe.app_chat_2.adapters;

import static va.vanthe.app_chat_2.adapters.UsersAdapter.users;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import va.vanthe.app_chat_2.databinding.ItemContainerUserAddfriendBinding;
import va.vanthe.app_chat_2.listeners.UserListener;
import va.vanthe.app_chat_2.models.User;

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.AddFriendViewHolder>{

    public static List<User> users;
    public static UserListener userListener;

    public AddFriendAdapter(List<User> users, UserListener userListener){
        this.users = users;
        this.userListener = userListener;
    }
    @NonNull
    @Override
    public AddFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserAddfriendBinding itemContainerUserAddfriendBinding = ItemContainerUserAddfriendBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AddFriendViewHolder(itemContainerUserAddfriendBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFriendViewHolder holder, int position) {

        holder.setData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class AddFriendViewHolder extends RecyclerView.ViewHolder{

        ItemContainerUserAddfriendBinding binding;

        AddFriendViewHolder(ItemContainerUserAddfriendBinding itemContainerUserAddfriendBinding){
            super(itemContainerUserAddfriendBinding.getRoot());
            binding = itemContainerUserAddfriendBinding;

        }

        void setData(User user){

            binding.textName.setText(user.name);

            binding.imageProfile.setImageBitmap(getUserImage(user.image));
        }
    }
    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
