package va.vanthe.app_chat_2.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.ItemContainerUserChatBinding;
import va.vanthe.app_chat_2.entity.User;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.UserChatViewHolder>{

    private final List<User> users;
    private final Context mContext;

    public UserChatAdapter(List<User> users, Context mContext) {
        this.users = users;
        this.mContext = mContext;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserChatBinding itemContainerUser2Binding = ItemContainerUserChatBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserChatViewHolder(itemContainerUser2Binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChatViewHolder holder, int position) {
        holder.setUserData(users.get(position), position);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserChatViewHolder extends RecyclerView.ViewHolder {
        ItemContainerUserChatBinding binding;
        public UserChatViewHolder(ItemContainerUserChatBinding itemContainerUser2Binding) {
            super(itemContainerUser2Binding.getRoot());
            binding = itemContainerUser2Binding;
        }
        void setUserData(User user, int pos) {
            if(pos != 0) {
                binding.textName.setText(user.getLastName());
                binding.imageProfile.setImageBitmap(getUserImage(user.getImage()));
            }else{
                binding.imageProfile.setBackgroundResource(R.drawable.ic_laughing_meuu);
                binding.textName.setText("Đặt trạng thái tùy chỉnh");
                binding.imageProfile.setOnClickListener(view -> {
                    Toast.makeText(itemView.getContext(), "hiii", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
