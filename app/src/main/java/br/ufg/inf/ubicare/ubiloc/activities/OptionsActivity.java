package br.ufg.inf.ubicare.ubiloc.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.ufg.inf.ubicare.ubiloc.R;
import br.ufg.inf.ubicare.ubiloc.domain.Beacon;
import br.ufg.inf.ubicare.ubiloc.domain.Room;
import br.ufg.inf.ubicare.ubiloc.domain.User;
import br.ufg.inf.ubicare.ubiloc.service.RemoteLocationService;

public class OptionsActivity extends AppCompatActivity {

    private ViewHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
    }

    private void initViews() {
        mHolder = new ViewHolder();
        setContentView(R.layout.activity_options);

        mHolder.remoteKey = (EditText) findViewById(R.id.key);
        if (User.listAll(User.class).size() == 0) {

        } else {
            User user = User.listAll(User.class).get(0);
            mHolder.remoteKey.setText(user.getKey());
        }
        mHolder.configure = (Button) findViewById(R.id.configure);
        mHolder.configure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Room.deleteAll(Room.class);
                Beacon.deleteAll(Beacon.class);
                startActivity(SetupActivity.class);
            }
        });
        mHolder.track = (Button) findViewById(R.id.track);
        mHolder.track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(TrackActivity.class);
            }
        });
    }

    private void startActivity(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    private class ViewHolder {
        EditText remoteKey;
        Button configure;
        Button track;
    }
}
