package com.example.maintena.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carpromaintenance.JobViewActivity;
import com.example.carpromaintenance.Model.JobDetails;
import com.example.carpromaintenance.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

// This class is an adapter for the RecyclerView in the MaintenanceActivity and is responsible for displaying a list of jobs with their details.
public class JobAdapter extends FirestoreRecyclerAdapter<JobDetails, JobAdapter.JobViewHolder> {

    Context context;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public JobAdapter(@NonNull FirestoreRecyclerOptions<JobDetails> options, Context context) {
        super(options);
        this.context = context;
    }

    // This method is responsible for populating each card displayed in MaintenanceActivity's recyclerView
    @Override
    protected void onBindViewHolder(@NonNull JobViewHolder holder, int position, @NonNull JobDetails jobDetails) {
        holder.textTitle.setText(jobDetails.getTitle());
        holder.textDate.setText(jobDetails.getDate());
        holder.textDescription.setText(jobDetails.getDescription());

        holder.btnPending.setVisibility(View.GONE);
        holder.btnVerified.setVisibility(View.GONE);
        holder.btnRejected.setVisibility(View.GONE);

        // This section determines which icon shows based on the values inside jobDetails
        if (jobDetails.getVerified()){
            holder.btnVerified.setVisibility(View.VISIBLE);
        } else {
            if (jobDetails.getComment()!=null && !jobDetails.getComment().isEmpty()) {
                holder.btnRejected.setVisibility(View.VISIBLE);
            } else {
                holder.btnPending.setVisibility(View.VISIBLE);
            }
        }

        // Load the image using Glide
        if (jobDetails.getImgUrl() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(jobDetails.getImgUrl())
                    .into(holder.imgReceipt);
        } else {
            // If no image is available, set a placeholder image
            holder.imgReceipt.setImageResource(R.drawable.placeholder_img);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, JobViewActivity.class);
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);

        });
    }


    // This method is called when the RecyclerView needs a new ViewHolder of the given type to represent an item.
    // In this case, it inflates the job_list_item.xml layout and returns a new JobViewHolder object that contains references to the views inside the layout.
    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_job_item, parent, false);
        return new JobViewHolder(view);
    }

    class JobViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle, textDate, textDescription;
        ImageView imgReceipt;
        ImageButton btnVerified, btnRejected, btnPending;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.textTitle);
            textDate = itemView.findViewById(R.id.textDate);
            textDescription = itemView.findViewById(R.id.textDescription);
            imgReceipt = itemView.findViewById(R.id.imgReceipt);
            btnVerified = itemView.findViewById(R.id.btnVerified);
            btnRejected = itemView.findViewById(R.id.btnRejected);
            btnPending = itemView.findViewById(R.id.btnPending);

        }
    }
}
