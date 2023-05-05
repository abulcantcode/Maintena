package com.example.maintena.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.maintena.Model.Vehicle;
import com.example.maintena.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

// This class is an adapter for the RecyclerView in the HomeActivity and is responsible for displaying a list of vehicles with their details.
public class VehicleAdapter extends FirestoreRecyclerAdapter<Vehicle, VehicleAdapter.VehicleViewHolder> {

    Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public VehicleAdapter(@NonNull FirestoreRecyclerOptions<Vehicle> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull VehicleViewHolder holder, int position, @NonNull Vehicle vehicle) {
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

//        holder.itemView.setOnClickListener(view -> {
//            Intent intent = new Intent(context, MaintenanceActivity.class);
//            intent.putExtra("numPlate", vehicle.getNumberPlate());
//            intent.putExtra("name", vehicle.getName());
//            intent.putExtra("make", vehicle.getMake());
//            intent.putExtra("colour", vehicle.getColour());
//            intent.putExtra("fuel", vehicle.getFuelType());
//            String docId = this.getSnapshots().getSnapshot(position).getId();
//            intent.putExtra("docId", docId);
//            context.startActivity(intent);
//        });
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_vehicle_item, parent, false);
        return new VehicleViewHolder(view);
    }

    class VehicleViewHolder extends RecyclerView.ViewHolder{
        TextView textCarName, textNumPlate, textMake, textColour;

        ImageView imgCar;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);

            textCarName = itemView.findViewById(R.id.textCarName);
            textNumPlate = itemView.findViewById(R.id.textNumPlate);
            textMake = itemView.findViewById(R.id.textMake);
            textColour = itemView.findViewById(R.id.textColour);
            imgCar = itemView.findViewById(R.id.imgCar);

        }
    }
}
