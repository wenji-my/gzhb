package com.example.administrator.envsystem.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.envsystem.R;
import com.example.administrator.envsystem.bean.PdaTaskList;

import java.util.List;

/**
 * Created by Administrator on 2017/4/13.
 */

public class PdaTaskAdapter extends BaseAdapter {

    private List<PdaTaskList> pdaTaskLists;
    private Context context;

    public PdaTaskAdapter(Context c) {
        this.context = c;
    }

    public void setData(List<PdaTaskList> pdaTaskLists) {
        this.pdaTaskLists = pdaTaskLists;
    }

    @Override
    public int getCount() {
        return pdaTaskLists.size();
    }

    @Override
    public Object getItem(int i) {
        return pdaTaskLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        PdaTaskList list = pdaTaskLists.get(i);
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.item_vehicle, null);
            holder.lsh = (TextView) view.findViewById(R.id.text_jylsh);
            holder.lsh1 = (TextView) view.findViewById(R.id.text_lsh);
            holder.cphm = (TextView) view.findViewById(R.id.hphm);
            holder.cpys = (TextView) view.findViewById(R.id.hpzl);
            holder.rllx = (TextView) view.findViewById(R.id.text_clsbdh);
            holder.rwlx = (TextView) view.findViewById(R.id.text_rw);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.lsh1.setText("流水号：" + list.getBusinessid());
        holder.lsh.setText("车辆VIN码：" + list.getVin());
        holder.cphm.setText("车牌号码：" + list.getCarcardnumber());
        holder.cpys.setText("车牌颜色：" + list.getCarcardcolor());
        holder.rllx.setText("燃料类型：" + list.getFuelname());
        String opt = list.getOpt();
        if (opt.equals("add")) {
            holder.rwlx.setText("任务操作：需检查");
        }else if (opt.equals("del")){holder.rwlx.setText("任务操作：无需检查");}
        return view;
    }

    private class ViewHolder {
        TextView lsh;
        TextView lsh1;
        TextView cphm;
        TextView cpys;
        TextView rllx;
        TextView rwlx;
    }
}
