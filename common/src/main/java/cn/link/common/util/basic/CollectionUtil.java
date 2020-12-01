package cn.link.common.util.basic;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 集合工具类
 *
 * @author Link50
 * @version 1.0
 * @date 2020/11/25 17:22
 */
public abstract class CollectionUtil {

    /**
     * list 第一个元素为空也会判空
     *
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {

        boolean flag = collection == null || collection.isEmpty();

        if (flag) {
            return true;
        }

        if (collection instanceof List) {
            List<?> list = (List<?>) collection;
            return list.size() == 1 && list.get(0) == null;
        }

        return false;

    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }


    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

}

