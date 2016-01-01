package org.esiea.lor_ye.daoverview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

public class DAAdapter extends RecyclerView.Adapter<DAAdapter.ViewHolder> {

    private HashMap<String,String[]> datasetMap;
    private String[] authors;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        private TextView titleView;
        private DAAdapter adapter;

        public ViewHolder(View itemView, DAAdapter adapter) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.author);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, GalleryActivity.class);
            String author = this.adapter.getAuthors()[this.getAdapterPosition()];
            /*Log.d("[YES]", "Dataset: ");
            for (String author_ : this.adapter.getDatasetMap().keySet()) {
                Log.d("[YES]", "author: " + author_);
                for (String image_ : this.adapter.getDatasetMap().get(author_))
                    Log.d("[YES]", image_);
            }*/
            Log.d("[YES]", "author: " + author + "; metadataUrlBase: " + this.adapter.getDatasetMap().get(author).toString());

            intent.putExtra("author", author);
            intent.putExtra("metadataUrlBase", (String[])this.adapter.getDatasetMap().get(author));
            context.startActivity(intent);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DAAdapter(HashMap<String,String[]> datasetMap) {
        this.datasetMap = datasetMap;
        this.authors = datasetMap.keySet().toArray(new String[datasetMap.size()]);
    }

    public HashMap<String,String[]> getDatasetMap() {
        return datasetMap;
    }

    public String[] getAuthors() {
        return authors;
    }

    @Override
    public DAAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        return new ViewHolder (LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false), this);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.titleView.setText(authors[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return authors.length;
    }
}
