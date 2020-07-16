package arkitchen.karachi.foodiesshipper.viewholders;


import android.view.View;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import arkitchen.karachi.foodiesshipper.R;
import info.hoang8f.widget.FButton;

/**
 * Created by hp on 2/28/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder {


    public TextView orderName;
    public TextView orderStatus;
    public TextView orderPhone;
    public TextView orderAddress;
    public TextView deliver_at;
    public FButton directions, start, end, call;
    public FButton details;


    private View view;

    public OrderViewHolder(View itemView) {
        super(itemView);
        orderName = itemView.findViewById(R.id.order_name);
        orderStatus = itemView.findViewById(R.id.order_status);
        orderAddress = itemView.findViewById(R.id.order_address);
        orderPhone = itemView.findViewById(R.id.order_phone);
        directions = itemView.findViewById(R.id.directions);
        deliver_at = itemView.findViewById(R.id.deliver_at);
        start = itemView.findViewById(R.id.start);
        end = itemView.findViewById(R.id.end);
        call = itemView.findViewById(R.id.contact);
        //details = itemView.findViewById(R.id.details);


        //itemView.setOnClickListener(this);
//        directions.setOnClickListener(this);
//        details.setOnClickListener(this);

    }

//    @Override
//    public void onClick(View v) {
//
//    }
////
//    public void onItemClickListener(ItemClickListener itemClickListener) {
//        this.itemClickListener = itemClickListener;
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        itemClickListener.onClick(v, getAdapterPosition(), false);
//    }


}

