package net.utils;

import com.tencent.mmkv.MMKV;

import java.util.HashSet;
import java.util.Set;

public class MMKVUtils {

    public static void saveKeys(String key, String keyValues) {
        Set<String> keys = MMKV.defaultMMKV().decodeStringSet(key);
        if (keys == null) {
            keys = new HashSet<>();
        }
        keys.add(keyValues);
        MMKV.defaultMMKV().encode(key, keys);
    }

//    public static ArrayList<DataEntity> getAllDatas(String key) {
//        ArrayList<DataEntity> result = new ArrayList<>();
//        Set<String> keySet = MMKV.defaultMMKV().decodeStringSet(key);
//        if (keySet != null) {
//            for (String item : keySet) {
//                DataEntity entity = MMKV.defaultMMKV().decodeParcelable(item, DataEntity.class);
//                result.add(entity);
//            }
//        }
//        return result;
//    }
}
