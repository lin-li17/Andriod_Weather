package com.example.androidweather;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;


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
    public boolean removeData(int position, ViewPager2 viewPager2){
        if (position >= 1 && position < datas.size()) {
            viewPager2.setCurrentItem(position - 1);

            ContentFragment fragment = datas.get(position);
            fragment.deleteData();

            datas.remove(position);

            return true;
        }else return false;
    }
    //修改
    public void setDatas(int i,ContentFragment contentFragment){
        //先删除文件
        ContentFragment fragment = datas.get(i);
        fragment.deleteData();
        datas.set(i,contentFragment);
        notifyDataSetChanged();
    }

}
