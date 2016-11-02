package elcg.dina.com.instagramapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FinalActivity extends AppCompatActivity {

    EditText editText;
    Button post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        editText = (EditText)findViewById(R.id.caption);
        post = (Button)findViewById(R.id.postBtn);

        final Intent i = getIntent();
        final String imgPath = i.getStringExtra("imagePath");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                ImageItem imageItem = new ImageItem();
                imageItem.setImageURL(imgPath);
                imageItem.setImageCaption(editText.getText().toString());
                imageItem.setImageIsLiked(false);

                db.addImage(imageItem);

            }
        });




    }
}
