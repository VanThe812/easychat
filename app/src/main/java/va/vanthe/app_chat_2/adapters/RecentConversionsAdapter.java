package va.vanthe.app_chat_2.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import va.vanthe.app_chat_2.databinding.ItemContainerConversionVerticalBinding;
import va.vanthe.app_chat_2.listeners.ConversionListener;
import va.vanthe.app_chat_2.entity.ChatMessage;

public class RecentConversionsAdapter extends RecyclerView.Adapter<RecentConversionsAdapter.ConversionViewHolder>{

    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;
    public RecentConversionsAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(

                ItemContainerConversionVerticalBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {
        ItemContainerConversionVerticalBinding binding;

        ConversionViewHolder(ItemContainerConversionVerticalBinding itemContainerRecentConversionBinding) {
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;
        }
        void setData(ChatMessage chatMessage) {
//            binding.imageProfile.setImageBitmap(getConverdionImage(chatMessage.conversionImage));
//            binding.textName.setText(chatMessage.receiverNickname);
//            binding.textRecentMessage.setText(chatMessage.message);
//            binding.getRoot().setOnClickListener(v -> {
//                User user= new User();
//                user.id = chatMessage.conversionId;
//                user.name = chatMessage.conversionName;
//                user.image = chatMessage.conversionImage;
//                user.conversationId = chatMessage.conversationId;
//                user.receiverNickname = chatMessage.receiverNickname;
//                conversionListener.onConversionCLicked(user);
//            });
        }
    }

    private Bitmap getConverdionImage(String encededImage) {
        byte[] bytes = Base64.decode(encededImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
