package va.vanthe.app_chat_2.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.database.UserDatabase;
import va.vanthe.app_chat_2.databinding.ItemChatNotificationBinding;
import va.vanthe.app_chat_2.databinding.ItemContainerReceivedMessageBinding;
import va.vanthe.app_chat_2.databinding.ItemContainerSentMessageBinding;
import va.vanthe.app_chat_2.entity.ChatMessage;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.HelperFunction;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<ChatMessage> chatMessages;

    private final String senderId;
    private final int styleChat;

    public static final int VIEW_TYPE_SENT = 1; // bản thân
    public static final int VIEW_TYPE_RECEIVED = 2; // người kia

    public static OnItemClickListener onItemClickListener;

    public ChatAdapter(List<ChatMessage> chatMessages,String senderId, int styleChat, OnItemClickListener onItemClickListener) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
        this.styleChat = styleChat;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Uri uri);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_NOTIFICATION) {
            return new NotificationViewHolder(ItemChatNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else
        if(viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }else if(viewType == VIEW_TYPE_RECEIVED){
            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position), styleChat);
        }else if(getItemViewType(position) == VIEW_TYPE_RECEIVED){
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position), styleChat);
        } else if(getItemViewType(position) == Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_NOTIFICATION) {
            ((NotificationViewHolder) holder).setData(chatMessages.get(position));
        }
        //kiểm tra khoảng thời gian từ tin nhắn trước tới tin nhắn này là bao nhiêu,
        //nếu lớn hơn 10p sẽ hiện ra màn hình thời gian chính xác tin nhắn này dc nhắn: VD 14:00
        if (position > 0) {

            if (!isCalculateTime(chatMessages.get(position).getDataTime(), chatMessages.get(position-1).getDataTime(), 10)) {
                if(getItemViewType(position) == VIEW_TYPE_SENT) {
                    ((SentMessageViewHolder) holder).setTimeVisible(chatMessages.get(position).getDataTime());
                } else if(getItemViewType(position) == VIEW_TYPE_RECEIVED){
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
        if (chatMessages.get(position).getStyleMessage() == Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_NOTIFICATION) {
            return chatMessages.get(position).getStyleMessage();
        }
        if (chatMessages.get(position).getStyleMessage() == Constants.KEY_CHAT_MESSAGE_STYLE_NEW_CHAT) {
            return chatMessages.get(position).getStyleMessage();
        }
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
        void setData(ChatMessage chatMessage, int styleChat) {


            binding.textTime.setText(getTimeAgo(chatMessage.getDataTime()));


            if (chatMessage.getStyleMessage() == Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_TEXT) {
                binding.textMessage.setVisibility(View.VISIBLE);
                binding.relativeLayoutImage.setVisibility(View.GONE);

                binding.textMessage.setText(chatMessage.getMessage().trim());
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.textMessage.getLayoutParams();
                layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.matchConstraintMaxWidth = 550;
                binding.textMessage.setLayoutParams(layoutParams);
                binding.textMessage.setOnClickListener(view -> {
                    if (binding.textTime.getVisibility() == View.VISIBLE) {
                        binding.textTime.setVisibility(View.GONE);
                    } else if (binding.textTime.getVisibility() == View.GONE) {
                        binding.textTime.setVisibility(View.VISIBLE);
                    }
                });
                if (HelperFunction.isEmoji(chatMessage.getMessage().trim())) {
                    binding.textMessage.setBackgroundResource(R.drawable.bg_trans);
                    binding.textMessage.setTextSize(30);
                } else {
                    binding.textMessage.setBackgroundResource(R.drawable.bg_sent_message);
                    binding.textMessage.setTextSize(18);
                }
            }

            else if (chatMessage.getStyleMessage() == Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_IMAGE) {
                binding.relativeLayoutImage.setVisibility(View.VISIBLE);
                binding.textMessage.setVisibility(View.GONE);

                StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("images")
                    .child("conversation")
                    .child(chatMessage.getConversationId())
                    .child(chatMessage.getMessage());

                // Tải ảnh xuống và gán vào ImageView
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Sử dụng thư viện Picasso để tải ảnh từ URL về và gán vào ImageView
                        Picasso.get()
                                .load(uri)
                                .into(binding.imageMessage);
                        binding.imageMessage.setOnClickListener(view -> {
                            onItemClickListener.onItemClick(uri);
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi có lỗi xảy ra
                        Log.e(ChatAdapter.class.toString(), "Error getting image from Firebase Storage.", e);
                    }
                });



            }

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

        void setData(ChatMessage chatMessage, int styleChat) {
            User user = UserDatabase.getInstance(itemView.getContext()).userDAO().getUser(chatMessage.getSenderId());
            if (user.isEmpty()) {
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                database.collection(Constants.KEY_USER)
                        .document(chatMessage.getSenderId())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User sender = documentSnapshot.toObject(User.class);
                                sender.setId(documentSnapshot.getId());
                                if (user.getId().isEmpty()) {
                                    UserDatabase.getInstance(itemView.getContext()).userDAO().insertUser(sender);
                                } else {
                                    UserDatabase.getInstance(itemView.getContext()).userDAO().updateUser(sender);
                                }


                                binding.textName.setText(sender.getLastName());
                                if (sender.getImage() != null) {
                                    StorageReference imagesRef = FirebaseStorage.getInstance().getReference()
                                            .child("user")
                                            .child("avatar")
                                            .child(sender.getImage());
                                    imagesRef.getDownloadUrl()
                                            .addOnSuccessListener(uri -> Picasso.get().load(uri).resize(50, 50).into(binding.imageProfile))
                                            .addOnFailureListener(Throwable::printStackTrace);
                                }
                            }
                        });
            }

            if (styleChat == Constants.KEY_TYPE_CHAT_GROUP) {
                binding.textName.setText(user.getLastName());
                binding.textName.setVisibility(View.VISIBLE);
            }
            binding.textTime.setText(getTimeAgo(chatMessage.getDataTime()));

            if (chatMessage.getStyleMessage() == Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_TEXT) {
                binding.textMessage.setVisibility(View.VISIBLE);
                binding.relativeLayoutImage.setVisibility(View.GONE);

                binding.textMessage.setText(chatMessage.getMessage().trim());
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.textMessage.getLayoutParams();
                layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.matchConstraintMaxWidth = 550;
                binding.textMessage.setLayoutParams(layoutParams);
                binding.textMessage.setOnClickListener(view -> {
                    if (binding.textTime.getVisibility() == View.VISIBLE) {
                        binding.textTime.setVisibility(View.GONE);
                    } else if (binding.textTime.getVisibility() == View.GONE) {
                        binding.textTime.setVisibility(View.VISIBLE);
                    }
                });
                if (HelperFunction.isEmoji(chatMessage.getMessage().trim())) {
                    binding.textMessage.setBackgroundResource(R.drawable.bg_trans);
                    binding.textMessage.setTextSize(30);
                } else {
                    binding.textMessage.setBackgroundResource(R.drawable.bg_received_message);
                    binding.textMessage.setTextSize(18);
                }
            }
            else if (chatMessage.getStyleMessage() == Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_IMAGE) {
                binding.relativeLayoutImage.setVisibility(View.VISIBLE);
                binding.textMessage.setVisibility(View.GONE);
                StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                        .child("images")
                        .child("conversation")
                        .child(chatMessage.getConversationId())
                        .child(chatMessage.getMessage());

                // Tải ảnh xuống và gán vào ImageView
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Sử dụng thư viện Picasso để tải ảnh từ URL về và gán vào ImageView
                        Picasso.get()
                                .load(uri)
                                .into(binding.imageMessage);
                        binding.imageMessage.setOnClickListener(view -> {
                            onItemClickListener.onItemClick(uri);
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi có lỗi xảy ra
                        Log.e(ChatAdapter.class.toString(), "Error getting image from Firebase Storage.", e);
                    }
                });


            }
            try {
                binding.imageProfile.setImageBitmap(HelperFunction.getBitmapFromEncodedImageString(user.getImage()));
            } catch (Exception e) {}

           


        }
        void setTimeVisible(Date datetime) {
            binding.textTime.setText(getTimeAgo(datetime));
            binding.textTime.setVisibility(View.VISIBLE);
        }
    }

    public static String getTimeAgo(Date dateOld) {
        Date now = new Date();

        long seconds = (now.getTime() - dateOld.getTime()) / 1000;

        long minutes = seconds / 60;

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

    static class NotificationViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatNotificationBinding binding;

        NotificationViewHolder(ItemChatNotificationBinding itemChatNotificationBinding) {
            super(itemChatNotificationBinding.getRoot());
            binding = itemChatNotificationBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textViewMessageNotification.setText(chatMessage.getMessage());
        }

    }
}
