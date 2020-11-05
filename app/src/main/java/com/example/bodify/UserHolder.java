package com.example.bodify;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserHolder extends RecyclerView.ViewHolder {
    public final ImageView image;
    public final TextView userName,email;
    private ArrayList<String> test = new ArrayList<>();
    public UserHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.cardViewProfilePicture);
        userName = itemView.findViewById(R.id.userNameCardView);
        email = itemView.findViewById(R.id.emailCard);
    }

    public void setUserName(String un) {
        userName.setText(un);
    }

    public void setEmailAddress(String ea) {
        email.setText(ea);
    }

    public void setImage(String i) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child(i).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(bitmap);
            }
        });
    }
}
