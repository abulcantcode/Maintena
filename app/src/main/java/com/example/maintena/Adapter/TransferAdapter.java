package com.example.maintena.Adapter;

import static com.example.maintena.Utility.getRecordCollectionReference;
import static com.example.maintena.Utility.getVehicleCollectionReference;
import static com.example.maintena.Utility.showToast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.maintena.Model.Vehicle;
import com.example.maintena.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

public class TransferAdapter  extends FirestoreRecyclerAdapter<Vehicle, TransferAdapter.TransferViewHolder> {
    Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TransferAdapter(@NonNull FirestoreRecyclerOptions<Vehicle> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull TransferViewHolder holder, int position, @NonNull Vehicle vehicle) {
        String docId = this.getSnapshots().getSnapshot(position).getId();

        holder.textCarName.setText(vehicle.getName());
        holder.textNumPlate.setText(vehicle.getNumberPlate());
        holder.textMake.setText(vehicle.getMake());
        holder.textColour.setText(vehicle.getColour());

        // Load the image using Glide
        if (vehicle.getImgUrl() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(vehicle.getImgUrl())
                    .into(holder.imgCar);
        } else {
            // If no image is available, set a placeholder image
            holder.imgCar.setImageResource(R.drawable.placeholder_img);
        }


        holder.btnReject.setOnClickListener(view -> {
            vehicle.setOwner(vehicle.getPreviousOwner());
            vehicle.setPending(false);
            getVehicleCollectionReference().document(docId).set(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    showToast(context, "Vehicle Rejected");
                }
            });
        });

        holder.bntAccept.setOnClickListener(view -> {
            Query query = getRecordCollectionReference().whereEqualTo("vehicleID", docId).whereEqualTo("owner", vehicle.getPreviousOwner());

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // Batch write from firebase
                        WriteBatch batch = FirebaseFirestore.getInstance().batch();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DocumentReference documentRef = document.getReference();
                            batch.update(documentRef, "owner", vehicle.getOwner());
                        }

                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    vehicle.setPending(false);
                                    getVehicleCollectionReference().document(docId).set(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            showToast(context, "Vehicle records transferred");
                                        }
                                    });
                                } else {
                                    showToast(context, "failed to transfer vehicle records");
                                }
                            }
                        });
                    } else {
                        showToast(context, "failed to transfer vehicle records");
                    }
                }
            });
        });
    }

    @NonNull
    @Override
    public TransferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_transfer_item, parent, false);
        return new TransferViewHolder(view);
    }

    public class TransferViewHolder extends RecyclerView.ViewHolder {

        Button btnReject, bntAccept;

        ImageView imgCar;

        TextView textColour, textMake, textNumPlate, textCarName;
        public TransferViewHolder(@NonNull View itemView) {
            super(itemView);

            textCarName = itemView.findViewById(R.id.textCarName);
            textNumPlate = itemView.findViewById(R.id.textNumPlate);
            textMake = itemView.findViewById(R.id.textMake);
            textColour = itemView.findViewById(R.id.textColour);
            imgCar = itemView.findViewById(R.id.imgCar);
            bntAccept = itemView.findViewById(R.id.bntAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
