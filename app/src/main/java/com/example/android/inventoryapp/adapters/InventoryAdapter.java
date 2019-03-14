package com.example.android.inventoryapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.InventoryContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryAdapterViewHolder>
{
    private static final String TAG = InventoryAdapter.class.getSimpleName();

    private Context context;
    private Cursor cursor;



    public InventoryAdapter(Context context)
    {

        this.context = context;
    }

    public class InventoryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        @BindView(R.id.name)
        TextView nameTextView ;
        @BindView(R.id.quantity)
        TextView quantityTextView;
        @BindView(R.id.price)
        TextView priceTextView;
        @BindView(R.id.list_item_image)
        ImageView imageView;

        public InventoryAdapterViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            cursor.moveToPosition(getAdapterPosition());

            String nameTextView = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_SHOES_NAME));
            String lineId = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_LINES_ID));


        }
    }


    @Override
    public InventoryAdapter.InventoryAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new InventoryAdapter.InventoryAdapterViewHolder(view);
    }

    public Cursor swapCursor(Cursor c)
    {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (cursor == c)
        {
            return null; // bc nothing has changed
        }

        Cursor temp = cursor;
        this.cursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null)
        {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    @Override
    public int getItemCount()
    {
        if (null == cursor)
            return 0;

        return cursor.getCount();
    }



}
