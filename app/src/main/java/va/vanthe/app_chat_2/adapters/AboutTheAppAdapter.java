package va.vanthe.app_chat_2.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import va.vanthe.app_chat_2.databinding.LayoutAboutTheAppBinding;
import va.vanthe.app_chat_2.entity.AboutTheApp;

public class AboutTheAppAdapter extends RecyclerView.Adapter<AboutTheAppAdapter.AboutTheAppViewHolder>{

    private List<AboutTheApp> mData;

    public AboutTheAppAdapter(List<AboutTheApp> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public AboutTheAppAdapter.AboutTheAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutAboutTheAppBinding binding = LayoutAboutTheAppBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AboutTheAppAdapter.AboutTheAppViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutTheAppAdapter.AboutTheAppViewHolder holder, int position) {
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class AboutTheAppViewHolder extends RecyclerView.ViewHolder {

        private LayoutAboutTheAppBinding binding;

        public AboutTheAppViewHolder(LayoutAboutTheAppBinding layoutAboutTheAppBinding) {
            super(layoutAboutTheAppBinding.getRoot());
            binding = layoutAboutTheAppBinding;

        }

        public void setData(AboutTheApp data) {
            binding.textViewTitle.setText(data.title);
        }
    }
}
