package langotec.numberq.client.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import langotec.numberq.client.R;

public class CustomBaseAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Menu> menus;
	private LayoutInflater mInflater;

	public CustomBaseAdapter(Context context, ArrayList<Menu> items) {
		this.context = context;
		this.menus = items;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.menu_row, null);
			holder = new ViewHolder();
			holder.textPrice = (TextView) convertView.findViewById(R.id.price);
			holder.textName = (TextView) convertView.findViewById(R.id.productName);
			holder.imageView = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		Menu menu = (Menu) getItem(position);
		holder.textPrice.setText("NT $" + menu.getPrice());
		holder.textName.setText(menu.getProductName());
		menu.setImageView(holder.imageView);

		return convertView;
	}

	@Override
	public int getCount() {
		return menus.size();
	}

	@Override
	public Object getItem(int position) {
		return menus.get(position);
	}

	@Override
	public long getItemId(int position) {
		return menus.indexOf(getItem(position));
	}

    /*private view holder class*/
    private class ViewHolder {
        TextView textPrice;
        TextView textName;
        ImageView imageView;
    }
}