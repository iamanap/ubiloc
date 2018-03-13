package br.ufg.inf.ubicare.ubiloc.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;

import java.util.List;
import java.util.UUID;

import br.ufg.inf.ubicare.ubiloc.R;
import br.ufg.inf.ubicare.ubiloc.domain.Room;
import br.ufg.inf.ubicare.ubiloc.domain.User;
import br.ufg.inf.ubicare.ubiloc.database.Database;
import br.ufg.inf.ubicare.ubiloc.fragments.SetupAddFragment;
import br.ufg.inf.ubicare.ubiloc.fragments.SetupRoomBeaconsFragment;
import br.ufg.inf.ubicare.ubiloc.service.RemoteLocationService;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener, SetupRoomBeaconsFragment.SetupRoomBeaconsFragmentCallback {

    private ViewHolder mHolder;
    private Fragment mCurrentFragment;
    private int roomIndexToSetup;
    private List<Room> mRoomList;
    private Database mDatabase;
    private MenuItem mDoneMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Generate UUID for Firebase account detection
        User user = null;
        if (User.listAll(User.class).size() == 0) {
            user = new User(UUID.randomUUID().toString().replace("-", ""));
            user.save();
        } else {
            user = User.listAll(User.class).get(0);
        }
        mDatabase = Database.with(user.getKey());
        initViews();

    }

    private void initViews() {
        mHolder = new ViewHolder();
        setContentView(R.layout.activity_setup);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("Configuração - Adicionar Cômodos");
        }

        mCurrentFragment = new SetupAddFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.setup_fragment, mCurrentFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setup_activity, menu);
        mDoneMenuItem = menu.findItem(R.id.done);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mCurrentFragment instanceof SetupAddFragment) {
            boolean isDone = ((SetupAddFragment) mCurrentFragment).onDoneAdd();
            if (isDone) {
                mRoomList = Room.listAll(Room.class);
                if (mRoomList != null && mRoomList.size() > 0) {
                    roomIndexToSetup = mRoomList.size() - 1;
                    setupRoom(mRoomList.get(roomIndexToSetup));
                }
            }
        } else if (mCurrentFragment instanceof SetupRoomBeaconsFragment) {
            ((SetupRoomBeaconsFragment) mCurrentFragment).onDoneAdd();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRoom(Room room) {
        mCurrentFragment = new SetupRoomBeaconsFragment();
        Bundle bd = new Bundle();
        bd.putString(getString(R.string.intent_room), room.getName());
        mCurrentFragment.setArguments(bd);
        getSupportFragmentManager().beginTransaction().replace(R.id.setup_fragment, mCurrentFragment).commit();
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("Configuração - " + room.getName());
        }
    }

    @Override
    public void onStartDetecting() {
        mDoneMenuItem.setVisible(false);
    }

    @Override
    public void onDoneDetecting(String roomName) {
        mDoneMenuItem.setVisible(true);
        Toast.makeText(this, "Configuração do cômodo " + roomName + " feita com sucesso!", Toast.LENGTH_LONG).show();
        // Didn't finished setting up all rooms
        if (roomIndexToSetup - 1 > -1) {
            roomIndexToSetup =- 1;
            setupRoom(mRoomList.get(roomIndexToSetup));
        } else {
            mRoomList = Room.listAll(Room.class);
            // Finished. Add rooms to Firebase
            mDatabase.addRoomsToServer(mRoomList);
            // Start remote location service
            Intent intent = new Intent(this, RemoteLocationService.class);
            startService(intent);

            showDoneDialog(mDatabase.getKey());
        }
    }

    private void showDoneDialog(String uuid) {
        ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("chave", uuid);
        myClipboard.setPrimaryClip(myClip);

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Configuração de todos os cômodos completa!")
                .setMessage("Você poderá acompanhar a localização deste dispositivo através do aplicativo de acompanhamento usando a chave:\n\n" + uuid + "\n\nEla já foi copiada para o seu Clipboard, você só precisa colar.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .show();
    }

    @Override
    public void onClick(View view) {

    }

    private class ViewHolder {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
