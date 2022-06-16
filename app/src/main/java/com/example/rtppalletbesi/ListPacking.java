package com.example.rtppalletbesi;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "list_packing")
public class ListPacking {
    @PrimaryKey(autoGenerate = true)
    public int id;
    String palletId;
    String noRoll;
    String coreId;
    String dateScanPallet;
    String group;

    public ListPacking(String palletId, String noRoll, String coreId, String dateScanPallet, String group) {
        this.id = 0;
        this.palletId = palletId;
        this.noRoll = noRoll;
        this.coreId = coreId;
        this.dateScanPallet = dateScanPallet;
        this.group = group;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPalletId() {
        return palletId;
    }

    public void setPalletId(String palletId) {
        this.palletId = palletId;
    }

    public String getNoRoll() {
        return noRoll;
    }

    public void setNoRoll(String noRoll) {
        this.noRoll = noRoll;
    }

    public String getCoreId() {
        return coreId;
    }

    public void setCoreId(String coreId) {
        this.coreId = coreId;
    }

    public String getDateScanPallet() {
        return dateScanPallet;
    }

    public void setDateScanPallet(String dateScanPallet) {
        this.dateScanPallet = dateScanPallet;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
