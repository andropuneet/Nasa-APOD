//package org.nasa_apod.vo;
//
//import android.arch.persistence.room.Entity;
//import android.arch.persistence.room.TypeConverters;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//
//import com.tsystems.db.ImagiaryTypeConverter;
//import com.tsystems.utils.Constants;
//
//import java.util.List;
//
//@Entity(primaryKeys = {"query"})
//@TypeConverters(ImagiaryTypeConverter.class)
//public class ImageSearchResult {
//    @NonNull
//    public final String query;
//    public final List<Integer> imageIds;
//    public final int totalCount;
//    @Nullable
//    public int next;
//
//    public ImageSearchResult(@NonNull String query, List<Integer> imageIds, int totalCount,
//                             @Nullable Integer next) {
//        this.query = query;
//        this.imageIds = imageIds;
//        this.totalCount = totalCount;
//        if(imageIds != null && imageIds.size() > 0) {
//            int reminder = imageIds.size() % Constants.PER_PAGE;
//            if(reminder != 0) {
//                this.next = -1;
//            } else {
//                this.next = (imageIds.size() / Constants.PER_PAGE) + 1;
//            }
//        }
//    }
//}
