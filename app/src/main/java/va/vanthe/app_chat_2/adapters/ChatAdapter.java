package va.vanthe.app_chat_2.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.database.UserDatabase;
import va.vanthe.app_chat_2.databinding.ItemContainerReceivedMessageBinding;
import va.vanthe.app_chat_2.databinding.ItemContainerSentMessageBinding;
import va.vanthe.app_chat_2.entity.ChatMessage;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.ulitilies.Constants;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<ChatMessage> chatMessages;

    private final String senderId;
    private final int styleChat;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatMessages,String senderId, int styleChat) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
        this.styleChat = styleChat;
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
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position), styleChat);


        }else {

            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position), styleChat);

        }
        //kiểm tra khoảng thời gian từ tin nhắn trước tới tin nhắn này là bao nhiêu,
        //nếu lớn hơn 10p sẽ hiện ra màn hình thời gian chính xác tin nhắn này dc nhắn: VD 14:00
        if (position > 0) {

            if (!isCalculateTime(chatMessages.get(position).getDataTime(), chatMessages.get(position-1).getDataTime(), 10)) {
                if(getItemViewType(position) == VIEW_TYPE_SENT) {
                    ((SentMessageViewHolder) holder).setTimeVisible(chatMessages.get(position).getDataTime());
                } else {
                    ((ReceivedMessageViewHolder) holder).setTimeVisible(chatMessages.get(position).getDataTime());
                }

            }
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).getSenderId().equals(senderId)) {
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
        void setData(ChatMessage chatMessage, int textStatus) {
            binding.textMessage.setText(chatMessage.getMessage());
//            binding.textDateTime.setText(chatMessage.datatime);
            if (isEmoji(chatMessage.getMessage().trim())) {
                binding.textMessage.setBackgroundResource(R.drawable.bg_trans);
                binding.textMessage.setTextSize(30);
            }
            binding.textTime.setText(getTimeAgo(chatMessage.getDataTime()));
            binding.textStatus.setText("Đã xem");
            binding.getRoot().setOnClickListener(view -> {
                if (binding.textTime.getVisibility() == View.VISIBLE) {
                    binding.textTime.setVisibility(View.GONE);
                    binding.textStatus.setVisibility(View.GONE);
                } else if (binding.textTime.getVisibility() == View.GONE) {
                    binding.textTime.setVisibility(View.VISIBLE);
                    binding.textStatus.setVisibility(View.VISIBLE);
                }
            });
        }
        void setTimeVisible(Date datetime) {
            binding.textTime.setText(getTimeAgo(datetime));
            binding.textTime.setVisibility(View.VISIBLE);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage, int styleChat)  {
            User user = UserDatabase.getInstance(itemView.getContext()).userDAO().getUser(chatMessage.getSenderId());
            if (user != null) {
                binding.textMessage.setText(chatMessage.getMessage().trim());

                if (isEmoji(chatMessage.getMessage().trim())) {
                    binding.textMessage.setBackgroundResource(R.drawable.bg_trans);
                    binding.textMessage.setTextSize(30);
                }
                if (styleChat == Constants.KEY_TYPE_CHAT_GROUP) {
                    binding.textName.setText(user.getLastName());
                }
                binding.textTime.setText(getTimeAgo(chatMessage.getDataTime()));
                binding.textStatus.setText("Đã xem");
                binding.getRoot().setOnClickListener(view -> {
                    if (binding.textTime.getVisibility() == View.VISIBLE) {
                        binding.textTime.setVisibility(View.GONE);
                        binding.textStatus.setVisibility(View.GONE);
                    } else if (binding.textTime.getVisibility() == View.GONE) {
                        binding.textTime.setVisibility(View.VISIBLE);
                        binding.textStatus.setVisibility(View.VISIBLE);
                    }
                });

                binding.imageProfile.setImageBitmap(getUserImage(user.getImage()));
            }
            else {
                Toast.makeText(itemView.getContext(), "Có lỗi xảy ra!!!", Toast.LENGTH_SHORT).show();
            }

        }
        void setTimeVisible(Date datetime) {
            binding.textTime.setText(getTimeAgo(datetime));
            binding.textTime.setVisibility(View.VISIBLE);
        }
        private Bitmap getUserImage(String encodedImage) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }
    public static boolean isEmoji(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.isHighSurrogate(str.charAt(i))) {
                if (i < str.length() - 1 && Character.isLowSurrogate(str.charAt(i + 1))) {
                    int codepoint = Character.toCodePoint(str.charAt(i), str.charAt(i + 1));
                    if (codepoint >= 0x1F000 && codepoint <= 0x1F9FF) {
                        return true;
                    }
                }
            } else {
                int type = Character.getType(str.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return true;
                }
            }
        }
        return false;
    }
    public static String getTimeAgo(Date dateOld) {
        Date now = new Date();

        long seconds = (now.getTime() - dateOld.getTime()) / 1000;
//        if (seconds < 60) {
//            return seconds + " giây trước";
//        }
//
        long minutes = seconds / 60;
//        if (minutes < 60) {
//            return minutes + " phút trước";
//        }

        long hours = minutes / 60;
        if (hours < 24 && isSameDay(dateOld, now)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            return dateFormat.format(dateOld);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, dd/MM/yyyy");
        return dateFormat.format(dateOld);
    }
    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date1).equals(dateFormat.format(date2));
    }
    public static boolean isCalculateTime(Date dateNew, Date dateOld, int period) {
        long minutes = ((dateNew.getTime() - dateOld.getTime()) / 1000)/60;
        Log.e(ChatAdapter.class.toString(), minutes + "");
        if (minutes < (long) period) {
            return true;
        }else{
            return false;
        }
    }
}
