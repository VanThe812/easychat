package va.vanthe.app_chat_2.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import va.vanthe.app_chat_2.activities.ChatMessageActivity;
import va.vanthe.app_chat_2.database.GroupMemberDatabase;
import va.vanthe.app_chat_2.database.UserDatabase;
import va.vanthe.app_chat_2.databinding.ItemContainerConversionBinding;
import va.vanthe.app_chat_2.entity.Conversation;
import va.vanthe.app_chat_2.entity.GroupMember;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.listeners.ConversionListener;
import va.vanthe.app_chat_2.entity.ChatMessage;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class ConversionsAdapter extends RecyclerView.Adapter<ConversionsAdapter.ConversionViewHolder>{


    private final List<Conversation> conversationList;
    private final String userId;
    public ConversionsAdapter(List<Conversation> conversationList, String userId) {
        this.conversationList = conversationList;
        this.userId = userId;

    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(

                ItemContainerConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(conversationList.get(position));

    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {
        ItemContainerConversionBinding binding;

        ConversionViewHolder(ItemContainerConversionBinding itemContainerConversionBinding) {
            super(itemContainerConversionBinding.getRoot());
            binding = itemContainerConversionBinding;



        }
        void setData(@NonNull Conversation conversation) {
            Log.e(ConversionsAdapter.class.toString(), conversation.getStyleChat() + "");
            if (conversation.getStyleChat() == Constants.KEY_TYPE_CHAT_SINGLE) {
                GroupMember groupMember = GroupMemberDatabase.getInstance(itemView.getContext()).groupMemberDAO().getGroupMember(userId, conversation.getId());

                if (groupMember != null) {

                    User user = UserDatabase.getInstance(itemView.getContext()).userDAO().getUser(groupMember.getUserId());

                    binding.textName.setText(user.getFirstName() + " " + user.getLastName());
                    binding.imageProfile.setImageBitmap(getConverdionImage(user.getImage()));
                    binding.textRecentMessage.setText(conversation.getNewMessage());

                    // Lấy ra TextView cần cập nhật thời gian
                    TextView textViewTimeAgo = binding.textTime;
                    // Lấy ra thời điểm cũ cần tính khoảng thời gian
                    Date dateOld = conversation.getMessageTime();
                    // Cập nhật thời gian lần đầu tiên
                    updateTime(textViewTimeAgo, dateOld);
                    // Tạm cmt vì lỗi khi mở activity khác đề vào sẽ dừng app
    //                Timer timer = new Timer();
    //                TimerTask timerTask = new TimerTask() {
    //                    @Override
    //                    public void run() {
    //                        updateTime(textViewTimeAgo, dateOld);
    //                    }
    //                };
    //                timer.schedule(timerTask, 60000, 60000); // Cập nhật sau 1 phút, lặp lại sau mỗi 1 phút

                    binding.getRoot().setOnClickListener(view -> {
                        Intent intent = new Intent(view.getContext(), ChatMessageActivity.class);
                        intent.putExtra(Constants.KEY_USER, user);
                        intent.putExtra(Constants.KEY_CONVERSATION, conversation);
                        intent.putExtra(Constants.KEY_CONVERSATION_ID, conversation.getId());
                        intent.putExtra(Constants.KEY_TYPE, Constants.KEY_TYPE_CHAT_SINGLE);
                        intent.putExtra(Constants.KEY_IS_NEW_CHAT, false);
                        view.getContext().startActivity(intent);
                    });
                }
                else {
                    binding.getRoot().setVisibility(View.GONE);
                }


            } else if (conversation.getStyleChat() == Constants.KEY_TYPE_CHAT_GROUP) {
                binding.textRecentMessage.setText(conversation.getNewMessage());
                binding.textName.setText(conversation.getConversationName());
                if (conversation.getBackgroundImage() != null) {
                    ///
                    binding.imageProfile.setImageBitmap(getConverdionImage(conversation.getBackgroundImage()));
                }
                TextView textViewTimeAgo = binding.textTime;
                Date dateOld = conversation.getMessageTime();
                updateTime(textViewTimeAgo, dateOld);
                binding.getRoot().setOnClickListener(view -> {
                    Intent intent = new Intent(view.getContext(), ChatMessageActivity.class);
                    intent.putExtra(Constants.KEY_CONVERSATION, conversation);
                    intent.putExtra(Constants.KEY_TYPE, Constants.KEY_TYPE_CHAT_GROUP);
                    view.getContext().startActivity(intent);
                });
            }
        }
        private void updateTime(TextView textView, Date dateOld) {
            // Lấy chuỗi thể hiện khoảng thời gian
            String timeAgo = getTimeAgo(dateOld);
            // Cập nhật lại nội dung của TextView
            textView.setText(timeAgo);
        }
    }

    private Bitmap getConverdionImage(String encededImage) {
        byte[] bytes = Base64.decode(encededImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    private String getTimeAgo(Date dateOld) {
        Date now = new Date();

        long seconds = (now.getTime() - dateOld.getTime()) / 1000;
        if (seconds < 60) {
            return seconds + " giây trước";
        }

        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + " phút trước";
        }

        long hours = minutes / 60;
        if (hours < 24 && isSameDay(dateOld, now)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            return dateFormat.format(dateOld);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(dateOld);
    }
    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date1).equals(dateFormat.format(date2));
    }
}
