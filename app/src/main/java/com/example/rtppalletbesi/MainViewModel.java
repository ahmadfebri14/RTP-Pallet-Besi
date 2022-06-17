package com.example.rtppalletbesi;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rtppalletbesi.Database.AppDatabase;
import com.example.rtppalletbesi.Utils.DeviceHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private final AppDatabase appDatabase;
    private final Application application;
    private final DeviceHelper deviceHelper = new DeviceHelper();
    private final CompositeDisposable disposable = new CompositeDisposable();
    public AddPackingFragment addPackingFragment;
    private MutableLiveData<List<ListPacking>> listDataPackings;
    private MutableLiveData<ListPacking> listSelectedPackings;
    private MutableLiveData<String> valueInsert = new MutableLiveData<>("");

    public LiveData<List<ListPacking>> getListPacking() {
        if (listDataPackings == null) {
            listDataPackings = new MutableLiveData<>();
        }
        return listDataPackings;
    }

    public LiveData<ListPacking> getSelectedPacking() {
        if (listSelectedPackings == null) {
            listSelectedPackings = new MutableLiveData<>();
        }
        return listSelectedPackings;
    }

    public LiveData<String> getValueInserted() {
        if (valueInsert == null) {
            valueInsert = new MutableLiveData<>();
        }
        return valueInsert;
    }

    public MainViewModel(Application application) {
        appDatabase = AppDatabase.getInstance(application);
        this.application = application;
        getDataListPacking();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

    public void insertToSql(ListPacking listPacking) {
        appDatabase.listPackingDao().insertListPacking(listPacking)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(application, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                        deviceHelper.vibrateDevice(300, application);
                        addPackingFragment.dismiss();
                        getDataListPacking();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(application, "Error", Toast.LENGTH_SHORT).show();
                        deviceHelper.vibrateDevice(500, application);
                    }
                });
    }

    public void deleteData(String data) {
        appDatabase.listPackingDao().deleteListPacking(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(application, "Data berhasil di unpack!", Toast.LENGTH_LONG).show();
                        deviceHelper.vibrateDevice(300, application);
                        getDataListPacking();
                        listSelectedPackings = new MutableLiveData<>();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(application, "Data gagal di unpack!", Toast.LENGTH_LONG).show();
                        deviceHelper.vibrateDevice(500, application);
                    }
                });
    }

    public void validateDb(String data) {
        appDatabase.listPackingDao().loadSelectedtPacking(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<List<ListPacking>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull List<ListPacking> listPackings) {
                        if (listPackings.size() != 0) {
                            Toast.makeText(application, "Data sudah ada list packing!", Toast.LENGTH_LONG).show();
                            deviceHelper.vibrateDevice(500, application);
                        } else {
                            deviceHelper.vibrateDevice(300, application);
                            valueInsert.postValue(data);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(application, "Gagal memvalidasi data!", Toast.LENGTH_LONG).show();
                        deviceHelper.vibrateDevice(500, application);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void emptyValueInsert() {
        valueInsert.postValue("");
    }

    public void getDataListPacking() {
        appDatabase.listPackingDao().loadAllListPacking2()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<List<ListPacking>>() {
                    @Override
                    public void onSuccess(@NonNull List<ListPacking> listPackings) {
                        ArrayList<ListPacking> tempList = new ArrayList<>();
                        for (int i = 0; i < listPackings.size(); i++) {
                            String pallet = listPackings.get(i).getPalletId();
                            String roll = listPackings.get(i).getNoRoll();
                            String core = listPackings.get(i).getCoreId();
                            String group = listPackings.get(i).getGroup();
                            String tanggal = listPackings.get(i).getDateScanPallet();

                            ListPacking packing = new ListPacking(pallet, roll, core, tanggal, group);
                            tempList.add(packing);
                        }
                        listDataPackings.postValue(tempList);
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("cek", "error: " + e);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void deleteAllData() {
        appDatabase.listPackingDao().deleteListPacking()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(application, "Data berhasil dihapus!", Toast.LENGTH_LONG).show();
                        deviceHelper.vibrateDevice(300, application);
                        getDataListPacking();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(application, "Data gagal dihapus!", Toast.LENGTH_LONG).show();
                        deviceHelper.vibrateDevice(500, application);
                    }
                });
    }

    public void getDataSelectedPacking(String data) {
        listSelectedPackings = new MutableLiveData<>();

        appDatabase.listPackingDao().loadSelectedtPacking(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<List<ListPacking>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull List<ListPacking> listPackings) {
                        if (listPackings.size() == 0) {
                            Toast.makeText(application, "Data tidak ditemukan!", Toast.LENGTH_LONG).show();
                            deviceHelper.vibrateDevice(500, application);
                        } else {
                            ListPacking data = new ListPacking(listPackings.get(0).getPalletId(), listPackings.get(0).getNoRoll(), listPackings.get(0).getCoreId(),
                                    listPackings.get(0).getDateScanPallet(), listPackings.get(0).getGroup());
                            listSelectedPackings.postValue(data);
                            deviceHelper.vibrateDevice(300, application);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(application, "Data gagal ditemukan!", Toast.LENGTH_LONG).show();
                        deviceHelper.vibrateDevice(500, application);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
