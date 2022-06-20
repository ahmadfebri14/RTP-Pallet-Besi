package com.example.rtppalletbesi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.rtppalletbesi.Utils.DeviceHelper;
import com.example.rtppalletbesi.Utils.PreferenceHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddPackingFragment extends DialogFragment {
    private EditText edtPallet, edtRoll, edtCore;
    private PreferenceHelper preferenceHelper;
    private MainViewModel mainViewModel;
    private final DeviceHelper deviceHelper = new DeviceHelper();

    private Context context;

    private String pallet = "", roll = "", core = "", group = "", date = "";
    private String mode = "insert";
    private String groupName = "";
    private final ArrayList<String> listGroup = new ArrayList<>();

    public AddPackingFragment() {
        // Required empty public constructor
    }

    public static AddPackingFragment newInstance(String pallet, String roll, String core, String group, String date) {
        AddPackingFragment frag = new AddPackingFragment();
        Bundle args = new Bundle();
        args.putString("pallet", pallet);
        args.putString("roll", roll);
        args.putString("core", core);
        args.putString("group", group);
        args.putString("date", date);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Remove Header for API19
        if(getDialog()!= null) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        return inflater.inflate(R.layout.fragment_add_packing, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        preferenceHelper = new PreferenceHelper(context);
        edtPallet = view.findViewById(R.id.edt_pallet);
        edtRoll = view.findViewById(R.id.edt_roll);
        edtCore = view.findViewById(R.id.edt_core);
        Spinner spGroup = view.findViewById(R.id.spinner_group);
        Button btnSimpan = view.findViewById(R.id.btn_simpan);

        pallet = getArguments().getString("pallet", "Scan Pallet");
        roll = getArguments().getString("roll", "Scan Roll");
        core = getArguments().getString("core", "Scan Core");
        group = getArguments().getString("group", "Enter Group");
        date = getArguments().getString("date", "");

        edtPallet.setText(pallet);
        edtRoll.setText(roll);
        edtCore.setText(core);

        listGroup.clear();
        if (!date.equals("")) {
            mode = "unpack";
            btnSimpan.setText(R.string.unpack);
            listGroup.add(group);
        } else {
            mode = "insert";
            btnSimpan.setText(R.string.save);
            listGroup.add(preferenceHelper.getGroup());
        }

        listGroup.add("Pack A");
        listGroup.add("Pack B");
        listGroup.add("Pack C");
        listGroup.add("Pack D");

        ArrayAdapter dataAdapterGroup = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, listGroup);
        dataAdapterGroup.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGroup.setAdapter(dataAdapterGroup);

        spGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                groupName = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode.equals("insert")) {
                    pallet = edtPallet.getText().toString();
                    roll = edtRoll.getText().toString();
                    core = edtCore.getText().toString();
                    group = groupName;
                    preferenceHelper.setGroup(group);
                    date = getDate();

                    if (!pallet.equals("") && !roll.equals("") && !group.equals("")) {
                        ListPacking listPacking = new ListPacking(pallet, roll, core, date, group);
                        mainViewModel.insertToSql(listPacking);

                    } else {
                        Toast.makeText(context, "Pallet, Roll, dan Group tidak boleh kosong", Toast.LENGTH_LONG).show();
                        deviceHelper.vibrateDevice(500, context);
                    }
                } else {
                    pallet = edtPallet.getText().toString();
                    mainViewModel.addPackingFragment.dismiss();
//                    mainViewModel.emptySelectedPacking();
                    mainViewModel.deleteData(pallet);
                }
            }
        });

        mainViewModel = obtainViewModel((AppCompatActivity) context);
        mainViewModel.getValueInserted().observe((LifecycleOwner) context, new Observer<String>() {
            @Override
            public void onChanged(String data) {
                if (data != null && !data.equals("")) {
                    insertData(data);
                }
            }
        });
    }

    private static MainViewModel obtainViewModel(AppCompatActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return new ViewModelProvider(activity, factory).get(MainViewModel.class);
    }

    private void insertData(String data) {
        if (data.contains("PPA") || data.contains("PBA")) {
            edtPallet.setText(data);
        } else if (data.contains("CBA")) {
            edtCore.setText(data);
        } else {
            if (data.length() >= 9 && data.length() <= 11 && isNumeric(data)) {
                edtRoll.setText(data);
            } else {
                Toast.makeText(context, "Tidak sesuai format!", Toast.LENGTH_LONG).show();
                mainViewModel.emptyValueInsert();
                deviceHelper.vibrateDevice(500, context);
            }
        }
    }

    private String getDate() {
        //function untuk mengambil tanggal dan jam
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        DateFormat dt = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return df.format(c) + " " + dt.format(c);
    }

    private static boolean isNumeric(String data) {
        try {
            Double.parseDouble(data);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}