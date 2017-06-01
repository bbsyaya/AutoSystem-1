package com.vampire.shareforwechat.fragment.alarm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.activity.alarm.RemindAddActivity;
import com.vampire.shareforwechat.adapter.MedicineAdapter;
import com.vampire.shareforwechat.fragment.AppBaseFragment;
import com.vampire.shareforwechat.model.alarm.RemindEntity;
import com.vampire.shareforwechat.service.RemindService;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by 焕焕 on 2017/5/18.
 */

public class MedicineFragment extends AppBaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemLongClickListener {
    public static final String EXTRA_ID = "id";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.lv_medicine)
    ListView lvMedicine;
    Button btnMedicineAdd;
    private List<RemindEntity> remindList = new ArrayList<>();
    private OnMedicineFIListener onMedicineFIListener;
    private MedicineAdapter medicineAdapter;

    @Override
    protected void loadData() {

    }

    @Override
    protected void initViews() {
        onMedicineFIListener.onMedicineToolbar(mToolbar);
        lvMedicine.setOnItemClickListener(this);
        lvMedicine.setOnItemLongClickListener(this);
        View viewFooter = LayoutInflater.from(getActivity()).inflate(R.layout.foot_medicine_btn, null, false);
        btnMedicineAdd = (Button) viewFooter.findViewById(R.id.btn_medicine_add);
        lvMedicine.addFooterView(viewFooter);
        btnMedicineAdd.setOnClickListener(this);


    }

    @Override
    public void onResume() {
        super.onResume();
        remindList = DataSupport.findAll(RemindEntity.class);
        medicineAdapter = new MedicineAdapter(getActivity(), remindList, R.layout.item_medicine_remind);
        lvMedicine.setAdapter(medicineAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_medicine;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMedicineFIListener) {
            onMedicineFIListener = (OnMedicineFIListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onMedicineFIListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ID, medicineAdapter.getItem(position).getId());//将id传过去
        startActivity(RemindAddActivity.class, bundle);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ID, -1);//将id传过去
        startActivity(RemindAddActivity.class, bundle);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(getActivity())
                .setTitle("确定要删除吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedId = medicineAdapter.getItem(position).getId();
                        DataSupport.deleteAll(RemindEntity.class, "id=?", selectedId+"");
                        medicineAdapter.removeInfo(medicineAdapter.getItem(position));
                        getActivity().startService(new Intent(getActivity(), RemindService.class));
                    }
                })
                .setNegativeButton("取消", null)
                .show();
        return true;
    }


    public interface OnMedicineFIListener {
        void onMedicineToolbar(Toolbar toolbar);
    }
}
