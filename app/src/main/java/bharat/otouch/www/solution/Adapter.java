package bharat.otouch.www.solution;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by shaan on 12/07/2017.
 */

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<UserData1> data= Collections.emptyList();
    UserData1 current;
    int currentPos=0;

    // create constructor to innitilize context and data sent from MainActivity
    public Adapter(Context context, List<UserData1> data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.user_query, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
         current=data.get(position);
        myHolder.textFishName.setText("Replay:" +current.ExecutiveReplay);

            // load image into imageview using glide
       /* Picasso.with(context).load(current.fishImage)
                // .placeholder(R.drawable.ic_img_error)
                // .error(R.drawable.ic_img_error)
                .into(myHolder.ivFish); */

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textFishName;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textFishName= (TextView) itemView.findViewById(R.id.textFishName);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

        }
    }

}

