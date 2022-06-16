package com.example.rtppalletbesi;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rtppalletbesi.Adapter.PackingListAdapter;
import com.example.rtppalletbesi.Database.AppDatabase;
import com.example.rtppalletbesi.Utils.DeviceHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private AppDatabase appDatabase;
    private final DeviceHelper deviceHelper = new DeviceHelper();
    private FloatingActionButton fabAdd;
    private PackingListAdapter packingListAdapter;

    private final ArrayList<ListPacking> dataListPackings = new ArrayList<>();
    private TextView txtNoData;
    private ProgressBar pgPacking;
    private RecyclerView rcPacking;
    private CountDownTimer cTimer = null;
    private Boolean statsScan = false;
    private static final int READ_STORAGE_PERMISSION_REQUEST = 123;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appDatabase = AppDatabase.getInstance(this);

        fabAdd = findViewById(R.id.fab_up_pack);
        txtNoData = findViewById(R.id.txt_no_data_packing);
        pgPacking = findViewById(R.id.pg_packing);
        rcPacking = findViewById(R.id.rc_packing);

        fabAdd.setOnClickListener(view -> {
            mainViewModel.emptyValueInsert();
            FragmentManager fm = getSupportFragmentManager();
            mainViewModel.addPackingFragment = AddPackingFragment.newInstance("", "", "", "", "");
            mainViewModel.addPackingFragment.setCancelable(true);
            mainViewModel.addPackingFragment.show(fm, "fragment_edit_name");
        });

        showRecyclerListPallete();

        if (SDK_INT >= Build.VERSION_CODES.R) {
            boolean isGranted = Environment.isExternalStorageManager();
            if (isGranted) {
//                checkTxtData();
            } else {
                showSnackbarPermission();
            }
        } else {
            String permission[] = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
            if (EasyPermissions.hasPermissions(this, permission)) {
//                checkTxtData();
            } else {
                EasyPermissions.requestPermissions(this, "Our App Requires a permission to access your storage", READ_STORAGE_PERMISSION_REQUEST, permission);
            }
        }

        mainViewModel = obtainViewModel(MainActivity.this);
        mainViewModel.getListPacking().observe(this, new Observer<List<ListPacking>>() {
            @Override
            public void onChanged(List<ListPacking> data) {
                dataListPackings.clear();
                dataListPackings.addAll(data);
                packingListAdapter.notifyDataSetChanged();
//                pgPacking.setVisibility(View.GONE);

                if (dataListPackings.size() != 0) {
                    txtNoData.setVisibility(View.GONE);
                } else {
                    txtNoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private static MainViewModel obtainViewModel(AppCompatActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return new ViewModelProvider(activity, factory).get(MainViewModel.class);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Dialog dialog;
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.barcode);
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                exitApp();
            } else {
                if (!dialog.isShowing() && statsScan == false) {

                    final EditText edBarcode = (EditText) dialog.findViewById(R.id.edBarcode);
                    edBarcode.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                                String barcode = edBarcode.getText().toString();
                                mainViewModel.getDataSelectedPacking(barcode);
                                mainViewModel.getSelectedPacking().observe(MainActivity.this, new Observer<ListPacking>() {
                                    @Override
                                    public void onChanged(ListPacking listPacking) {
                                        if (listPacking != null) {
                                            mainViewModel.emptyValueInsert();

                                            FragmentManager fm = getSupportFragmentManager();
                                            Fragment fragment = fm.findFragmentByTag("fragment_edit_name");
                                            if (fragment == null) {
                                                mainViewModel.addPackingFragment = AddPackingFragment.newInstance(
                                                        listPacking.getPalletId(), listPacking.getNoRoll(), listPacking.getCoreId(),
                                                        listPacking.getGroup(), listPacking.getDateScanPallet());
                                                mainViewModel.addPackingFragment.setCancelable(true);
                                                mainViewModel.addPackingFragment.show(fm, "fragment_edit_name");
                                            }
                                        }
                                    }
                                });

                                dialog.dismiss();
                                statsScan = true;
                                rcPacking.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            }
                            return false;
                        }
                    });
                    dialog.show();

                    cTimer = new CountDownTimer(2000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            if (cTimer != null) {
                                cTimer.cancel();
                                dialog.dismiss();
                            }
                        }
                    };
                    cTimer.start();
                }
            }
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);
        if (event.getAction() == KeyEvent.ACTION_UP && event.getAction() != KeyEvent.ACTION_DOWN) {
            statsScan = false;
        }
        return true;
    }

    private void showRecyclerListPallete() {
        rcPacking.setLayoutManager(new LinearLayoutManager(this));
        packingListAdapter = new PackingListAdapter(dataListPackings);
        rcPacking.setAdapter(packingListAdapter);

        packingListAdapter.setOnItemClickCallback((data, position) -> {
            mainViewModel.emptyValueInsert();
            deviceHelper.vibrateDevice(300, MainActivity.this);

            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag("fragment_edit_name");
            if (fragment == null) {
                mainViewModel.addPackingFragment = AddPackingFragment.newInstance(
                        data.getPalletId(), data.getNoRoll(), data.getCoreId(),
                        data.getGroup(), data.getDateScanPallet());
                mainViewModel.addPackingFragment.setCancelable(true);
                mainViewModel.addPackingFragment.show(fm, "fragment_edit_name");
            }
        });
    }

    public class exportData extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            String timeFix = currentTime.replace(":", "");
            String date = df.format(c);
            String dateFix = date.replace("/", "-");
            String file_name = "ScanPalletBesi" + "_" + dateFix + "_" + timeFix;
            boolean hasil = false;

            appDatabase = AppDatabase.getInstance(getApplicationContext());
            List<ListPacking> listPackings = appDatabase.listPackingDao().loadAllListPacking();
            try {
                //Create the folder if not exist
                File directoryFolder = new File("sdcard/PalletBesi/");
                if(!directoryFolder.exists()) {
                    directoryFolder.mkdirs();
                }

                File gpxfile = new File("sdcard/PalletBesi/", file_name + ".txt"); // File Name

                if (!gpxfile.exists()) {
                    FileWriter writer = new FileWriter(gpxfile, true);
                    for (int i = 0; i < listPackings.size(); i++) {
                        String palletId = listPackings.get(i).getPalletId();
                        String palletDate = listPackings.get(i).getDateScanPallet();

                        String roll = listPackings.get(i).getNoRoll();
                        String core = listPackings.get(i).getCoreId();
                        String group = listPackings.get(i).getGroup();

                        writer.append(group + "\t" + palletId + "\t" + palletDate + "\t" +
                                roll + "\t" + core + "\r\n");
                    }
                    writer.flush();
                    writer.close();

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(gpxfile));
                    hasil = true;
                }
            } catch (Exception e) {
                hasil = false;
            }
            return hasil;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Toast.makeText(getApplicationContext(), "Export Berhasil!", Toast.LENGTH_LONG).show();
                deviceHelper.vibrateDevice(300, MainActivity.this);
            } else {
                Toast.makeText(getApplicationContext(), "Export Gagal!", Toast.LENGTH_LONG).show();
                deviceHelper.vibrateDevice(500, MainActivity.this);
            }
            Log.d("hasil_export", "gagal" + aBoolean);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return true;
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_delete:
//                mainViewModel.deleteAllData();
//                break;
//            case R.id.menu_export:
//                new exportData().execute();
//                break;
//        }
        if (item.getItemId() == R.id.menu_delete) {
            mainViewModel.deleteAllData();
        } else if (item.getItemId() == R.id.menu_export) {
            new exportData().execute();
        }
        return super.onOptionsItemSelected(item);
    }


    private void showSnackbarPermission() {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "Please Grant Permissions to access storage", Snackbar.LENGTH_INDEFINITE)
                .setAction("ENABLE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestPermission();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.black))
                .setTextColor(getResources().getColor(R.color.white))
                .setBackgroundTint(getResources().getColor(R.color.black))
                .show();
    }

    private void requestPermission() {
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
            startActivityForResult(intent, 2296);
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, 2296);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
//                    checkTxtData();
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                    showSnackbarPermission();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
        System.exit(0);
    }
}