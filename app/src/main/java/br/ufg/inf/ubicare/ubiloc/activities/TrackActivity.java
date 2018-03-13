package br.ufg.inf.ubicare.ubiloc.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import br.ufg.inf.ubicare.ubiloc.R;
import br.ufg.inf.ubicare.ubiloc.data.PositionPojo;
import br.ufg.inf.ubicare.ubiloc.database.Database;
import br.ufg.inf.ubicare.ubiloc.domain.User;

public class TrackActivity extends AppCompatActivity {

    private ViewHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
    }

    private void initViews() {
        mHolder = new ViewHolder();
        setContentView(R.layout.activity_track);

        mHolder.coordinates = (TextView) findViewById(R.id.coordinates);
        if (User.listAll(User.class).size() > 0) {
            User user = User.listAll(User.class).get(0);
            Database.with(user.getKey()).getPositionFromServer(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    PositionPojo position = dataSnapshot.getValue(PositionPojo.class);
                    mHolder.coordinates.setText("Localização atual é: Cômodo - " + position.getRoomName() + " X: " + String.format("%.2f", position.getX()) + " , Y: " + String.format("%.2f", position.getY()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private class ViewHolder {
        TextView coordinates;
    }
}
