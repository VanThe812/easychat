package va.vanthe.app_chat_2.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import va.vanthe.app_chat_2.databinding.ItemContainerReceivedMessageBinding;
import va.vanthe.app_chat_2.databinding.ItemContainerSentMessageBinding;
import va.vanthe.app_chat_2.models.ChatMessage;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<ChatMessage> chatMessages;
//    private final Bitmap receiverProfileImage;
//    private final String sendrId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
//        this.sendrId = sendrId;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }else {
            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        }else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).datatime == "1") {
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }
        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
//            binding.textDateTime.setText(chatMessage.datatime);

        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage)  {
            binding.textMessage.setText(chatMessage.message);
//            binding.textDateTime.setText(chatMessage.datatime);
//            if(chatMessage.senderName != "" && chatMessage.senderName != null) {
//                if (chatMessage.senderImage == "" || chatMessage == null) {
//                    binding.imageProfile.setImageBitmap(getUserImage(Constants.IMAGE_AVATAR_DEFAULT));
//                }
//                else {
//                    binding.imageProfile.setImageBitmap(getUserImage(chatMessage.senderImage));
//                }
//                if (chatMessage.senderNickname != "") {
//                    binding.textName.setText(chatMessage.senderNickname);
//                }else {
//                    binding.textName.setText(chatMessage.senderName);
//                }
//            }else {
//                binding.imageProfile.setImageBitmap(receiverProfileImage);
//            }

        }
        private Bitmap getUserImage(String encodedImage) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }

}
