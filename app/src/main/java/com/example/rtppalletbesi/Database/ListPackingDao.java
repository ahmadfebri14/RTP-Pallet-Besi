package com.example.rtppalletbesi.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.rtppalletbesi.ListPacking;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;


@Dao
public interface ListPackingDao {
    @Query("SELECT * FROM list_packing ORDER BY id DESC")
    List<ListPacking> loadAllListPacking();

    @Query("SELECT * FROM list_packing ORDER BY id DESC")
    Maybe<List<ListPacking>> loadAllListPacking2();

    @Query("SELECT * FROM list_packing WHERE palletId = :data or noRoll = :data or coreId = :data")
    Maybe<List<ListPacking>> loadSelectedtPacking(String data);

    @Insert
    Completable insertListPacking(ListPacking packing);
    //
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateListPacking(ListPacking packing);
    //
    @Query("DELETE FROM list_packing")
    Completable deleteListPacking();

    @Query("DELETE FROM list_packing WHERE palletId = :pallet")
    Completable deleteListPacking(String pallet);
}
