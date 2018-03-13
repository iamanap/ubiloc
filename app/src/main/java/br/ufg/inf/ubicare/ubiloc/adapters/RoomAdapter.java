package br.ufg.inf.ubicare.ubiloc.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import br.ufg.inf.ubicare.ubiloc.R;
import br.ufg.inf.ubicare.ubiloc.domain.Room;

public class RoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Room> mItems;
    //    private Map<String,String> mItems;
    private int TYPE_FOOTER = 0;
    private int TYPE_ITEM = 1;

    public RoomAdapter(List<Room> items) {
        mItems = items;
    }

    public void setValues(List<Room> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.setup_room_footer, parent, false);
            return new RoomAdapter.ViewHolderFooter(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.setup_room_item, parent, false);
            return new RoomAdapter.ViewHolderItem(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderItem) {
            ((ViewHolderItem)holder).index.setText("Cômodo " + (position + 1));
            ((ViewHolderItem)holder).name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() != 0) {
                        mItems.get(position).setName(charSequence.toString());

                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });
            ((ViewHolderItem)holder).width.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() != 0) {
                        mItems.get(position).setWidth(Float.valueOf(charSequence.toString()));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) { }
            });
            ((ViewHolderItem)holder).height.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() != 0) {
                        mItems.get(position).setHeight(Float.valueOf(charSequence.toString()));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

        } else {
            ((ViewHolderFooter)holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItems.add(new Room("", 0, 0));
                    RoomAdapter.super.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mItems.size()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size() + 1;
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {
        TextView index;
        EditText name;
        EditText width;
        EditText height;

        public ViewHolderItem(View view) {
            super(view);
            index = (TextView) view.findViewById(R.id.room_index);
            name = (EditText) view.findViewById(R.id.room_name);
            width = (EditText) view.findViewById(R.id.room_width);
            height = (EditText) view.findViewById(R.id.room_height);
        }

    }

    public class ViewHolderFooter extends RecyclerView.ViewHolder {
        View view;

        public ViewHolderFooter(View view) {
            super(view);
            this.view = view;
        }

    }
}
