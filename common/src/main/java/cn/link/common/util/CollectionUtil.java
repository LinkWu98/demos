package cn.link.common.util;

import java.util.Collection;
import java.util.List;

/**
 * 集合工具类
 *
 * @author Link50
 * @version 1.0
 * @date 2020/11/25 17:22
 */
public abstract class CollectionUtil {

    public static boolean isEmpty(Collection<?> collection) {

        boolean flag = collection == null || collection.isEmpty();

        if (!flag) {
            return true;
        }

        if (collection instanceof List) {
            List<?> list = (List<?>) collection;
            return list.size() == 1 && list.get(0) == null;

        }

        return false;

    }

}
