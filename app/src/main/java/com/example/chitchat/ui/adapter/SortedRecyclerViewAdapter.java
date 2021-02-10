package com.example.chitchat.ui.adapter;


import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

public abstract class SortedRecyclerViewAdapter<Item, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private SortedList<Item> data = new SortedList<>(getItemClass(), new SortedList.Callback<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            return SortedRecyclerViewAdapter.this.compare(o1, o2);
        }

        @Override
        public void onChanged(int position, int count) {
            SortedRecyclerViewAdapter.this.onChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(Item oldItem, Item newItem) {
            return SortedRecyclerViewAdapter.this.areContentsTheSame(oldItem, newItem);
        }

        @Override
        public boolean areItemsTheSame(Item item1, Item item2) {
            return SortedRecyclerViewAdapter.this.areItemsTheSame(item1, item2);
        }

        @Override
        public void onInserted(int position, int count) {
            SortedRecyclerViewAdapter.this.onInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            SortedRecyclerViewAdapter.this.onRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            SortedRecyclerViewAdapter.this.onMoved(fromPosition, toPosition);
        }
    });

    protected abstract Class<Item> getItemClass();

    protected abstract int compare(Item item1, Item item2);

    protected void onChanged(int position, int count) {

    }

    protected boolean areContentsTheSame(Item oldItem, Item newItem) {
        return oldItem.equals(newItem);
    }

    protected boolean areItemsTheSame(Item item1, Item item2) {
        return item1.equals(item2);
    }

    protected void onInserted(int position, int count) {

    }

    protected void onRemoved(int position, int count) {

    }

    protected void onMoved(int fromPosition, int toPosition) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public SortedList<Item> getData() {
        return data;
    }

    public int findPosition(Item item) {
        if (data == null) {
            return -1;
        }

        int size = data.size() - 1;
        for (int i = size; i >= 0; i--) {
            if (data.get(i).equals(item)) {
                return i;
            }
        }

        return -1;
    }

    public void clear() {
        data.clear();
    }
}
