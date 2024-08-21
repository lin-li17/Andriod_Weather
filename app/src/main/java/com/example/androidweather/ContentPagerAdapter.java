package com.example.androidweather;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import java.util.ArrayList;
import java.util.List;

public class ContentPagerAdapter extends FragmentStateAdapter {
    private List<ContentFragment> datas = new ArrayList<>();


    public ContentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return datas.get(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void addData(ContentFragment contentFragment){
        datas.add(contentFragment);
        notifyDataSetChanged();

    }
    public boolean removeData(int position){
        if (position >= 1 && position < datas.size()) {
            datas.get(position).deleteData();
            datas.remove(position);
            notifyItemRemoved(position);//通知这个项被删除了
            notifyItemRangeChanged(position, datas.size() - position);//通知后面的位置被改变了
            return true;
        }else return false;
    }
    //修改
    public void setDatas(int i,ContentFragment contentFragment){
        //先删除文件
        datas.get(i).deleteData();

        datas.set(i,contentFragment);
        notifyDataSetChanged();
    }

}
