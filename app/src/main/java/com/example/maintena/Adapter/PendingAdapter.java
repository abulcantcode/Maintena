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
import com.example.carpromaintenance.VerifyActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

// This class is an adapter for the RecyclerView in the PendingActivity and RecordsActivity.
// It is responsible for displaying a list of all jobs / pending jobs with their details for the dealership / garage.
public class PendingAdapter extends FirestoreRecyclerAdapter<JobDetails, PendingAdapter.PendingViewHolder> {
    Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PendingAdapter(@NonNull FirestoreRecyclerOptions<JobDetails> options, Context context) {
        super(options);
        this.context = context;
    }


    // This method is responsible for populating each card displayed in PendingActivity's and RecordsActivity's recyclerView
    @Override
    protected void onBindViewHolder(@NonNull PendingViewHolder holder, int position, @NonNull JobDetails jobDetails) {
        holder.textTitle.setText(jobDetails.getTitle());
        holder.textDate.setText(jobDetails.getDate());
        holder.txtNumPlate.setText(jobDetails.getNumberPlate());

        holder.btnPending.setVisibility(View.GONE);
        holder.btnVerified.setVisibility(View.GONE);
        holder.btnRejected.setVisibility(View.GONE);

        if(jobDetails.getPending()){
            holder.btnPending.setVisibility(View.VISIBLE);
        } else {
            if (jobDetails.getVerified()){
                holder.btnVerified.setVisibility(View.VISIBLE);
            } else {
                holder.btnRejected.setVisibility(View.VISIBLE);
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
            if(jobDetails.getPending()){
                intent = new Intent(context, VerifyActivity.class);
            }
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public PendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_verify, parent, false);
        return new PendingViewHolder(view);
    }

    public class PendingViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, txtNumPlate, textDate;

        ImageView imgReceipt;

        ImageButton btnVerified, btnPending, btnRejected;
        public PendingViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.textTitle);
            txtNumPlate = itemView.findViewById(R.id.txtNumPlate);
            textDate = itemView.findViewById(R.id.textDate);
            imgReceipt = itemView.findViewById(R.id.imgReceipt);
            btnVerified = itemView.findViewById(R.id.btnVerified);
            btnPending = itemView.findViewById(R.id.btnPending);
            btnRejected = itemView.findViewById(R.id.btnRejected);
        }
    }
}
