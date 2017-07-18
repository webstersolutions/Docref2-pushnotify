package in.docref;


        import android.content.Context;
        import android.media.Image;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import com.android.volley.toolbox.ImageLoader;
        import com.android.volley.toolbox.NetworkImageView;

        import org.w3c.dom.Text;

        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by alfiasorte on 13-07-2017.
 */


public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private ImageLoader imageLoader;
    private ImageLoader imageLoader2;
    private Context context;

    //List of superHeroes
    List<appointments> appointments;

    public CardAdapter(List<appointments> appointments, Context context){
        super();
        //Getting all the superheroes
        this.appointments = appointments;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appointments_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        appointments appoint =  appointments.get(position);

        /*
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(appoint.getImageUrl(), ImageLoader.getImageListener(holder.imageView, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));
        holder.imageView.setImageUrl(appoint.getImageUrl(), imageLoader);

        holder.textViewName.setText(appoint.getName());
        holder.textViewRank.setText(String.valueOf(appoint.getRank()));
        holder.textViewRealName.setText(appoint.getRealName());
        holder.textViewCreatedBy.setText(appoint.getCreatedBy());
        holder.textViewFirstAppearance.setText(appoint.getFirstAppearance());

        String powers = "";

        for(int i = 0; i<appoint.getPowers().size(); i++){
            powers+= appoint.getPowers().get(i);
        }

        holder.textViewPowers.setText(powers);
        */

        holder.textDocID.setText(appoint.getDoctorId());
        holder.textDocName.setText(appoint.getDoctorName());
        holder.textDocMobile.setText(appoint.getDoctorMobile());

        /*
        imageLoader2 = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader2.get(appoint.getImageThumbUrl(), ImageLoader.getImageListener(holder.imageViewThumb, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));

        holder.imageViewThumb.setImageUrl(appoint.getImageThumbUrl(), imageLoader2);

        */

        holder.textPatientName.setText(appoint.getPatientName());
        holder.textPatientMobile.setText(appoint.getPatientMobile());

        holder.textActivityStatus.setText(appoint.getActivityStatus());
        holder.textAppId.setText(appoint.getAppointmentID());
        holder.textAppDate.setText(appoint.getAppointmentDate());
        holder.textCreateTime.setText(appoint.getCreateTime());
        holder.textAppStatus.setText(appoint.getAppointmentStatus());

    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        /*
        public TextView textViewName;
        public TextView textViewRank;
        public TextView textViewRealName;
        public TextView textViewCreatedBy;
        public TextView textViewFirstAppearance;
        public TextView  textViewPowers;
        */

        //public NetworkImageView imageView;
        public TextView textDocID;
        public TextView textDocName;
        public TextView textDocMobile;
        /* public NetworkImageView imageViewThumb; */
        public TextView textPatientName;
        public TextView textPatientMobile;
        public TextView textActivityStatus;
        public TextView textAppId;
        public TextView textAppDate;
        public TextView textCreateTime;
        public TextView textAppStatus;



        public ViewHolder(View itemView) {
            super(itemView);

            /*
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewRank= (TextView) itemView.findViewById(R.id.textViewRank);
            textViewRealName= (TextView) itemView.findViewById(R.id.textViewRealName);
            textViewCreatedBy= (TextView) itemView.findViewById(R.id.textViewCreatedBy);
            textViewFirstAppearance= (TextView) itemView.findViewById(R.id.textViewFirstAppearance);
            textViewPowers= (TextView) itemView.findViewById(R.id.textViewPowers);
             */

            //imageView = (NetworkImageView) itemView.findViewById(R.id.imageViewHero);
            textDocID= (TextView) itemView.findViewById(R.id.textDocId);
            textDocName= (TextView) itemView.findViewById(R.id.textDocName);
            textDocMobile= (TextView) itemView.findViewById(R.id.textDocMobile);
            /* imageViewThumb = (NetworkImageView) itemView.findViewById(R.id.imageViewThumb); */
            textPatientName= (TextView) itemView.findViewById(R.id.textPatientName);
            textPatientMobile= (TextView) itemView.findViewById(R.id.textPatientMobile);
            textActivityStatus= (TextView) itemView.findViewById(R.id.textActivityStatus);
            textAppId= (TextView) itemView.findViewById(R.id.textAppId);
            textAppDate= (TextView) itemView.findViewById(R.id.textAppDate);
            textCreateTime= (TextView) itemView.findViewById(R.id.textCreateTime);
            textAppStatus= (TextView) itemView.findViewById(R.id.textAppStatus);

        }
    }
}