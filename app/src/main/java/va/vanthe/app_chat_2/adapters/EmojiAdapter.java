package va.vanthe.app_chat_2.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.ItemEmojiBinding;
import va.vanthe.app_chat_2.entity.Emoji;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>{

    private List<Emoji> mEmojis;
    private OnItemClickListener onItemClickListener;

    public EmojiAdapter(List<Emoji> mEmojis, OnItemClickListener onItemClickListener) {
        this.mEmojis = mEmojis;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Emoji emoji);
    }
    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEmojiBinding view = ItemEmojiBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new EmojiAdapter.EmojiViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder holder, int position) {
        holder.setData(mEmojis.get(position));
    }

    @Override
    public int getItemCount() {
        return mEmojis.size();
    }

    public class EmojiViewHolder extends RecyclerView.ViewHolder {

        private ItemEmojiBinding binding;

        public EmojiViewHolder(ItemEmojiBinding itemEmojiBinding) {
            super(itemEmojiBinding.getRoot());
            binding = itemEmojiBinding;

        }

        public void setData(Emoji emoji) {
            binding.textViewEmoji.setText(emoji.getEmoji());
            if(emoji.isChecker()) {
                binding.textViewEmoji.setBackgroundResource(R.drawable.bg_emoji);
            }
            binding.getRoot().setOnClickListener(view -> {
                onItemClickListener.onItemClick(emoji);
            });
        }
    }
}
