package va.vanthe.app_chat_2.thiandroidNC.hoten_lop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import va.vanthe.app_chat_2.databinding.ItemContainerUserBinding;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.listeners.UserListener;

public class TaiKhoanAdapter extends RecyclerView.Adapter<TaiKhoanAdapter.UsersViewholder>{

    public static List<TaiKhoan> taiKhoans;

    public TaiKhoanAdapter(List<TaiKhoan> taiKhoans) {
        this.taiKhoans = taiKhoans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UsersViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UsersViewholder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewholder holder, int position) {
        holder.setUserData(taiKhoans.get(position));


    }

    @Override
    public int getItemCount() {
        return taiKhoans.size();
    }

    class UsersViewholder extends RecyclerView.ViewHolder {


        ItemContainerUserBinding binding;

        UsersViewholder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }
        void setUserData(TaiKhoan taiKhoan) {
            binding.textName.setText(taiKhoan.getTen());
//            binding.imageProfile.setImageBitmap(getUserImage(user.getImage()));
            StorageReference imagesRef2 = FirebaseStorage.getInstance().getReference()
                    .child("thi")
                    .child("avatar")
                    .child(taiKhoan.getImage());
            imagesRef2.getDownloadUrl()
                    .addOnSuccessListener(uri -> Picasso.get().load(uri).into(binding.imageProfile))
                    .addOnFailureListener(Throwable::printStackTrace);
        }
    }


}
