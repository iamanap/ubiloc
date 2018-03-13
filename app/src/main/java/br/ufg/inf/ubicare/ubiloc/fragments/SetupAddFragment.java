package br.ufg.inf.ubicare.ubiloc.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.ufg.inf.ubicare.ubiloc.R;
import br.ufg.inf.ubicare.ubiloc.adapters.RoomAdapter;
import br.ufg.inf.ubicare.ubiloc.domain.Room;

public class SetupAddFragment extends Fragment {

    private ViewHolder mHolder;
    private List<Room> mRooms;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setup_add, container, false);
        initViews(root);
        return root;
    }

    private void initViews(View root) {
        mRooms = new ArrayList<>();
        mRooms.add(new Room("", 0, 0));

        mHolder = new ViewHolder();
        mHolder.list = (RecyclerView) root.findViewById(R.id.list);
        mHolder.list.setItemViewCacheSize(100);
        mHolder.list.setHasFixedSize(true);
        mHolder.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHolder.list.setAdapter(new RoomAdapter(mRooms));
    }

    public boolean onDoneAdd() {
        String error = "";
        for (int i = 0; i < mRooms.size(); i++) {
            Room room = mRooms.get(i);
            if (room.getName() == "") {
                error += "- Cômodo " + i + " não possui nome\n";
            }
            if (room.getWidth() == 0.0) {
                error += "- Cômodo " + i + " não possui largura\n";
            }
            if (room.getHeight() == 0.0) {
                error += "- Cômodo " + i + " não possui altura\n";
            }
        }
        if (error.length() == 0) {
            for (Room room: mRooms) {
                room.save();
            }
            return true;
        } else {
            showAlert("Aviso", error);
            return false;
        }
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class ViewHolder {
        RecyclerView list;
    }
}
