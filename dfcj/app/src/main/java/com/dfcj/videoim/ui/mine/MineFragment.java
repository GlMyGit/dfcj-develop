package com.dfcj.videoim.ui.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dfcj.videoim.BR;
import com.dfcj.videoim.R;
import com.dfcj.videoim.base.BaseFragment;
import com.dfcj.videoim.databinding.MineFragmentBinding;

public class MineFragment extends BaseFragment<MineFragmentBinding,MineViewModel> {

    private MineAdapter mineAdapter;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.mine_fragment;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    private void initRecyclerView(){

        mineAdapter=new MineAdapter();
        binding.setAdapter(mineAdapter);
        binding.setLayoutManager(new LinearLayoutManager(getActivity()));




    }



}
