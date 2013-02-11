package rs.pedjaapps.md.helpers;


import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import rs.pedjaapps.md.*;
import rs.pedjaapps.md.entries.*;

public final class SearchAdapter extends ArrayAdapter<SearchListEntry>
{

	private final int itemsItemLayoutResource;
    Context c;
	public SearchAdapter(final Context context, final int itemsItemLayoutResource)
	{
		
		super(context, 0);
		this.itemsItemLayoutResource = itemsItemLayoutResource;
		this.c = context;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent)
	{

		final View view = getWorkingView(convertView);
		final ViewHolder viewHolder = getViewHolder(view);
		final SearchListEntry entry = getItem(position);

		
			
		viewHolder.titleView.setText(entry.getTitle());
		viewHolder.yearView.setText(""+entry.getYear());
		viewHolder.plotView.setOnClickListener(new View.OnClickListener(){

				public void onClick(View p1)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(c);

					builder.setTitle(entry.getTitle());
					builder.setMessage(entry.getPlot());
					builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which)
							{

							}

						});

					AlertDialog alert = builder.create();
					alert.show();
					
				}
				
			
		});
		return view;
	}

	private View getWorkingView(final View convertView)
	{
		View workingView = null;

		if (null == convertView)
		{
			final Context context = getContext();
			final LayoutInflater inflater = (LayoutInflater)context.getSystemService
			(Context.LAYOUT_INFLATER_SERVICE);

			workingView = inflater.inflate(itemsItemLayoutResource, null);
		}
		else
		{
			workingView = convertView;
		}

		return workingView;
	}

	private ViewHolder getViewHolder(final View workingView)
	{
		final Object tag = workingView.getTag();
		ViewHolder viewHolder = null;


		if (null == tag || !(tag instanceof ViewHolder))
		{
			viewHolder = new ViewHolder();

			viewHolder.titleView = (TextView) workingView.findViewById(R.id.title);
			viewHolder.yearView = (TextView) workingView.findViewById(R.id.year);
			viewHolder.plotView = (Button) workingView.findViewById(R.id.plot);
			
			workingView.setTag(viewHolder);

		}
		else
		{
			viewHolder = (ViewHolder) tag;
		}

		return viewHolder;
	}

	private class ViewHolder
	{
		public TextView titleView;
		public TextView yearView;
		public Button plotView;
	}


}
