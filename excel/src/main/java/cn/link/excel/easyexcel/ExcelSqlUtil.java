package cn.link.excel.easyexcel;

import cn.link.common.util.basic.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Link50
 * @version 1.0
 * @date 2021/3/11 16:32
 */
public class ExcelSqlUtil {

    private static final String INSERT_PREFIX = "INSERT INTO";
    private static final String INSERT_SUFFIX = "VALUES";
    private static final String DATE_FIELD = "create_date";
    private static final String STR_FIELD = "name";


    /**
     * insert前缀
     *
     * @param tableName
     * @param fieldNames
     * @return
     */
    private static String getInsertSqlPrefix(String tableName, String[] fieldNames) {

        StringBuilder subPrefix = new StringBuilder(INSERT_PREFIX + " " + tableName);

        for (int i = 0; i < fieldNames.length; i++) {

            if (i == 0) {
                subPrefix.append("(");
            }

            String fieldName = fieldNames[i];

            subPrefix.append(fieldName);

            if (i == fieldNames.length - 1) {
                subPrefix.append(")");
            } else {
                subPrefix.append(",");
            }

        }

        return subPrefix.toString();

    }


    /**
     * 通过准确的时间获取插入数据
     * @param tableName
     * @param fieldNames 字段和数据的顺序必须一致
     * @param values     数据之间顺序必须一致
     * @return
     */
    public static List<String> generateInsertSqlByAccurateDate(String tableName, String[] fieldNames, String[] values) {

        List<String> sqlList = new ArrayList<>();


        //插入前缀
        String insertSqlPrefix = getInsertSqlPrefix(tableName, fieldNames);

        int rowNum = 0;

        //数据
        List<List<String>> dataList = new ArrayList<>();
        for (String value : values) {
            List<String> splitData = Arrays.asList(value.split("\\s+"));
            dataList.add(splitData);
            if (splitData.size() > rowNum) {
                rowNum = splitData.size();
            }
        }

        //前缀拼接多行后缀
        for (int i = 0; i < rowNum; i++) {

            StringBuilder sql = new StringBuilder(insertSqlPrefix + INSERT_SUFFIX);


            for (int j = 0; j < fieldNames.length; j++) {

                if (j == 0) {
                    sql.append("(");
                }

                List<String> sameFieldDataList = dataList.get(j);

                String fieldData;
                if (sameFieldDataList.size() == 1) {
                    fieldData = sameFieldDataList.get(0);
                } else {
                    fieldData = sameFieldDataList.get(i);
                }

                //日期字段数据加上单引号
                if (DATE_FIELD.equals(fieldNames[j]) || fieldNames[j].contains(STR_FIELD)) {
                    fieldData = "'" + fieldData + "'";
                }

                //小数点后两位不四舍五入
                if (fieldData.contains(".") && (fieldData.length() - fieldData.indexOf(".")) > 3) {
                    fieldData = fieldData.substring(0, fieldData.indexOf(".") + 3);
                }

                if (fieldData.contains("-")) {
                    fieldData = ((Double)(Double.parseDouble(fieldData) * -1)).toString();
                }

                sql.append(fieldData);

                if (j == fieldNames.length - 1) {
                    sql.append(")");
                } else {
                    sql.append(",");
                }

            }

            sql.append(";");

            sqlList.add(sql.toString());

        }

        return sqlList;

    }

    /**
     * 输入年月，获取对应当月有多少天，遍历value,一个对应一天
     *
     * @param year
     * @param month
     * @param tableName
     * @param fieldNames
     * @param values
     * @return
     */
    public static List<String> generateInsertSqlByTimeCondition(int year, int month, String tableName, String[] fieldNames, String[] values) {

        List<String> sqlList = new ArrayList<>();

        //插入前缀
        String insertSqlPrefix = getInsertSqlPrefix(tableName, fieldNames);

        int rowNumByMonth = DateUtil.getLastDayOfMonth(year, month);

        //数据
        List<List<String>> dataList = new ArrayList<>();
        for (String value : values) {
            List<String> splitData = Arrays.asList(value.split("\\s+"));
            dataList.add(splitData);
        }

        //前缀拼接多行后缀
        for (int i = 0; i < rowNumByMonth; i++) {

            StringBuilder sql = new StringBuilder(insertSqlPrefix + INSERT_SUFFIX);

            for (int j = 0; j < fieldNames.length; j++) {

                if (j == 0) {
                    sql.append("(");
                }

                List<String> sameFieldDataList = dataList.get(j);

                String fieldData;
                if (sameFieldDataList.size() == 1) {
                    fieldData = sameFieldDataList.get(0);
                } else {
                    fieldData = sameFieldDataList.get(i);
                }

                //日期字段,转为年月日
                if (DATE_FIELD.equals(fieldNames[j])) {
                    fieldData = "" + year + "/" + month + "/" + (i + 1);
                }

                //加上单引号
                if (DATE_FIELD.equals(fieldNames[j]) || fieldNames[j].contains(STR_FIELD)) {
                    fieldData = "'" + fieldData + "'";
                }

                //小数点后两位不四舍五入
                if (fieldData.contains(".") && (fieldData.length() - fieldData.indexOf(".")) > 3) {
                    fieldData = fieldData.substring(0, fieldData.indexOf(".") + 3);
                }

                //负数， 乘以 -1
                if (fieldData.contains("-")) {
                    fieldData = ((Double) (Double.parseDouble(fieldData) * -1)).toString();
                }

                sql.append(fieldData);

                if (j == fieldNames.length - 1) {
                    sql.append(")");
                } else {
                    sql.append(",");
                }

            }

            sql.append(";");

            sqlList.add(sql.toString());

        }

        return sqlList;

    }

    public static void printSql(List<String> sqlList) {

        for (String sql : sqlList) {
            System.out.println(sql);
        }

    }

    public static void main(String[] args) {

        //skuCategory();
        //trailAmount();
        //trailProductAmount();
        //getAnchorData19Register();
        //getAnchorData20And21Register();
        //getAnchorData19Total();
        //getAnchorData20And21Total();
        //getAnchorData19Active();
        //getAnchorData20And21Active();
        //getAnchorLiveData();

    }

    /**
     * 商家看板 商品数量 / 选中率(暂缺平台商家数量) ok
     */
    public static void skuCategory() {
        String tableName = "show_seller_data";
        String[] fieldNames = new String[]{"product_amount", "selected_rate", "category_name", "create_date"};
        String[] values = new String[]{
                "26\t260\t443\t693\t1366\t2566\t3029\t4010\t5279\t6794\t7508\t9966\t10247\t12603\t16419\t17358\t18823\t16527\t18781\t22021\t20825\t21442\t22709\t22656\t26130",
                "0.269 \t0.269 \t0.280 \t0.263 \t0.264 \t0.260 \t0.288 \t0.256 \t0.280 \t0.253 \t0.262 \t0.281 \t0.279 \t0.251 \t0.285 \t0.288 \t0.294 \t0.271 \t0.290 \t0.280 \t0.294 \t0.276 \t0.277 \t0.291 \t0.212",
                "食品",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/1\n",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

        tableName = "show_seller_data";
        fieldNames = new String[]{"product_amount", "selected_rate", "category_name", "create_date"};
        values = new String[]{
                "31\t361\t591\t828\t1362\t2546\t3273\t3713\t4973\t6520\t7972\t9632\t12554\t15399\t14485\t15332\t15550\t17241\t20770\t22778\t24685\t27919\t19759\t22525\t23709",
                "0.226 \t0.227 \t0.210 \t0.208 \t0.248 \t0.230 \t0.211 \t0.210 \t0.214 \t0.225 \t0.240 \t0.243 \t0.206 \t0.215 \t0.214 \t0.237 \t0.219 \t0.223 \t0.204 \t0.220 \t0.244 \t0.221 \t0.221 \t0.248 \t0.190",
                "日用百货",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/1\n",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

        tableName = "show_seller_data";
        fieldNames = new String[]{"product_amount", "selected_rate", "category_name", "create_date"};
        values = new String[]{
                "21\t277\t322\t422\t833\t1165\t1425\t2225\t3316\t3260\t5646\t6822\t6757\t8553\t11191\t11682\t13999\t15646\t15577\t17311\t15191\t16618\t17963\t18214\t19563",
                "0.286 \t0.184 \t0.180 \t0.192 \t0.187 \t0.182 \t0.183 \t0.199 \t0.197 \t0.199 \t0.197 \t0.195 \t0.190 \t0.194 \t0.191 \t0.188 \t0.196 \t0.194 \t0.188 \t0.198 \t0.190 \t0.181 \t0.198 \t0.193 \t0.141",
                "美妆",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/1\n",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

        tableName = "show_seller_data";
        fieldNames = new String[]{"product_amount", "selected_rate", "category_name", "create_date"};
        values = new String[]{
                "23\t251\t418\t594\t1069\t2015\t2528\t2826\t4474\t4925\t3374\t5438\t7237\t6365\t6584\t7520\t9555\t9031\t10038\t12345\t9494\t10968\t12479\t12871\t13554",
                "0.217 \t0.223 \t0.220 \t0.205 \t0.203 \t0.222 \t0.214 \t0.223 \t0.209 \t0.224 \t0.221 \t0.206 \t0.229 \t0.217 \t0.200 \t0.205 \t0.219 \t0.211 \t0.217 \t0.214 \t0.203 \t0.205 \t0.226 \t0.221 \t0.169",
                "家居家纺",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/1\n",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

        tableName = "show_seller_data";
        fieldNames = new String[]{"product_amount", "selected_rate", "category_name", "create_date"};
        values = new String[]{
                "29\t251\t344\t330\t474\t787\t849\t1243\t1492\t2631\t2624\t3567\t3471\t3827\t5985\t5052\t5909\t3144\t4197\t3644\t3750\t2991\t3119\t3217\t3485",
                "0.217 \t0.223 \t0.220 \t0.205 \t0.203 \t0.222 \t0.214 \t0.223 \t0.209 \t0.224 \t0.221 \t0.206 \t0.229 \t0.217 \t0.200 \t0.205 \t0.219 \t0.211 \t0.217 \t0.214 \t0.203 \t0.205 \t0.226 \t0.221 \t0.169",
                "服饰穿搭",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/1\n",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

        tableName = "show_seller_data";
        fieldNames = new String[]{"product_amount", "selected_rate", "category_name", "create_date"};
        values = new String[]{
                "25\t169\t342\t431\t832\t1528\t3128\t4542\t4148\t5504\t6090\t4706\t8016\t10287\t11176\t16066\t13914\t20511\t17177\t13011\t20997\t19771\t27966\t27777\t29334",
                "0.200 \t0.231 \t0.213 \t0.258 \t0.213 \t0.229 \t0.218 \t0.249 \t0.282 \t0.221 \t0.264 \t0.219 \t0.227 \t0.205 \t0.229 \t0.212 \t0.226 \t0.250 \t0.262 \t0.210 \t0.235 \t0.275 \t0.227 \t0.232 \t0.174",
                "其他",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/1\n",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

    }

    /**
     * 商家 - 申样金额，拼量承担样品费 ok
     */
    public static void trailAmount() {

        String tableName = "show_trail_amount_data";
        String[] fieldNames = new String[]{"pl_afforded_amount", "trail_product_amount", "create_date"};
        String[] values = new String[]{
                "623590.52\t177571.26\t787577.64\t1141376.23\t2212468.19\t2036978.39\t2493424.96\t4089603.57\t4827273.29\t5168983.62\t9155426.32\t7028448.01\t4396207.32\t1702360.9\t3337899.6878\t2571137.25\t3047367.578\t5944355.9\t7221479.22\t9744117.9\t6196366.14\t9536212.87\t21091644.452\t19698122.5\t12181038.58",
                "750977.51\t328556.8\t1077443\t2051652.09\t4184250\t5122981.14\t6201273.6\t7898020.01\t10052520\t12850560\t27852371.91\t21814472.61\t11546198.08\t2830616.47\t8871167.39\t9641772.75\t10059918.52\t22011765.08\t25294274.95\t30662442.89\t40012376.53\t47215136.74\t79801090.23\t81533423.33\t54265768.27",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

    }

    /**
     * 商家 - 主播、分类 寄样数/金额 ok
     */
    public static void trailProductAmount() {

        //分类维度
        String tableName = "show_seller_trail_data";
        String[] fieldNames = new String[]{"name", "trail_amount", "trail_product_amount", "type", "create_date"};
        String[] values = new String[]{
                "食品",
                "5225\t2693\t7572\t13982\t30595\t39370\t38935\t48983\t72077\t102394\t185862\t167251\t76229\t21160\t60865\t61084\t69043\t155347\t192135\t235088\t275528\t356251\t563013\t489357\t378280",
                "220787.4\t106452.4\t320000.6\t590875.8\t1292933.3\t1690583.8\t1804570.6\t2203547.6\t3045913.6\t4047926.4\t7854368.9\t7067889.1\t3221389.3\t908627.9\t2821031.2\t2747905.2\t2776537.5\t6141282.5\t8119462.3\t9934631.5\t11643601.6\t15297704.3\t26094956.5\t22014024.3\t15212305.6",
                "1",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

        tableName = "show_seller_trail_data";
        fieldNames = new String[]{"name", "trail_amount", "trail_product_amount", "type", "create_date"};
        values = new String[]{
                "日用百货",
                "4108\t1778\t6223\t13642\t24718\t27746\t33026\t37650\t59944\t65088\t159295\t138005\t71723\t15640\t52495\t44048\t64328\t109304\t140367\t197938\t231492\t271214\t442169\t437259\t268294",
                "165215.1\t78787.9\t241778.2\t464904.4\t892919.0\t1115785.3\t1350637.4\t1668061.8\t2410594.3\t2883665.7\t6188797.0\t4703200.3\t2590966.8\t628963.0\t2146822.5\t1951494.8\t2323841.2\t4842588.3\t5453445.7\t6745737.4\t8362586.7\t10906696.6\t18082927.0\t19372341.4\t9692068.8",
                "1",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

        tableName = "show_seller_trail_data";
        fieldNames = new String[]{"name", "trail_amount", "trail_product_amount", "type", "create_date"};
        values = new String[]{
                "美妆",
                "1528\t617\t2042\t3508\t7430\t9750\t14405\t14627\t22499\t24120\t54297\t42161\t21604\t5115\t19109\t19882\t18695\t42133\t49310\t58122\t83271\t85323\t182005\t163454\t62836",
                "142685.7\t63050.0\t212902.7\t358628.8\t739357.0\t963632.8\t1296066.2\t1455605.1\t2100976.7\t2466022.5\t5662387.2\t4310539.8\t2149902.1\t505548.1\t1719232.2\t1978491.8\t1987839.9\t4307702.4\t5142326.1\t5942381.4\t8286563.2\t8432623.4\t16375183.7\t16265918.0\t6681328.9",
                "1",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

        tableName = "show_seller_trail_data";
        fieldNames = new String[]{"name", "trail_amount", "trail_product_amount", "type", "create_date"};
        values = new String[]{
                "家居家纺",
                "1410\t754\t2465\t4647\t8766\t10648\t14940\t16649\t21514\t28584\t57047\t50338\t26631\t5883\t19728\t23006\t21926\t52608\t60074\t63548\t80435\t92361\t168224\t198318\t124283",
                "104761.4\t47805.0\t169697.3\t326212.7\t621361.1\t783816.1\t967398.7\t1078079.7\t1598350.7\t1811929.0\t3927184.4\t3533944.6\t1887803.4\t433084.3\t1277448.1\t1489653.9\t1554257.4\t3334782.4\t4135614.0\t4461385.4\t5701763.7\t6798979.7\t10892848.8\t12841514.2\t8809978.6",
                "1",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

        tableName = "show_seller_trail_data";
        fieldNames = new String[]{"name", "trail_amount", "trail_product_amount", "type", "create_date"};
        values = new String[]{
                "服饰穿搭",
                "981\t341\t1657\t3104\t5804\t6764\t9223\t10530\t14505\t13778\t40010\t32105\t16170\t4230\t11467\t12113\t10815\t25339\t33399\t42596\t49631\t67140\t117572\t98252\t68618",
                "64884.5\t27204.5\t102788.1\t203113.6\t391645.8\t419572.2\t597182.6\t739254.7\t959010.4\t1098722.9\t2481646.3\t2100733.7\t1091115.7\t262398.1\t742516.7\t850404.4\t832961.3\t2020680.0\t2071601.1\t2787216.1\t3349035.9\t4164375.1\t7613024.0\t6897727.6\t5284977.5",
                "1",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

        tableName = "show_seller_trail_data";
        fieldNames = new String[]{"name", "trail_amount", "trail_product_amount", "type", "create_date"};
        values = new String[]{
                "其他",
                "531\t210\t710\t1052\t3098\t2763\t4241\t5043\t6168\t7918\t20053\t10183\t7615\t1557\t5129\t6549\t5625\t13434\t17352\t14314\t25851\t24449\t50106\t52611\t32008",
                "39801.8\t17906.3\t52256.0\t102582.6\t230133.8\t256149.1\t341070.0\t371206.9\t462415.9\t674654.4\t1476175.7\t992558.5\t565763.7\t144361.4\t412509.3\t482088.6\t548265.6\t1144611.8\t1277360.9\t1395141.2\t1920594.1\t2266326.6\t4029955.1\t3872837.6\t3119824.7",
                "1",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

        //主播级别维度
        tableName = "show_seller_trail_data";
        fieldNames = new String[]{"name", "trail_product_amount", "type", "create_date"};
        values = new String[]{
                "特优级主播",
                "20043.25\t15288.34\t34961.48\t79451.56\t163247.06\t176113.18\t239359.51\t230764.6\t428174.69\t546063.61\t746350.7\t674128.79\t176536.09\t99444.65\t186725.35\t263467.72\t277801.16\t505766.31\t687964.93\t546344.37\t1248136.74\t1468077.74\t1228670.71\t1590131.94\t1762143.94",
                "2",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

        tableName = "show_seller_trail_data";
        fieldNames = new String[]{"name", "trail_product_amount", "type", "create_date"};
        values = new String[]{
                "优质级主播",
                "309711.28\t111263.55\t333789.16\t731325.08\t1534801.73\t2355719.99\t1985842.84\t2364280.5\t3477879.02\t3324529.64\t9956573.77\t8320310.62\t5264037.1\t1081701.03\t3134161.33\t3856291.35\t4127629.84\t11079265.79\t9200323.31\t10512650.42\t15583654.83\t14268744.16\t31998377.17\t34644563.59\t20982214.73",
                "2",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

        tableName = "show_seller_trail_data";
        fieldNames = new String[]{"name", "trail_product_amount", "type", "create_date"};
        values = new String[]{
                "活跃级主播",
                "421222.98\t202004.91\t708692.36\t1240875.45\t2486201.21\t2591147.97\t3976071.25\t5302974.91\t6146466.29\t8979966.75\t17149447.44\t12820033.2\t6105624.89\t1649470.79\t5550280.71\t5522013.68\t5654487.52\t10426732.98\t15405986.71\t19603448.1\t23180584.96\t31478314.84\t46574042.35\t45298727.8\t31521409.6",
                "2",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

    }

    public static void getAnchorData20And21Register() {

        //新增注册维度
        String tableName = "show_anchor_data";
        String[] fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        String[] values = new String[]{
                "0 \t0 \t1 \t0 \t1 \t1 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t1 \t1 \t0 \t0 \t0 \t1 \t1 \t0 \t0 \t0 \t0 \t1",
                "61 \t19 \t14 \t27 \t47 \t55 \t56 \t18 \t25 \t41 \t51 \t12 \t32 \t5 \t15 \t11 \t34 \t24 \t5 \t2 \t33 \t43 \t40 \t58 \t33 \t45 \t63 \t1 \t42 \t1 \t23",
                "136 \t40 \t61 \t35 \t53 \t80 \t116 \t115 \t8 \t99 \t4 \t40 \t9 \t64 \t132 \t118 \t71 \t124 \t79 \t77 \t62 \t30 \t134 \t5 \t16 \t85 \t84 \t81 \t5 \t101 \t46",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 1, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t0 \t0 \t1 \t0 \t1 \t0 \t0 \t0 \t1 \t1 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0",
                "9 \t0 \t0 \t0 \t0 \t3 \t12 \t1 \t1 \t11 \t11 \t3 \t7 \t2 \t3 \t10 \t10 \t6 \t3 \t6 \t10 \t7 \t10 \t6 \t3 \t7 \t10 \t12 \t0 \t0 \t0",
                "42 \t150 \t111 \t67 \t130 \t103 \t142 \t121 \t10 \t31 \t17 \t46 \t26 \t103 \t22 \t154 \t8 \t95 \t108 \t65 \t13 \t75 \t45 \t13 \t79 \t72 \t83 \t37 \t0 \t0 \t0",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 2, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t0 \t0 \t0 \t2 \t1 \t1 \t1 \t1 \t2 \t0 \t2 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t2 \t0 \t1 \t0 \t0 \t0 \t1 \t1 \t0 \t1 \t1 \t1",
                "5 \t23 \t37 \t27 \t2 \t3 \t45 \t46 \t15 \t32 \t29 \t41 \t9 \t33 \t31 \t29 \t35 \t26 \t9 \t48 \t14 \t31 \t14 \t19 \t1 \t12 \t32 \t50 \t2 \t28 \t38",
                "24 \t53 \t27 \t51 \t70 \t68 \t85 \t86 \t96 \t114 \t2 \t98 \t52 \t110 \t15 \t9 \t33 \t95 \t39 \t2 \t74 \t98 \t29 \t11 \t0 \t38 \t93 \t91 \t16 \t30 \t73",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 3, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t1 \t1 \t1 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t1 \t1 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t0",
                "2 \t0 \t1 \t0 \t1 \t2 \t3 \t1 \t3 \t2 \t1 \t1 \t2 \t3 \t2 \t3 \t0 \t0 \t3 \t2 \t0 \t1 \t0 \t2 \t2 \t0 \t2 \t2 \t1 \t0 \t0",
                "198 \t3 \t188 \t146 \t33 \t116 \t75 \t215 \t8 \t123 \t123 \t142 \t119 \t190 \t39 \t85 \t162 \t2 \t90 \t207 \t161 \t1 \t57 \t1 \t8 \t24 \t196 \t202 \t146 \t66 \t0",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 4, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t0 \t1 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t1 \t1 \t0 \t0 \t0 \t0 \t0",
                "49 \t31 \t28 \t31 \t18 \t0 \t1 \t16 \t23 \t49 \t1 \t24 \t51 \t32 \t1 \t40 \t30 \t27 \t42 \t9 \t29 \t13 \t8 \t38 \t27 \t35 \t24 \t45 \t12 \t13 \t23",
                "45 \t184 \t115 \t176 \t78 \t198 \t223 \t119 \t108 \t180 \t69 \t210 \t147 \t85 \t151 \t24 \t121 \t56 \t51 \t62 \t54 \t26 \t166 \t24 \t30 \t152 \t47 \t229 \t128 \t138 \t187",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 5, tableName, fieldNames, values));


        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "3 \t0 \t4 \t3 \t2 \t0 \t0 \t3 \t1 \t0 \t2 \t2 \t0 \t1 \t4 \t2 \t0 \t1 \t0 \t2 \t0 \t2 \t0 \t3 \t2 \t1 \t1 \t0 \t2 \t2 \t0",
                "63 \t44 \t73 \t2 \t122 \t89 \t55 \t121 \t103 \t127 \t95 \t99 \t67 \t3 \t25 \t68 \t71 \t66 \t14 \t40 \t43 \t93 \t146 \t97 \t111 \t91 \t56 \t68 \t89 \t1 \t0",
                "15 \t34 \t2 \t57 \t46 \t42 \t98 \t10 \t37 \t102 \t103 \t90 \t96 \t2 \t106 \t1 \t59 \t71 \t73 \t36 \t52 \t71 \t6 \t97 \t48 \t1 \t66 \t94 \t62 \t83 \t0",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 6, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t0 \t0 \t0 \t1 \t1 \t0 \t0 \t0 \t1 \t1 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t1 \t1 \t0 \t1 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t1",
                "1 \t73 \t25 \t7 \t57 \t38 \t40 \t5 \t62 \t25 \t54 \t39 \t51 \t69 \t50 \t32 \t54 \t17 \t37 \t2 \t13 \t61 \t49 \t37 \t56 \t0 \t69 \t0 \t3 \t63 \t20",
                "85 \t119 \t177 \t145 \t203 \t124 \t170 \t2 \t99 \t33 \t272 \t202 \t276 \t114 \t31 \t262 \t274 \t276 \t146 \t92 \t263 \t4 \t9 \t106 \t93 \t187 \t230 \t26 \t165 \t142 \t257",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 7, tableName, fieldNames, values));


        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t1 \t1 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t1 \t1 \t0 \t0 \t0 \t0 \t0 \t1 \t1 \t0",
                "24 \t128 \t112 \t151 \t14 \t16 \t64 \t176 \t31 \t116 \t91 \t110 \t121 \t120 \t32 \t172 \t10 \t22 \t111 \t82 \t27 \t112 \t170 \t51 \t54 \t133 \t184 \t204 \t86 \t148 \t156",
                "145 \t75 \t79 \t47 \t124 \t82 \t71 \t26 \t69 \t31 \t99 \t20 \t146 \t126 \t38 \t111 \t86 \t77 \t29 \t47 \t17 \t79 \t160 \t48 \t56 \t64 \t28 \t70 \t42 \t27 \t14",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 8, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t0 \t1 \t0 \t1 \t0 \t1 \t0 \t1 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t0",
                "90 \t0 \t71 \t137 \t2 \t47 \t13 \t52 \t27 \t40 \t25 \t48 \t117 \t9 \t109 \t48 \t22 \t87 \t129 \t52 \t139 \t65 \t34 \t129 \t29 \t90 \t62 \t107 \t132 \t59 \t0",
                "270 \t4 \t157 \t256 \t41 \t60 \t204 \t306 \t169 \t93 \t200 \t218 \t0 \t29 \t131 \t4 \t243 \t212 \t19 \t166 \t63 \t37 \t67 \t233 \t89 \t217 \t276 \t181 \t132 \t274 \t0",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 9, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t0 \t0 \t1 \t2 \t1 \t0 \t0 \t0 \t1 \t2 \t0 \t1 \t2 \t2 \t1 \t0 \t1 \t1 \t0 \t0 \t2 \t0 \t2 \t0 \t0 \t2 \t0 \t0 \t1 \t1",
                "44 \t25 \t3 \t52 \t44 \t43 \t62 \t44 \t0 \t35 \t64 \t21 \t4 \t71 \t64 \t69 \t9 \t15 \t53 \t24 \t34 \t10 \t36 \t42 \t2 \t52 \t49 \t5 \t52 \t25 \t27",
                "98 \t397 \t125 \t59 \t331 \t86 \t134 \t195 \t280 \t222 \t348 \t114 \t57 \t364 \t213 \t124 \t179 \t256 \t291 \t134 \t196 \t119 \t16 \t176 \t262 \t166 \t134 \t387 \t316 \t373 \t63",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 10, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t2 \t2 \t0 \t0 \t0 \t2 \t0 \t1 \t3 \t3 \t1 \t1 \t1 \t2 \t1 \t1 \t2 \t2 \t1 \t1 \t0 \t0 \t0 \t1 \t0 \t1 \t0 \t2 \t0 \t0",
                "139 \t265 \t186 \t205 \t175 \t230 \t15 \t206 \t203 \t12 \t244 \t282 \t188 \t42 \t56 \t25 \t100 \t32 \t75 \t98 \t44 \t102 \t2 \t138 \t214 \t10 \t247 \t184 \t35 \t141 \t0",
                "93 \t198 \t55 \t206 \t231 \t215 \t115 \t40 \t74 \t52 \t245 \t22 \t249 \t24 \t206 \t79 \t206 \t240 \t190 \t35 \t125 \t153 \t93 \t29 \t148 \t262 \t183 \t13 \t8 \t174 \t0",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 11, tableName, fieldNames, values));


        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t0 \t0 \t0 \t1 \t0 \t1 \t0 \t1 \t0 \t1 \t0 \t1 \t0 \t1 \t1 \t0 \t1 \t0 \t0 \t1 \t0 \t1 \t0 \t0 \t1 \t0 \t1 \t0 \t1 \t0",
                "51 \t218 \t58 \t68 \t216 \t18 \t12 \t183 \t107 \t202 \t96 \t178 \t145 \t179 \t115 \t199 \t34 \t196 \t75 \t177 \t81 \t63 \t154 \t126 \t218 \t158 \t186 \t120 \t55 \t31 \t35",
                "126 \t155 \t6 \t92 \t152 \t83 \t72 \t122 \t89 \t83 \t115 \t18 \t201 \t131 \t167 \t208 \t196 \t69 \t179 \t137 \t1 \t8 \t77 \t145 \t207 \t196 \t217 \t105 \t209 \t128 \t202",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 12, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t1 \t0 \t1 \t1 \t0 \t2 \t1 \t1 \t1 \t0 \t0 \t0 \t2 \t0 \t1 \t1 \t0 \t0 \t0 \t1 \t1 \t0 \t0 \t0 \t2 \t1 \t0 \t1 \t0 \t0",
                "100 \t132 \t10 \t26 \t128 \t134 \t9 \t23 \t139 \t118 \t74 \t13 \t36 \t42 \t46 \t64 \t116 \t1 \t134 \t104 \t8 \t70 \t28 \t33 \t125 \t117 \t43 \t135 \t93 \t72 \t114",
                "136 \t55 \t160 \t164 \t72 \t168 \t17 \t30 \t70 \t20 \t169 \t21 \t76 \t151 \t121 \t14 \t39 \t134 \t120 \t103 \t143 \t61 \t160 \t195 \t94 \t13 \t26 \t105 \t162 \t2 \t128",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2021, 1, tableName, fieldNames, values));

    }

    /**
     * 活跃维度
     */
    public static void getAnchorData19Active() {

        String tableName = "show_anchor_data";
        String[] fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        String[] values = new String[]{
                "0 \t0 \t1 \t0 \t0 \t0 \t1 \t0 \t1 \t0 \t0 \t1 \t1 \t0 \t1 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t1 \t1 \t1 \t1 \t1 \t1 \t1 \t1",
                "0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0",
                "2 \t7 \t10 \t10 \t2 \t1 \t2 \t7 \t5 \t6 \t3 \t9 \t3 \t6 \t4 \t4 \t0 \t2 \t12 \t6 \t5 \t9 \t9 \t13 \t10 \t11 \t4 \t4 \t8 \t8 \t7",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 3, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t1 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0",
                "0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0",
                "26 \t3 \t15 \t59 \t100 \t76 \t17 \t11 \t120 \t13 \t16 \t6 \t127 \t127 \t66 \t1 \t65 \t8 \t122 \t121 \t119 \t5 \t50 \t125 \t100 \t118 \t100 \t39 \t22 \t88 \t0",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 4, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t1 \t1 \t1 \t2 \t1 \t1 \t1 \t1 \t1 \t1 \t1 \t2 \t1 \t1 \t1 \t1 \t1 \t1 \t1 \t1 \t1 \t1 \t2 \t1 \t1 \t1 \t1 \t1 \t1 \t1",
                "13 \t18 \t2 \t13 \t16 \t10 \t5 \t0 \t11 \t10 \t13 \t12 \t9 \t11 \t20 \t15 \t13 \t16 \t22 \t11 \t5 \t23 \t17 \t22 \t1 \t20 \t2 \t10 \t2 \t1 \t9",
                "27 \t106 \t159 \t111 \t15 \t17 \t120 \t124 \t5 \t72 \t129 \t59 \t49 \t158 \t57 \t43 \t40 \t124 \t152 \t118 \t4 \t1 \t139 \t23 \t26 \t123 \t22 \t21 \t88 \t73 \t60",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 5, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t1 \t0",
                "14 \t51 \t4 \t27 \t23 \t49 \t44 \t31 \t53 \t20 \t5 \t18 \t55 \t27 \t33 \t48 \t57 \t10 \t60 \t14 \t19 \t1 \t3 \t11 \t15 \t55 \t53 \t8 \t0 \t60 \t0",
                "24 \t225 \t47 \t20 \t170 \t79 \t77 \t87 \t228 \t28 \t178 \t42 \t142 \t209 \t38 \t34 \t199 \t130 \t189 \t162 \t63 \t55 \t131 \t177 \t143 \t63 \t150 \t131 \t29 \t139 \t0",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 6, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t0 \t1 \t0 \t1 \t0 \t0 \t0 \t1 \t1 \t0 \t1 \t0 \t1 \t0 \t1 \t0 \t1 \t1 \t1 \t0 \t1 \t0 \t1 \t1 \t0 \t0 \t0 \t1 \t0 \t0",
                "24 \t51 \t20 \t53 \t6 \t6 \t71 \t24 \t56 \t62 \t65 \t35 \t39 \t23 \t6 \t22 \t40 \t32 \t1 \t28 \t58 \t7 \t40 \t45 \t55 \t7 \t6 \t55 \t37 \t42 \t55",
                "138 \t8 \t18 \t161 \t152 \t66 \t13 \t66 \t209 \t228 \t128 \t166 \t161 \t52 \t120 \t214 \t33 \t12 \t147 \t140 \t59 \t57 \t201 \t207 \t74 \t2 \t142 \t164 \t56 \t208 \t108",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 7, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t0 \t0 \t2 \t0 \t1 \t0 \t0 \t1 \t1 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t1 \t1 \t0 \t1 \t0 \t1 \t1 \t0 \t1 \t0 \t0 \t1 \t0",
                "17 \t51 \t77 \t53 \t27 \t14 \t1 \t83 \t62 \t75 \t40 \t60 \t27 \t52 \t71 \t16 \t12 \t19 \t20 \t42 \t38 \t28 \t62 \t20 \t79 \t49 \t16 \t37 \t15 \t51 \t33",
                "132 \t136 \t181 \t197 \t178 \t215 \t41 \t82 \t49 \t28 \t220 \t219 \t50 \t8 \t37 \t16 \t139 \t217 \t142 \t4 \t81 \t201 \t222 \t108 \t21 \t111 \t203 \t135 \t207 \t102 \t11",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 8, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t0 \t1 \t1 \t1 \t0 \t0 \t2 \t2 \t1 \t0 \t2 \t2 \t1 \t1 \t1 \t2 \t1 \t1 \t1 \t0 \t2 \t2 \t0 \t2 \t2 \t0 \t1 \t1 \t1 \t0",
                "29 \t31 \t25 \t41 \t95 \t14 \t48 \t92 \t93 \t49 \t94 \t93 \t46 \t3 \t39 \t39 \t57 \t54 \t64 \t10 \t54 \t72 \t22 \t88 \t9 \t24 \t26 \t21 \t97 \t29 \t0",
                "79 \t177 \t52 \t17 \t13 \t9 \t200 \t115 \t18 \t180 \t297 \t134 \t44 \t198 \t219 \t176 \t154 \t197 \t203 \t231 \t142 \t286 \t11 \t259 \t143 \t5 \t219 \t6 \t134 \t302 \t0",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 9, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "2 \t3 \t1 \t1 \t2 \t3 \t2 \t3 \t1 \t2 \t0 \t3 \t2 \t2 \t2 \t3 \t0 \t1 \t1 \t1 \t3 \t3 \t1 \t1 \t2 \t1 \t3 \t0 \t1 \t0 \t1",
                "33 \t73 \t80 \t10 \t21 \t115 \t25 \t23 \t27 \t96 \t7 \t104 \t11 \t80 \t25 \t50 \t51 \t45 \t36 \t87 \t117 \t29 \t82 \t38 \t109 \t28 \t98 \t26 \t22 \t6 \t34",
                "160 \t225 \t103 \t183 \t169 \t174 \t48 \t222 \t126 \t263 \t157 \t81 \t263 \t289 \t194 \t75 \t287 \t175 \t117 \t36 \t118 \t30 \t149 \t185 \t270 \t113 \t205 \t24 \t60 \t17 \t30",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 10, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "2 \t4 \t3 \t1 \t5 \t1 \t4 \t2 \t2 \t0 \t2 \t1 \t3 \t1 \t5 \t1 \t1 \t3 \t2 \t5 \t1 \t3 \t0 \t1 \t2 \t1 \t2 \t1 \t2 \t3 \t0",
                "9 \t107 \t6 \t77 \t57 \t76 \t40 \t73 \t67 \t50 \t61 \t45 \t112 \t74 \t66 \t7 \t24 \t19 \t83 \t53 \t86 \t79 \t55 \t98 \t20 \t47 \t18 \t31 \t99 \t110 \t0",
                "374 \t355 \t392 \t121 \t251 \t375 \t43 \t366 \t114 \t136 \t316 \t312 \t36 \t68 \t441 \t459 \t43 \t307 \t49 \t216 \t287 \t9 \t231 \t248 \t66 \t380 \t29 \t364 \t212 \t300 \t0",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 11, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "2 \t3 \t3 \t2 \t3 \t1 \t3 \t1 \t3 \t3 \t4 \t2 \t2 \t1 \t2 \t4 \t3 \t4 \t2 \t2 \t4 \t1 \t4 \t2 \t3 \t3 \t3 \t3 \t1 \t2 \t1",
                "61 \t18 \t82 \t20 \t107 \t83 \t55 \t68 \t63 \t106 \t4 \t62 \t30 \t38 \t108 \t56 \t56 \t40 \t98 \t79 \t112 \t47 \t59 \t6 \t41 \t68 \t29 \t42 \t55 \t19 \t0",
                "54 \t43 \t279 \t107 \t236 \t199 \t209 \t94 \t1 \t246 \t162 \t31 \t1 \t329 \t71 \t90 \t145 \t273 \t40 \t244 \t156 \t305 \t289 \t132 \t329 \t68 \t177 \t319 \t68 \t101 \t171",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 12, tableName, fieldNames, values));

    }

    public static void getAnchorData20And21Active() {

        //新增注册维度
        String tableName = "show_anchor_data";
        String[] fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        String[] values = new String[]{
                "0 \t1 \t2 \t2 \t3 \t1 \t1 \t3 \t1 \t0 \t1 \t3 \t1 \t1 \t2 \t1 \t1 \t2 \t3 \t2 \t2 \t3 \t3 \t2 \t2 \t0 \t2 \t1 \t1 \t3 \t1",
                "80 \t70 \t89 \t71 \t43 \t48 \t56 \t60 \t7 \t43 \t86 \t9 \t22 \t43 \t42 \t52 \t60 \t38 \t50 \t16 \t4 \t29 \t24 \t34 \t67 \t2 \t34 \t56 \t40 \t53 \t30",
                "0 \t4 \t77 \t63 \t18 \t32 \t42 \t60 \t43 \t98 \t94 \t89 \t9 \t105 \t83 \t8 \t75 \t69 \t83 \t21 \t104 \t98 \t84 \t10 \t42 \t84 \t107 \t7 \t7 \t91 \t37",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 1, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t0 \t0 \t1 \t0 \t1 \t0 \t0 \t0 \t1 \t1 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0",
                "4 \t16 \t20 \t3 \t4 \t2 \t1 \t17 \t13 \t0 \t16 \t15 \t17 \t4 \t19 \t15 \t4 \t18 \t2 \t12 \t0 \t19 \t18 \t4 \t17 \t14 \t8 \t4 \t0 \t0 \t0",
                "5 \t4 \t1 \t11 \t8 \t2 \t22 \t21 \t10 \t18 \t3 \t17 \t10 \t17 \t6 \t11 \t5 \t10 \t2 \t24 \t4 \t24 \t10 \t15 \t17 \t13 \t11 \t15 \t0 \t0 \t0",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 2, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t1 \t1 \t2 \t0 \t1 \t2 \t0 \t2 \t2 \t1 \t0 \t1 \t1 \t1 \t0 \t0 \t1 \t2 \t1 \t1 \t1 \t0 \t2 \t0 \t1 \t0 \t2 \t1 \t1 \t1",
                "27 \t4 \t78 \t65 \t59 \t66 \t26 \t21 \t76 \t76 \t8 \t75 \t17 \t47 \t62 \t29 \t44 \t27 \t21 \t6 \t5 \t45 \t10 \t28 \t41 \t21 \t61 \t39 \t79 \t27 \t47",
                "121 \t22 \t176 \t33 \t55 \t34 \t109 \t70 \t83 \t76 \t182 \t103 \t90 \t70 \t126 \t30 \t16 \t17 \t44 \t163 \t60 \t129 \t172 \t20 \t27 \t83 \t107 \t124 \t108 \t85 \t87",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 3, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t0 \t2 \t1 \t2 \t1 \t2 \t2 \t0 \t2 \t3 \t3 \t3 \t1 \t2 \t3 \t1 \t1 \t2 \t2 \t2 \t2 \t2 \t0 \t0 \t0 \t3 \t2 \t3 \t1 \t0",
                "95 \t100 \t2 \t72 \t2 \t22 \t42 \t86 \t53 \t88 \t42 \t12 \t92 \t12 \t1 \t32 \t60 \t95 \t66 \t96 \t34 \t71 \t28 \t92 \t18 \t70 \t35 \t4 \t90 \t38 \t0",
                "23 \t94 \t188 \t94 \t194 \t75 \t51 \t220 \t204 \t203 \t151 \t199 \t39 \t4 \t19 \t60 \t152 \t168 \t157 \t166 \t240 \t156 \t21 \t156 \t158 \t146 \t87 \t205 \t95 \t21 \t0",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 4, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t3 \t1 \t3 \t3 \t0 \t0 \t1 \t1 \t2 \t1 \t1 \t2 \t3 \t2 \t0 \t0 \t1 \t1 \t0 \t0 \t1 \t2 \t1 \t0 \t2 \t2 \t1 \t1 \t3 \t0",
                "1 \t43 \t108 \t86 \t104 \t27 \t9 \t4 \t113 \t52 \t29 \t2 \t52 \t115 \t18 \t72 \t71 \t51 \t92 \t55 \t82 \t75 \t57 \t67 \t9 \t58 \t109 \t101 \t13 \t13 \t56",
                "243 \t84 \t222 \t214 \t202 \t111 \t173 \t241 \t261 \t233 \t188 \t201 \t57 \t122 \t31 \t5 \t5 \t119 \t79 \t82 \t25 \t112 \t238 \t266 \t15 \t181 \t271 \t52 \t9 \t135 \t35",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 5, tableName, fieldNames, values));


        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "2 \t3 \t1 \t2 \t1 \t2 \t2 \t4 \t0 \t5 \t5 \t6 \t2 \t5 \t5 \t1 \t4 \t1 \t3 \t4 \t0 \t5 \t4 \t0 \t5 \t2 \t1 \t5 \t5 \t5 \t0",
                "105 \t51 \t6 \t152 \t139 \t151 \t133 \t69 \t18 \t42 \t35 \t22 \t85 \t67 \t132 \t79 \t18 \t30 \t13 \t112 \t36 \t129 \t38 \t43 \t128 \t47 \t30 \t95 \t128 \t93 \t0",
                "76 \t296 \t93 \t27 \t242 \t288 \t192 \t215 \t114 \t266 \t190 \t67 \t134 \t81 \t170 \t37 \t16 \t23 \t213 \t232 \t301 \t242 \t89 \t265 \t232 \t180 \t183 \t122 \t218 \t42 \t0",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 6, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t1 \t4 \t6 \t6 \t5 \t3 \t4 \t4 \t6 \t4 \t2 \t3 \t3 \t0 \t4 \t0 \t5 \t0 \t2 \t1 \t6 \t2 \t5 \t2 \t7 \t7 \t1 \t5 \t6 \t1",
                "49 \t103 \t1 \t113 \t180 \t179 \t97 \t243 \t60 \t131 \t3 \t22 \t11 \t189 \t179 \t32 \t238 \t101 \t43 \t5 \t125 \t163 \t31 \t144 \t168 \t93 \t237 \t220 \t217 \t124 \t40",
                "126 \t259 \t3 \t366 \t295 \t466 \t136 \t367 \t219 \t91 \t307 \t400 \t266 \t133 \t237 \t112 \t100 \t315 \t343 \t34 \t133 \t97 \t222 \t231 \t460 \t338 \t131 \t296 \t8 \t165 \t378",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 7, tableName, fieldNames, values));


        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t2 \t5 \t0 \t2 \t0 \t6 \t6 \t3 \t3 \t0 \t7 \t2 \t0 \t3 \t3 \t6 \t2 \t4 \t5 \t4 \t7 \t6 \t5 \t4 \t4 \t1 \t6 \t7 \t3 \t6",
                "287 \t257 \t202 \t230 \t220 \t223 \t242 \t104 \t187 \t112 \t144 \t149 \t77 \t32 \t102 \t150 \t217 \t56 \t66 \t219 \t259 \t9 \t49 \t294 \t6 \t99 \t280 \t86 \t133 \t91 \t205",
                "209 \t485 \t315 \t157 \t418 \t114 \t606 \t423 \t140 \t357 \t10 \t479 \t341 \t605 \t291 \t68 \t521 \t434 \t531 \t21 \t237 \t592 \t215 \t521 \t365 \t130 \t108 \t149 \t345 \t41 \t0",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 8, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t5 \t1 \t5 \t5 \t1 \t8 \t2 \t3 \t1 \t1 \t5 \t4 \t7 \t0 \t7 \t1 \t5 \t0 \t5 \t3 \t1 \t3 \t6 \t4 \t5 \t5 \t6 \t7 \t5 \t0",
                "330 \t247 \t401 \t10 \t260 \t71 \t88 \t179 \t11 \t91 \t221 \t51 \t377 \t20 \t161 \t86 \t193 \t102 \t314 \t365 \t254 \t238 \t119 \t413 \t121 \t412 \t341 \t105 \t180 \t3 \t0",
                "31 \t139 \t723 \t89 \t105 \t890 \t567 \t399 \t546 \t322 \t509 \t413 \t25 \t796 \t354 \t436 \t859 \t878 \t206 \t134 \t431 \t735 \t678 \t796 \t27 \t313 \t136 \t228 \t629 \t26 \t0",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 9, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "2 \t6 \t4 \t0 \t8 \t7 \t7 \t1 \t0 \t6 \t6 \t9 \t0 \t3 \t6 \t5 \t3 \t6 \t9 \t1 \t6 \t4 \t2 \t2 \t2 \t1 \t7 \t10 \t2 \t8 \t6",
                "211 \t102 \t289 \t470 \t358 \t102 \t226 \t275 \t411 \t418 \t285 \t155 \t361 \t247 \t78 \t180 \t56 \t367 \t200 \t413 \t137 \t29 \t239 \t161 \t473 \t215 \t335 \t29 \t210 \t23 \t72",
                "120 \t587 \t321 \t943 \t8 \t599 \t802 \t702 \t442 \t425 \t757 \t661 \t933 \t35 \t109 \t150 \t647 \t386 \t625 \t548 \t10 \t807 \t508 \t803 \t330 \t38 \t674 \t438 \t809 \t928 \t138",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 10, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "2 \t1 \t1 \t13 \t7 \t11 \t5 \t11 \t8 \t1 \t2 \t6 \t10 \t12 \t7 \t10 \t10 \t0 \t12 \t8 \t13 \t10 \t0 \t2 \t1 \t10 \t0 \t7 \t12 \t4 \t0",
                "461 \t79 \t282 \t770 \t806 \t326 \t53 \t272 \t690 \t392 \t351 \t78 \t479 \t279 \t887 \t839 \t823 \t412 \t945 \t339 \t38 \t565 \t598 \t76 \t409 \t363 \t667 \t392 \t27 \t825 \t0",
                "1254 \t880 \t480 \t1136 \t521 \t127 \t538 \t467 \t685 \t310 \t903 \t526 \t468 \t786 \t569 \t896 \t534 \t209 \t446 \t1521 \t784 \t1139 \t567 \t386 \t198 \t676 \t1335 \t932 \t149 \t1076 \t0",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 11, tableName, fieldNames, values));


        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "5 \t7 \t4 \t10 \t10 \t7 \t3 \t10 \t3 \t7 \t1 \t1 \t11 \t11 \t3 \t1 \t1 \t6 \t9 \t5 \t6 \t7 \t6 \t6 \t4 \t6 \t11 \t2 \t10 \t5 \t13",
                "918 \t926 \t746 \t262 \t699 \t27 \t35 \t930 \t406 \t310 \t393 \t673 \t308 \t352 \t42 \t853 \t419 \t245 \t611 \t483 \t33 \t726 \t810 \t630 \t641 \t560 \t543 \t284 \t545 \t527 \t450",
                "511 \t808 \t1386 \t1064 \t983 \t896 \t1190 \t963 \t539 \t1577 \t411 \t296 \t1435 \t126 \t745 \t879 \t1109 \t1226 \t381 \t1562 \t318 \t954 \t824 \t535 \t1579 \t221 \t262 \t633 \t762 \t168 \t971",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 12, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "14 \t10 \t5 \t2 \t4 \t13 \t14 \t0 \t9 \t2 \t7 \t9 \t7 \t13 \t13 \t3 \t5 \t4 \t8 \t2 \t3 \t9 \t14 \t13 \t3 \t2 \t0 \t6 \t9 \t7 \t3",
                "431 \t813 \t784 \t17 \t26 \t320 \t714 \t189 \t305 \t36 \t691 \t599 \t102 \t709 \t37 \t578 \t813 \t293 \t5 \t21 \t243 \t596 \t887 \t761 \t778 \t187 \t705 \t823 \t39 \t675 \t399",
                "913 \t1452 \t409 \t1995 \t2025 \t229 \t119 \t812 \t1165 \t1431 \t1992 \t1396 \t253 \t17 \t1405 \t194 \t413 \t1916 \t1259 \t1267 \t1495 \t1643 \t1593 \t359 \t928 \t796 \t1104 \t187 \t894 \t684 \t1188",
                "3",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2021, 1, tableName, fieldNames, values));

    }

    public static void getAnchorData19Register() {

        //新增注册维度
        String tableName = "show_anchor_data";
        String[] fieldNames = new String[]{"premium_amount", "active_amount", "type", "create_date"};
        String[] values = new String[]{
                "1 \t1 \t0 \t1 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t1 \t0 \t0 \t2 \t1 \t0 \t0 \t1 \t0 \t0 \t2 \t0 \t0 \t1 \t0 \t1 \t0 \t1",
                "7 \t11 \t5 \t10 \t6 \t5 \t2 \t5 \t1 \t1 \t5 \t2 \t9 \t10 \t8 \t7 \t3 \t3 \t2 \t12 \t6 \t7 \t2 \t12 \t10 \t2 \t8 \t5 \t4 \t10 \t10",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 3, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t1 \t0 \t0 \t1 \t1 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t1 \t1 \t0 \t0 \t0 \t1 \t2 \t0 \t0 \t1 \t0 \t1 \t0 \t0 \t0 \t0",
                "122 \t68 \t101 \t22 \t60 \t90 \t70 \t7 \t107 \t40 \t99 \t82 \t91 \t124 \t74 \t120 \t6 \t89 \t83 \t23 \t96 \t45 \t10 \t29 \t92 \t137 \t94 \t21 \t69 \t42 \t0",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 4, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0",
                "1 \t22 \t25 \t20 \t5 \t8 \t3 \t19 \t0 \t5 \t5 \t0 \t8 \t24 \t24 \t23 \t7 \t10 \t25 \t20 \t21 \t0 \t1 \t7 \t21 \t6 \t13 \t18 \t22 \t6 \t14",
                "167 \t90 \t28 \t75 \t65 \t39 \t151 \t176 \t182 \t90 \t101 \t35 \t173 \t1 \t165 \t32 \t10 \t14 \t177 \t93 \t5 \t153 \t5 \t141 \t138 \t162 \t185 \t62 \t83 \t118 \t37",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 5, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0",
                "29 \t54 \t50 \t50 \t8 \t39 \t95 \t28 \t96 \t26 \t64 \t11 \t87 \t116 \t72 \t47 \t4 \t43 \t5 \t95 \t59 \t84 \t39 \t117 \t22 \t33 \t58 \t37 \t72 \t69 \t0",
                "62 \t51 \t61 \t29 \t64 \t56 \t35 \t55 \t3 \t25 \t4 \t1 \t27 \t38 \t58 \t4 \t45 \t36 \t32 \t4 \t17 \t24 \t59 \t11 \t19 \t42 \t58 \t18 \t14 \t35 \t0",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 6, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "2 \t0 \t0 \t1 \t0 \t0 \t1 \t1 \t0 \t1 \t2 \t0 \t1 \t0 \t1 \t0 \t1 \t0 \t1 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t1 \t2 \t0 \t0",
                "14 \t3 \t4 \t16 \t22 \t8 \t4 \t16 \t1 \t4 \t10 \t13 \t2 \t2 \t20 \t4 \t8 \t22 \t15 \t5 \t17 \t19 \t0 \t15 \t16 \t12 \t8 \t17 \t15 \t3 \t3",
                "86 \t9 \t71 \t52 \t74 \t101 \t91 \t4 \t47 \t37 \t22 \t65 \t96 \t57 \t26 \t33 \t88 \t47 \t3 \t60 \t3 \t91 \t96 \t69 \t4 \t82 \t16 \t58 \t97 \t31 \t106",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 7, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t0 \t0 \t0 \t1 \t1 \t0 \t0 \t0 \t1 \t0 \t1 \t1 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0",
                "19 \t8 \t20 \t19 \t26 \t3 \t4 \t16 \t16 \t11 \t21 \t1 \t28 \t31 \t25 \t22 \t7 \t3 \t18 \t4 \t26 \t31 \t25 \t26 \t19 \t3 \t8 \t23 \t14 \t16 \t17",
                "161 \t174 \t100 \t92 \t100 \t53 \t29 \t77 \t107 \t12 \t13 \t152 \t98 \t41 \t38 \t2 \t50 \t113 \t81 \t150 \t82 \t154 \t122 \t128 \t156 \t25 \t49 \t27 \t147 \t43 \t10",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 8, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t0 \t1 \t0 \t0 \t0 \t1 \t1 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t1 \t0 \t0 \t1 \t1 \t0 \t1 \t0",
                "16 \t12 \t16 \t13 \t7 \t15 \t2 \t1 \t8 \t2 \t8 \t1 \t14 \t11 \t12 \t3 \t6 \t14 \t8 \t6 \t3 \t16 \t5 \t9 \t5 \t6 \t0 \t13 \t14 \t12 \t0",
                "64 \t62 \t59 \t142 \t4 \t196 \t183 \t13 \t147 \t191 \t169 \t68 \t37 \t45 \t22 \t104 \t21 \t110 \t95 \t31 \t203 \t50 \t26 \t85 \t95 \t180 \t188 \t185 \t69 \t185 \t0",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 9, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t1 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0",
                "21 \t3 \t20 \t20 \t15 \t11 \t24 \t17 \t10 \t0 \t19 \t12 \t17 \t14 \t25 \t15 \t19 \t8 \t0 \t26 \t5 \t5 \t16 \t23 \t2 \t16 \t5 \t10 \t18 \t22 \t0",
                "39 \t75 \t45 \t86 \t213 \t179 \t115 \t48 \t161 \t64 \t52 \t21 \t158 \t151 \t90 \t115 \t131 \t200 \t113 \t188 \t140 \t231 \t63 \t30 \t65 \t198 \t48 \t112 \t145 \t118 \t138",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 10, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t1 \t0 \t1 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0",
                "4 \t12 \t13 \t13 \t15 \t8 \t3 \t5 \t10 \t2 \t9 \t8 \t10 \t2 \t3 \t2 \t6 \t4 \t6 \t7 \t8 \t4 \t1 \t11 \t4 \t3 \t8 \t2 \t12 \t13 \t0",
                "278 \t29 \t101 \t28 \t4 \t222 \t131 \t10 \t294 \t69 \t90 \t259 \t197 \t239 \t101 \t91 \t87 \t5 \t187 \t227 \t160 \t147 \t189 \t4 \t299 \t233 \t275 \t224 \t3 \t54 \t0",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 11, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t0 \t0 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t1 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t0 \t0",
                "15 \t3 \t7 \t2 \t4 \t3 \t15 \t9 \t11 \t5 \t7 \t0 \t14 \t14 \t9 \t1 \t15 \t8 \t2 \t14 \t12 \t1 \t8 \t9 \t3 \t2 \t1 \t3 \t7 \t5 \t14",
                "76 \t4 \t118 \t215 \t223 \t96 \t118 \t102 \t107 \t6 \t91 \t122 \t55 \t40 \t131 \t240 \t101 \t13 \t201 \t18 \t170 \t125 \t171 \t111 \t129 \t218 \t100 \t184 \t4 \t135 \t145",
                "2",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 12, tableName, fieldNames, values));
    }

    /**
     * 累计维度
     */
    public static void getAnchorData19Total() {

        //新增注册维度
        String tableName = "show_anchor_data";
        String[] fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        String[] values = new String[]{
                "0 \t1 \t0 \t0 \t0 \t0 \t0 \t1 \t1 \t1 \t0 \t0 \t0 \t1 \t0 \t1 \t1 \t1 \t1 \t0 \t1 \t1 \t1 \t0 \t0 \t1 \t0 \t0 \t0 \t1 \t1",
                "0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0",
                "6 \t3 \t2 \t7 \t3 \t11 \t9 \t5 \t8 \t5 \t1 \t5 \t3 \t8 \t1 \t11 \t11 \t5 \t8 \t9 \t6 \t7 \t2 \t10 \t11 \t2 \t10 \t11 \t3 \t0 \t5",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 3, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t0 \t2 \t0 \t1 \t0 \t2 \t1 \t1 \t2 \t1 \t2 \t2 \t2 \t1 \t0 \t1 \t1 \t1 \t0 \t0 \t1 \t1 \t2 \t1 \t0 \t0 \t2 \t1 \t0 \t0",
                "0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0 \t0",
                "3 \t9 \t45 \t132 \t102 \t112 \t134 \t60 \t47 \t39 \t120 \t39 \t69 \t80 \t73 \t145 \t120 \t90 \t56 \t7 \t119 \t25 \t61 \t114 \t125 \t147 \t60 \t63 \t7 \t101 \t0",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 4, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t0 \t0 \t2 \t2 \t2 \t2 \t1 \t1 \t1 \t2 \t0 \t0 \t0 \t2 \t1 \t1 \t2 \t1 \t2 \t0 \t1 \t2 \t1 \t2 \t1 \t0 \t0 \t0 \t2 \t2",
                "0 \t5 \t13 \t9 \t18 \t18 \t14 \t18 \t11 \t15 \t21 \t9 \t11 \t9 \t22 \t8 \t19 \t1 \t13 \t21 \t17 \t25 \t10 \t1 \t15 \t15 \t15 \t1 \t12 \t5 \t0",
                "62 \t88 \t70 \t43 \t229 \t172 \t94 \t273 \t132 \t241 \t366 \t137 \t49 \t71 \t271 \t351 \t284 \t214 \t91 \t199 \t54 \t180 \t134 \t186 \t59 \t281 \t318 \t80 \t141 \t160 \t223",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 5, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "2 \t1 \t2 \t1 \t0 \t2 \t0 \t2 \t2 \t2 \t2 \t1 \t1 \t2 \t1 \t3 \t0 \t3 \t2 \t1 \t0 \t2 \t1 \t0 \t2 \t0 \t3 \t1 \t3 \t1 \t0",
                "52 \t37 \t31 \t11 \t86 \t58 \t39 \t69 \t57 \t44 \t32 \t113 \t23 \t109 \t135 \t136 \t111 \t42 \t23 \t37 \t100 \t11 \t12 \t104 \t113 \t70 \t113 \t100 \t39 \t69 \t0",
                "192 \t113 \t63 \t399 \t199 \t79 \t258 \t17 \t289 \t30 \t15 \t150 \t29 \t392 \t75 \t432 \t154 \t177 \t301 \t281 \t226 \t292 \t342 \t312 \t317 \t143 \t90 \t71 \t462 \t318 \t0",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 6, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "3 \t1 \t0 \t2 \t0 \t1 \t2 \t2 \t1 \t3 \t1 \t0 \t1 \t2 \t1 \t3 \t3 \t0 \t3 \t3 \t3 \t3 \t0 \t0 \t0 \t2 \t1 \t1 \t0 \t3 \t2",
                "64 \t86 \t68 \t42 \t86 \t99 \t21 \t159 \t25 \t13 \t147 \t84 \t5 \t131 \t60 \t56 \t30 \t109 \t82 \t141 \t86 \t20 \t138 \t62 \t104 \t48 \t53 \t1 \t89 \t87 \t87",
                "494 \t272 \t71 \t246 \t438 \t32 \t437 \t54 \t194 \t111 \t261 \t337 \t470 \t413 \t481 \t306 \t140 \t215 \t60 \t501 \t144 \t273 \t205 \t35 \t247 \t38 \t414 \t233 \t248 \t310 \t269",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 7, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "0 \t3 \t2 \t0 \t1 \t3 \t1 \t3 \t2 \t3 \t3 \t4 \t2 \t0 \t2 \t1 \t3 \t1 \t2 \t2 \t4 \t2 \t0 \t3 \t3 \t3 \t3 \t1 \t0 \t0 \t4",
                "168 \t40 \t173 \t36 \t128 \t75 \t87 \t175 \t70 \t17 \t163 \t35 \t130 \t92 \t171 \t136 \t59 \t35 \t21 \t84 \t56 \t141 \t7 \t71 \t5 \t75 \t59 \t158 \t120 \t149 \t50",
                "428 \t561 \t214 \t14 \t607 \t649 \t219 \t271 \t492 \t625 \t626 \t294 \t571 \t340 \t197 \t613 \t349 \t338 \t533 \t11 \t36 \t621 \t286 \t50 \t429 \t66 \t350 \t145 \t248 \t75 \t288",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 8, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "3 \t3 \t4 \t3 \t1 \t3 \t2 \t3 \t3 \t4 \t4 \t0 \t2 \t3 \t5 \t3 \t5 \t1 \t3 \t1 \t1 \t2 \t2 \t1 \t4 \t5 \t4 \t4 \t4 \t3 \t0",
                "90 \t63 \t142 \t191 \t24 \t149 \t124 \t47 \t151 \t80 \t82 \t86 \t105 \t44 \t96 \t90 \t60 \t150 \t217 \t178 \t92 \t157 \t13 \t21 \t137 \t178 \t155 \t4 \t69 \t57 \t0",
                "106 \t12 \t42 \t546 \t221 \t239 \t190 \t180 \t296 \t661 \t498 \t835 \t104 \t791 \t273 \t481 \t946 \t857 \t5 \t135 \t965 \t415 \t385 \t545 \t635 \t922 \t713 \t743 \t703 \t125 \t0",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 9, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "4 \t0 \t1 \t4 \t5 \t5 \t4 \t3 \t5 \t4 \t4 \t4 \t2 \t1 \t4 \t3 \t1 \t2 \t1 \t2 \t0 \t6 \t4 \t2 \t2 \t1 \t5 \t4 \t2 \t3 \t1",
                "14 \t74 \t61 \t174 \t182 \t17 \t88 \t70 \t72 \t161 \t99 \t3 \t202 \t216 \t83 \t132 \t16 \t216 \t223 \t45 \t101 \t38 \t147 \t111 \t80 \t175 \t160 \t116 \t172 \t138 \t101",
                "806 \t48 \t91 \t457 \t566 \t513 \t495 \t70 \t943 \t814 \t640 \t332 \t982 \t677 \t578 \t1076 \t843 \t787 \t156 \t176 \t326 \t690 \t686 \t421 \t198 \t42 \t602 \t877 \t551 \t761 \t886",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 10, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t5 \t1 \t3 \t5 \t5 \t5 \t1 \t1 \t5 \t2 \t2 \t2 \t4 \t2 \t6 \t4 \t4 \t5 \t0 \t4 \t6 \t2 \t2 \t3 \t1 \t5 \t1 \t6 \t2 \t0",
                "45 \t103 \t143 \t284 \t112 \t34 \t126 \t108 \t14 \t74 \t167 \t133 \t16 \t257 \t96 \t274 \t64 \t120 \t133 \t40 \t111 \t237 \t107 \t214 \t224 \t87 \t90 \t146 \t104 \t18 \t0",
                "393 \t703 \t246 \t958 \t1355 \t616 \t564 \t1599 \t138 \t251 \t841 \t885 \t678 \t1221 \t760 \t1096 \t837 \t326 \t151 \t516 \t1046 \t154 \t740 \t251 \t1062 \t1076 \t348 \t1061 \t622 \t847 \t0",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 11, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t2 \t4 \t4 \t5 \t5 \t4 \t1 \t6 \t0 \t7 \t4 \t1 \t5 \t6 \t4 \t5 \t1 \t3 \t4 \t1 \t2 \t5 \t1 \t6 \t3 \t1 \t3 \t2 \t4 \t3",
                "179 \t186 \t146 \t19 \t138 \t233 \t230 \t109 \t206 \t3 \t123 \t210 \t220 \t11 \t159 \t90 \t149 \t145 \t24 \t61 \t50 \t219 \t159 \t180 \t149 \t130 \t58 \t13 \t79 \t153 \t74",
                "936 \t1512 \t306 \t1504 \t1194 \t1574 \t1039 \t253 \t649 \t140 \t193 \t336 \t1546 \t379 \t1135 \t919 \t855 \t332 \t557 \t945 \t88 \t483 \t547 \t1253 \t289 \t176 \t467 \t1588 \t1146 \t1302 \t1258",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2019, 12, tableName, fieldNames, values));

    }

    public static void getAnchorLiveData() {

        //新增注册维度
        String tableName = "show_anchor_live_data";
        String[] fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        String[] values = new String[]{
                "2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t2\t68\t2\t2\t2\t3\t2\t3\t2\t3\t3\t3\t2\t3\t3\t2\t2\t2\t3\t3\t2\t3\t2\t3\t3\t2\t2\t2\t3\t2\t2\t1\t72\t2\t2\t2\t3\t2\t2\t2\t2\t2\t3\t2\t2\t3\t3\t2\t3\t2\t2\t3\t2\t2\t2\t2\t2\t2\t3\t3\t2\t2\t2\t4\t76\t2\t2\t2\t3\t2\t3\t3\t2\t3\t3\t3\t2\t3\t3\t2\t3\t3\t3\t3\t3\t3\t3\t3\t3\t2\t3\t3\t3\t3\t4\t80\t4\t3\t4\t3\t4\t3\t4\t4\t3\t4\t4\t3\t4\t3\t3\t3\t4\t4\t3\t3\t3\t3\t3\t4\t4\t4\t3\t4\t3\t3\t4\t110\t6\t6\t6\t6\t7\t6\t6\t6\t7\t6\t6\t6\t6\t6\t6\t6\t6\t7\t6\t6\t6\t6\t6\t7\t5\t6\t6\t6\t6\t6\t8\t189\t9\t9\t9\t10\t9\t9\t9\t9\t9\t8\t10\t8\t9\t10\t9\t9\t8\t9\t9\t10\t8\t9\t9\t9\t9\t10\t8\t9\t8\t11\t272\t11\t13\t13\t12\t13\t13\t12\t12\t12\t12\t12\t12\t12\t12\t11\t12\t11\t12\t13\t11\t13\t12\t13\t12\t13\t11\t11\t12\t11\t12\t402\t765\t46\t56\t51\t52\t46\t50\t54\t47\t47\t49\t47\t53\t50\t55\t47\t48\t51\t53\t53\t55\t47\t50\t46\t47\t51\t56\t54\t49\t55\t54\t1518\t39\t36\t39\t39\t37\t39\t36\t36\t36\t33\t38\t35\t35\t35\t35\t33\t36\t35\t38\t40\t36\t35\t35\t33\t35\t39\t34\t38\t33\t36\t50\t1134\t17\t16\t14\t15\t16\t16\t16\t17\t16\t17\t17\t15\t16\t15\t17\t16\t17\t17\t14\t15\t15\t16\t15\t16\t14\t16\t15\t16\t16\t15\t19\t492\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t5\t7\t144\t18\t17\t16\t17\t18\t18\t17\t16\t18\t16\t19\t18\t17\t19\t17\t18\t18\t19\t18\t16\t18\t17\t17\t17\t18\t19\t19\t19\t18\t16\t14\t540\t28\t30\t31\t31\t27\t30\t31\t29\t32\t29\t28\t28\t30\t31\t32\t27\t27\t27\t27\t32\t31\t32\t28\t29\t32\t27\t30\t27\t28\t24\t874\t25\t24\t25\t25\t25\t24\t26\t27\t27\t26\t25\t23\t27\t27\t25\t26\t26\t27\t25\t24\t25\t27\t23\t26\t26\t27\t24\t23\t24\t27\t21\t780\t70\t67\t74\t75\t71\t74\t74\t62\t67\t64\t63\t63\t62\t62\t70\t62\t68\t74\t61\t66\t68\t74\t61\t74\t69\t64\t66\t68\t62\t89\t2046\t75\t71\t73\t73\t77\t74\t81\t77\t69\t77\t78\t68\t70\t73\t80\t67\t79\t68\t76\t74\t79\t79\t71\t71\t80\t77\t72\t69\t82\t68\t83\t2310\t88\t94\t94\t92\t92\t97\t95\t95\t86\t96\t87\t97\t95\t89\t98\t101\t99\t90\t86\t91\t98\t86\t103\t93\t100\t88\t93\t90\t85\t88\t115\t2900\t91\t95\t94\t87\t89\t85\t97\t92\t92\t89\t101\t96\t95\t96\t94\t99\t83\t90\t99\t92\t84\t87\t95\t96\t92\t84\t86\t92\t93\t85\t2750\t114\t117\t110\t126\t123\t122\t119\t112\t111\t118\t120\t118\t119\t120\t115\t117\t110\t123\t123\t121\t122\t117\t123\t123\t121\t124\t121\t110\t113\t110\t8\t3552\t174\t164\t165\t153\t165\t178\t168\t155\t177\t166\t153\t179\t170\t173\t154\t165\t161\t177\t159\t152\t175\t153\t151\t167\t178\t179\t151\t175\t168\t144\t4950\t176\t180\t165\t183\t173\t195\t173\t163\t189\t197\t164\t174\t184\t175\t181\t167\t169\t188\t189\t162\t191\t174\t191\t177\t177\t196\t176\t174\t175\t162\t228\t5568\t174\t161\t177\t155\t184\t169\t181\t155\t152\t175",
                "0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t277\t248\t243\t291\t252\t284\t266\t261\t253\t293\t283\t274\t281\t284\t266\t283\t290\t263\t292\t268\t260\t256\t266\t247\t246\t251\t293\t265\t292\t253\t222\t8303\t930\t890\t892\t880\t947\t839\t958\t984\t831\t935\t878\t877\t901\t926\t900\t864\t858\t886\t935\t898\t954\t854\t998\t885\t901\t890\t973\t873\t989\t1128\t27456\t1168\t1131\t1134\t1066\t1104\t1177\t1076\t1181\t1162\t1215\t1111\t1136\t1141\t1134\t1219\t1092\t1197\t1200\t1039\t1215\t1143\t1025\t1087\t1036\t1009\t1223\t1043\t1039\t1225\t1194\t704\t34624\t1333\t1320\t1412\t1480\t1320\t1542\t1480\t1392\t1441\t1475\t1305\t1379\t1461\t1350\t1501\t1336\t1407\t1324\t1461\t1517\t1275\t1381\t1277\t1473\t1365\t1440\t1326\t1469\t1458\t1399\t1441\t43540\t2412\t1985\t2104\t2396\t2097\t2022\t2104\t2013\t2035\t2313\t2152\t2055\t2284\t2339\t2099\t2379\t2401\t2071\t2280\t2302\t2201\t2394\t2396\t2416\t1989\t2405\t2209\t2016\t2046\t2099\t66015\t1409\t1562\t1520\t1517\t1637\t1589\t1604\t1637\t1607\t1573\t1508\t1442\t1489\t1412\t1475\t1349\t1519\t1490\t1351\t1577\t1635\t1474\t1469\t1555\t1439\t1406\t1376\t1568\t1358\t1591\t47717\t92858\t4749\t5497\t5151\t5090\t5446\t4687\t5247\t5268\t4906\t4774\t5034\t5598\t5405\t4626\t5298\t5201\t4581\t4784\t5568\t5405\t5542\t5100\t5517\t4784\t5151\t5542\t5018\t5074\t4647\t3995\t152685\t3267\t3257\t3571\t3036\t3591\t3340\t3488\t3407\t3461\t3367\t3625\t3581\t3605\t3394\t3153\t3250\t3193\t3273\t3334\t3113\t3089\t3089\t3270\t3053\t3417\t3317\t3310\t3327\t3150\t3625\t3806\t103761\t2249\t2299\t1981\t1949\t2139\t1956\t2034\t2101\t1947\t2112\t1922\t1897\t2282\t2192\t2118\t2031\t2038\t2088\t2164\t2202\t1935\t2295\t2053\t2257\t1966\t2255\t2040\t2015\t2133\t2276\t2404\t65328\t460\t444\t497\t453\t475\t445\t529\t476\t453\t494\t446\t452\t539\t503\t486\t493\t443\t486\t515\t455\t463\t513\t452\t458\t499\t485\t521\t520\t796\t14250\t1614\t1588\t1783\t1528\t1533\t1550\t1727\t1788\t1773\t1685\t1710\t1560\t1789\t1604\t1786\t1786\t1504\t1624\t1588\t1788\t1705\t1743\t1763\t1519\t1599\t1538\t1550\t1710\t1540\t1502\t1692\t51168\t2037\t1940\t2148\t1953\t2211\t2270\t2224\t1965\t2268\t1965\t1944\t2239\t1919\t2169\t1923\t2253\t2119\t1957\t2257\t2136\t2178\t2243\t2209\t2194\t1900\t2110\t1923\t2136\t2278\t1988\t63058\t2388\t2553\t2567\t2560\t2514\t2272\t2413\t2294\t2246\t2413\t2640\t2625\t2487\t2362\t2625\t2594\t2299\t2598\t2217\t2478\t2468\t2618\t2499\t2483\t2384\t2594\t2289\t2181\t2531\t2492\t1181\t74863\t4031\t4101\t3550\t4159\t3539\t3903\t3903\t3763\t4217\t3616\t3666\t4197\t3488\t4097\t3996\t4031\t3787\t3872\t3635\t4159\t4244\t3918\t4042\t3539\t3841\t3597\t3973\t3616\t3860\t3934\t116272\t7799\t7899\t6676\t7439\t6971\t6640\t7892\t7302\t7525\t7165\t6518\t6748\t7892\t7870\t6619\t7827\t7518\t7698\t7885\t6878\t7446\t7072\t7532\t7180\t7000\t6705\t7439\t7856\t7863\t7691\t2475\t223020\t9646\t8702\t10054\t8656\t9267\t10146\t8434\t9396\t8452\t8619\t8739\t9341\t8970\t8350\t9998\t8369\t9109\t8396\t8332\t9618\t9776\t9757\t8813\t9220\t9683\t8943\t10165\t8452\t9748\t8619\t13210\t286980\t13252\t12965\t12064\t14087\t13591\t12273\t14074\t12116\t12860\t12664\t12834\t12038\t12455\t12703\t13500\t13905\t13552\t13265\t11829\t13774\t12717\t11842\t14257\t12403\t14296\t11907\t13095\t12730\t12886\t15746\t391680\t11770\t11884\t12427\t13828\t11707\t13791\t13386\t13172\t13172\t12149\t12944\t12225\t12111\t13828\t13096\t11719\t12300\t13513\t12667\t12755\t12957\t13121\t11884\t13690\t11884\t11467\t12452\t13513\t13349\t11618\t11113\t391490\t30006\t28165\t27039\t24813\t25143\t25610\t27781\t25830\t29787\t29402\t28303\t28303\t25198\t27671\t25143\t29567\t24868\t27396\t28330\t25610\t25555\t29924\t30199\t29292\t28110\t29594\t25555\t25830\t29814\t26517\t824354\t31538\t29315\t31157\t30998\t34333\t30617\t30967\t32459\t32840\t30617\t31570\t29410\t32682\t30776\t30014\t33285\t33094\t31951\t33221\t32396\t30808\t28997\t34047\t32936\t33285\t30331\t30268\t32332\t30236\t34810\t33285\t984576\t21082\t20368\t24053\t20167\t20636\t23874\t20591\t23874\t23494\t22914",
                "570\t560\t573\t633\t646\t642\t633\t558\t633\t566\t547\t651\t587\t650\t560\t625\t560\t590\t590\t552\t564\t570\t546\t548\t578\t573\t595\t560\t558\t629\t971\t18620\t4665\t5300\t4906\t5316\t4927\t4716\t5208\t5408\t5546\t5444\t5326\t5172\t5526\t4870\t5408\t4752\t5582\t5377\t5244\t4711\t5495\t5480\t5070\t5142\t5413\t4860\t4824\t4798\t5331\t3816\t153633\t9588\t8205\t9157\t8963\t8347\t9007\t9225\t9084\t8209\t8242\t8516\t9709\t8636\t8578\t8815\t8488\t8664\t8008\t8753\t8258\t9240\t8398\t8569\t9244\t8744\t8921\t9590\t8525\t8052\t8583\t11746\t274065\t4444\t4687\t4232\t5292\t4727\t4755\t4925\t4974\t5212\t5118\t5272\t4344\t5022\t4254\t4689\t4782\t4317\t4981\t4665\t5048\t5077\t5228\t4449\t5135\t4921\t4443\t4729\t5209\t5212\t2446\t142590\t3062\t3396\t3115\t3249\t3563\t3657\t3359\t3110\t3826\t3540\t3708\t3907\t3706\t3148\t3277\t3533\t3864\t3685\t3317\t3480\t3821\t3693\t3859\t3649\t4030\t3105\t3716\t3386\t3716\t3589\t3118\t109182\t5797\t4906\t5738\t4413\t5429\t5312\t5433\t5129\t4517\t4516\t4667\t4952\t5133\t5387\t5196\t5421\t4943\t5314\t5329\t5652\t4873\t5448\t5650\t5348\t5640\t4891\t5294\t5091\t5697\t4939\t2959\t159014\t6979\t7584\t7466\t6806\t6586\t6724\t6875\t6062\t6594\t7410\t7444\t6781\t5808\t6048\t6593\t5965\t6650\t7382\t7003\t6193\t6063\t6155\t6367\t5900\t7795\t6698\t6376\t7097\t6100\t8766\t202272\t4125\t3936\t4964\t4000\t3867\t3820\t4568\t9385\t10997\t9543\t9926\t12027\t12845\t11730\t11527\t12799\t12022\t12844\t13492\t12899\t11464\t12676\t13402\t13728\t13120\t12019\t10502\t11308\t9487\t9596\t-26038\t272580\t18466\t18468\t16990\t20065\t19953\t18261\t20808\t19631\t18449\t18057\t18038\t17703\t16595\t19885\t16586\t20004\t19151\t19016\t16571\t19984\t16413\t19037\t19904\t18643\t19317\t19395\t20348\t17211\t18653\t16244\t557847\t11352\t10625\t10375\t9080\t10316\t8882\t10326\t10218\t9963\t9131\t8747\t9951\t8864\t10583\t11618\t10405\t10917\t9182\t9913\t9930\t10980\t9757\t10760\t11222\t9133\t9377\t10196\t10929\t11003\t9772\t8850\t312354\t3275\t3469\t3540\t3052\t2841\t2923\t3454\t2676\t3175\t3237\t3358\t3550\t2544\t3535\t2813\t3165\t3502\t2726\t3610\t2757\t3352\t3060\t3103\t3014\t3009\t3147\t2888\t3070\t3027\t2794\t4557\t98224\t481\t501\t529\t571\t467\t495\t464\t440\t551\t548\t418\t458\t339\t531\t460\t516\t442\t517\t410\t569\t397\t371\t457\t523\t544\t408\t447\t499\t-81\t13272\t8457\t8375\t8350\t7356\t8983\t8768\t8731\t8493\t8505\t7230\t7935\t7749\t8490\t7773\t8197\t7533\t7796\t8357\t7979\t8938\t7456\t7340\t7281\t9117\t8323\t8047\t8995\t7510\t7492\t8818\t10540\t254916\t8427\t7962\t8980\t8615\t8296\t9322\t9205\t8422\t7460\t9746\t9854\t7816\t9651\t7947\t9666\t7889\t8399\t8507\t8574\t9185\t7615\t7744\t7502\t7838\t9238\t9280\t9431\t8900\t7475\t9943\t258888\t7058\t7811\t6629\t7012\t6382\t7927\t6531\t7015\t7854\t7042\t6343\t6544\t7979\t6436\t6435\t7787\t6654\t6296\t6930\t6593\t8029\t7010\t7036\t6625\t7969\t7313\t8228\t7161\t6684\t6614\t11414\t223342\t19286\t18332\t16326\t17003\t17228\t19919\t16528\t17876\t17040\t19351\t17794\t18948\t19393\t19183\t16039\t19715\t19332\t18265\t20532\t16014\t16237\t18929\t18994\t19751\t18966\t20435\t18483\t20500\t18488\t11805\t546694\t21849\t22401\t24916\t23383\t22151\t25537\t24463\t24225\t23424\t24763\t27321\t22445\t20466\t22920\t21945\t22168\t24408\t24301\t25678\t20859\t24942\t25039\t22212\t26203\t26004\t25346\t22737\t23587\t24800\t21533\t18471\t730496\t32156\t28781\t31370\t29576\t32365\t33099\t32284\t35593\t30366\t29609\t35179\t34983\t34733\t34779\t31795\t32591\t31272\t35520\t29367\t33674\t28947\t30844\t31482\t33946\t34098\t36385\t29227\t36750\t35790\t34014\t15293\t995868\t62941\t51499\t53687\t57248\t61602\t53059\t53390\t63718\t59829\t65675\t59560\t56929\t59658\t54333\t64620\t53057\t59431\t55780\t65357\t62274\t59336\t62925\t58786\t64928\t55246\t66294\t57025\t65678\t59515\t26994\t1750374\t77524\t66092\t77900\t65338\t72778\t70867\t67159\t80581\t72353\t68483\t74543\t76207\t75720\t71344\t79626\t73114\t81283\t72600\t79704\t65131\t75300\t76426\t79801\t74652\t76032\t73188\t71006\t75270\t64802\t82566\t44940\t2262328\t98132\t100936\t95033\t85475\t98239\t85130\t98696\t84100\t95847\t91238\t91636\t94827\t90673\t96060\t84429\t88692\t83030\t100501\t79570\t85871\t105085\t94661\t93673\t93254\t82510\t93297\t99747\t96231\t92611\t65672\t2744856\t94355\t96451\t92263\t101843\t92806\t87930\t98408\t91212\t101238\t82711\t100298\t105552\t87863\t87542\t82461\t90133\t98519\t104115\t93901\t80346\t104634\t106959\t79038\t85504\t96582\t100635\t94135\t84745\t85101\t84142\t68833\t2860256\t131747\t111833\t113014\t115805\t112379\t118082\t132371\t107356\t112342\t127265",
                "1",
                "2019/3/1\t2019/3/2\t2019/3/3\t2019/3/4\t2019/3/5\t2019/3/6\t2019/3/7\t2019/3/8\t2019/3/9\t2019/3/10\t2019/3/11\t2019/3/12\t2019/3/13\t2019/3/14\t2019/3/15\t2019/3/16\t2019/3/17\t2019/3/18\t2019/3/19\t2019/3/20\t2019/3/21\t2019/3/22\t2019/3/23\t2019/3/24\t2019/3/25\t2019/3/26\t2019/3/27\t2019/3/28\t2019/3/29\t2019/3/30\t2019/3/31\t2019/4/1\t2019/4/2\t2019/4/3\t2019/4/4\t2019/4/5\t2019/4/6\t2019/4/7\t2019/4/8\t2019/4/9\t2019/4/10\t2019/4/11\t2019/4/12\t2019/4/13\t2019/4/14\t2019/4/15\t2019/4/16\t2019/4/17\t2019/4/18\t2019/4/19\t2019/4/20\t2019/4/21\t2019/4/22\t2019/4/23\t2019/4/24\t2019/4/25\t2019/4/26\t2019/4/27\t2019/4/28\t2019/4/29\t2019/4/30\t2019/5/1\t2019/5/2\t2019/5/3\t2019/5/4\t2019/5/5\t2019/5/6\t2019/5/7\t2019/5/8\t2019/5/9\t2019/5/10\t2019/5/11\t2019/5/12\t2019/5/13\t2019/5/14\t2019/5/15\t2019/5/16\t2019/5/17\t2019/5/18\t2019/5/19\t2019/5/20\t2019/5/21\t2019/5/22\t2019/5/23\t2019/5/24\t2019/5/25\t2019/5/26\t2019/5/27\t2019/5/28\t2019/5/29\t2019/5/30\t2019/5/31\t2019/6/1\t2019/6/2\t2019/6/3\t2019/6/4\t2019/6/5\t2019/6/6\t2019/6/7\t2019/6/8\t2019/6/9\t2019/6/10\t2019/6/11\t2019/6/12\t2019/6/13\t2019/6/14\t2019/6/15\t2019/6/16\t2019/6/17\t2019/6/18\t2019/6/19\t2019/6/20\t2019/6/21\t2019/6/22\t2019/6/23\t2019/6/24\t2019/6/25\t2019/6/26\t2019/6/27\t2019/6/28\t2019/6/29\t2019/6/30\t2019/7/1\t2019/7/2\t2019/7/3\t2019/7/4\t2019/7/5\t2019/7/6\t2019/7/7\t2019/7/8\t2019/7/9\t2019/7/10\t2019/7/11\t2019/7/12\t2019/7/13\t2019/7/14\t2019/7/15\t2019/7/16\t2019/7/17\t2019/7/18\t2019/7/19\t2019/7/20\t2019/7/21\t2019/7/22\t2019/7/23\t2019/7/24\t2019/7/25\t2019/7/26\t2019/7/27\t2019/7/28\t2019/7/29\t2019/7/30\t2019/7/31\t2019/8/1\t2019/8/2\t2019/8/3\t2019/8/4\t2019/8/5\t2019/8/6\t2019/8/7\t2019/8/8\t2019/8/9\t2019/8/10\t2019/8/11\t2019/8/12\t2019/8/13\t2019/8/14\t2019/8/15\t2019/8/16\t2019/8/17\t2019/8/18\t2019/8/19\t2019/8/20\t2019/8/21\t2019/8/22\t2019/8/23\t2019/8/24\t2019/8/25\t2019/8/26\t2019/8/27\t2019/8/28\t2019/8/29\t2019/8/30\t2019/8/31\t2019/9/1\t2019/9/2\t2019/9/3\t2019/9/4\t2019/9/5\t2019/9/6\t2019/9/7\t2019/9/8\t2019/9/9\t2019/9/10\t2019/9/11\t2019/9/12\t2019/9/13\t2019/9/14\t2019/9/15\t2019/9/16\t2019/9/17\t2019/9/18\t2019/9/19\t2019/9/20\t2019/9/21\t2019/9/22\t2019/9/23\t2019/9/24\t2019/9/25\t2019/9/26\t2019/9/27\t2019/9/28\t2019/9/29\t2019/9/30\t2019/10/1\t2019/10/2\t2019/10/3\t2019/10/4\t2019/10/5\t2019/10/6\t2019/10/7\t2019/10/8\t2019/10/9\t2019/10/10\t2019/10/11\t2019/10/12\t2019/10/13\t2019/10/14\t2019/10/15\t2019/10/16\t2019/10/17\t2019/10/18\t2019/10/19\t2019/10/20\t2019/10/21\t2019/10/22\t2019/10/23\t2019/10/24\t2019/10/25\t2019/10/26\t2019/10/27\t2019/10/28\t2019/10/29\t2019/10/30\t2019/10/31\t2019/11/1\t2019/11/2\t2019/11/3\t2019/11/4\t2019/11/5\t2019/11/6\t2019/11/7\t2019/11/8\t2019/11/9\t2019/11/10\t2019/11/11\t2019/11/12\t2019/11/13\t2019/11/14\t2019/11/15\t2019/11/16\t2019/11/17\t2019/11/18\t2019/11/19\t2019/11/20\t2019/11/21\t2019/11/22\t2019/11/23\t2019/11/24\t2019/11/25\t2019/11/26\t2019/11/27\t2019/11/28\t2019/11/29\t2019/11/30\t2019/12/1\t2019/12/2\t2019/12/3\t2019/12/4\t2019/12/5\t2019/12/6\t2019/12/7\t2019/12/8\t2019/12/9\t2019/12/10\t2019/12/11\t2019/12/12\t2019/12/13\t2019/12/14\t2019/12/15\t2019/12/16\t2019/12/17\t2019/12/18\t2019/12/19\t2019/12/20\t2019/12/21\t2019/12/22\t2019/12/23\t2019/12/24\t2019/12/25\t2019/12/26\t2019/12/27\t2019/12/28\t2019/12/29\t2019/12/30\t2019/12/31\t2020/1/1\t2020/1/2\t2020/1/3\t2020/1/4\t2020/1/5\t2020/1/6\t2020/1/7\t2020/1/8\t2020/1/9\t2020/1/10\t2020/1/11\t2020/1/12\t2020/1/13\t2020/1/14\t2020/1/15\t2020/1/16\t2020/1/17\t2020/1/18\t2020/1/19\t2020/1/20\t2020/1/21\t2020/1/22\t2020/1/23\t2020/1/24\t2020/1/25\t2020/1/26\t2020/1/27\t2020/1/28\t2020/1/29\t2020/1/30\t2020/1/31\t2020/2/1\t2020/2/2\t2020/2/3\t2020/2/4\t2020/2/5\t2020/2/6\t2020/2/7\t2020/2/8\t2020/2/9\t2020/2/10\t2020/2/11\t2020/2/12\t2020/2/13\t2020/2/14\t2020/2/15\t2020/2/16\t2020/2/17\t2020/2/18\t2020/2/19\t2020/2/20\t2020/2/21\t2020/2/22\t2020/2/23\t2020/2/24\t2020/2/25\t2020/2/26\t2020/2/27\t2020/2/28\t2020/2/29\tFeb/20\t2020/3/1\t2020/3/2\t2020/3/3\t2020/3/4\t2020/3/5\t2020/3/6\t2020/3/7\t2020/3/8\t2020/3/9\t2020/3/10\t2020/3/11\t2020/3/12\t2020/3/13\t2020/3/14\t2020/3/15\t2020/3/16\t2020/3/17\t2020/3/18\t2020/3/19\t2020/3/20\t2020/3/21\t2020/3/22\t2020/3/23\t2020/3/24\t2020/3/25\t2020/3/26\t2020/3/27\t2020/3/28\t2020/3/29\t2020/3/30\t2020/3/31\t2020/4/1\t2020/4/2\t2020/4/3\t2020/4/4\t2020/4/5\t2020/4/6\t2020/4/7\t2020/4/8\t2020/4/9\t2020/4/10\t2020/4/11\t2020/4/12\t2020/4/13\t2020/4/14\t2020/4/15\t2020/4/16\t2020/4/17\t2020/4/18\t2020/4/19\t2020/4/20\t2020/4/21\t2020/4/22\t2020/4/23\t2020/4/24\t2020/4/25\t2020/4/26\t2020/4/27\t2020/4/28\t2020/4/29\t2020/4/30\t2020/5/1\t2020/5/2\t2020/5/3\t2020/5/4\t2020/5/5\t2020/5/6\t2020/5/7\t2020/5/8\t2020/5/9\t2020/5/10\t2020/5/11\t2020/5/12\t2020/5/13\t2020/5/14\t2020/5/15\t2020/5/16\t2020/5/17\t2020/5/18\t2020/5/19\t2020/5/20\t2020/5/21\t2020/5/22\t2020/5/23\t2020/5/24\t2020/5/25\t2020/5/26\t2020/5/27\t2020/5/28\t2020/5/29\t2020/5/30\t2020/5/31\t2020/6/1\t2020/6/2\t2020/6/3\t2020/6/4\t2020/6/5\t2020/6/6\t2020/6/7\t2020/6/8\t2020/6/9\t2020/6/10\t2020/6/11\t2020/6/12\t2020/6/13\t2020/6/14\t2020/6/15\t2020/6/16\t2020/6/17\t2020/6/18\t2020/6/19\t2020/6/20\t2020/6/21\t2020/6/22\t2020/6/23\t2020/6/24\t2020/6/25\t2020/6/26\t2020/6/27\t2020/6/28\t2020/6/29\t2020/6/30\t2020/7/1\t2020/7/2\t2020/7/3\t2020/7/4\t2020/7/5\t2020/7/6\t2020/7/7\t2020/7/8\t2020/7/9\t2020/7/10\t2020/7/11\t2020/7/12\t2020/7/13\t2020/7/14\t2020/7/15\t2020/7/16\t2020/7/17\t2020/7/18\t2020/7/19\t2020/7/20\t2020/7/21\t2020/7/22\t2020/7/23\t2020/7/24\t2020/7/25\t2020/7/26\t2020/7/27\t2020/7/28\t2020/7/29\t2020/7/30\t2020/7/31\t2020/8/1\t2020/8/2\t2020/8/3\t2020/8/4\t2020/8/5\t2020/8/6\t2020/8/7\t2020/8/8\t2020/8/9\t2020/8/10\t2020/8/11\t2020/8/12\t2020/8/13\t2020/8/14\t2020/8/15\t2020/8/16\t2020/8/17\t2020/8/18\t2020/8/19\t2020/8/20\t2020/8/21\t2020/8/22\t2020/8/23\t2020/8/24\t2020/8/25\t2020/8/26\t2020/8/27\t2020/8/28\t2020/8/29\t2020/8/30\t2020/8/31\t2020/9/1\t2020/9/2\t2020/9/3\t2020/9/4\t2020/9/5\t2020/9/6\t2020/9/7\t2020/9/8\t2020/9/9\t2020/9/10\t2020/9/11\t2020/9/12\t2020/9/13\t2020/9/14\t2020/9/15\t2020/9/16\t2020/9/17\t2020/9/18\t2020/9/19\t2020/9/20\t2020/9/21\t2020/9/22\t2020/9/23\t2020/9/24\t2020/9/25\t2020/9/26\t2020/9/27\t2020/9/28\t2020/9/29\t2020/9/30\t2020/10/1\t2020/10/2\t2020/10/3\t2020/10/4\t2020/10/5\t2020/10/6\t2020/10/7\t2020/10/8\t2020/10/9\t2020/10/10\t2020/10/11\t2020/10/12\t2020/10/13\t2020/10/14\t2020/10/15\t2020/10/16\t2020/10/17\t2020/10/18\t2020/10/19\t2020/10/20\t2020/10/21\t2020/10/22\t2020/10/23\t2020/10/24\t2020/10/25\t2020/10/26\t2020/10/27\t2020/10/28\t2020/10/29\t2020/10/30\t2020/10/31\t2020/11/1\t2020/11/2\t2020/11/3\t2020/11/4\t2020/11/5\t2020/11/6\t2020/11/7\t2020/11/8\t2020/11/9\t2020/11/10\t2020/11/11\t2020/11/12\t2020/11/13\t2020/11/14\t2020/11/15\t2020/11/16\t2020/11/17\t2020/11/18\t2020/11/19\t2020/11/20\t2020/11/21\t2020/11/22\t2020/11/23\t2020/11/24\t2020/11/25\t2020/11/26\t2020/11/27\t2020/11/28\t2020/11/29\t2020/11/30\t2020/12/1\t2020/12/2\t2020/12/3\t2020/12/4\t2020/12/5\t2020/12/6\t2020/12/7\t2020/12/8\t2020/12/9\t2020/12/10\t2020/12/11\t2020/12/12\t2020/12/13\t2020/12/14\t2020/12/15\t2020/12/16\t2020/12/17\t2020/12/18\t2020/12/19\t2020/12/20\t2020/12/21\t2020/12/22\t2020/12/23\t2020/12/24\t2020/12/25\t2020/12/26\t2020/12/27\t2020/12/28\t2020/12/29\t2020/12/30\t2020/12/31\t2021/1/1\t2021/1/2\t2021/1/3\t2021/1/4\t2021/1/5\t2021/1/6\t2021/1/7\t2021/1/8\t2021/1/9\t2021/1/10\t2021/1/11\t2021/1/12\t2021/1/13\t2021/1/14\t2021/1/15\t2021/1/16\t2021/1/17\t2021/1/18\t2021/1/19\t2021/1/20\t2021/1/21\t2021/1/22\t2021/1/23\t2021/1/24\t2021/1/25\t2021/1/26\t2021/1/27\t2021/1/28\t2021/1/29\t2021/1/30\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByAccurateDate(tableName, fieldNames, values));

    }

    /**
     * 累计维度
     */
    public static void getAnchorData20And21Total() {

        //新增注册维度
        String tableName = "show_anchor_data";
        String[] fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        String[] values = new String[]{
                "2 \t1 \t6 \t0 \t6 \t5 \t4 \t1 \t4 \t6 \t0 \t6 \t6 \t2 \t4 \t0 \t5 \t1 \t3 \t3 \t3 \t4 \t3 \t1 \t2 \t7 \t3 \t1 \t0 \t1 \t2",
                "72 \t248 \t17 \t271 \t2 \t136 \t184 \t230 \t61 \t86 \t9 \t173 \t182 \t173 \t122 \t301 \t297 \t285 \t123 \t302 \t232 \t253 \t73 \t318 \t181 \t220 \t30 \t118 \t108 \t32 \t19",
                "874 \t1118 \t1361 \t1141 \t515 \t1213 \t406 \t1359 \t366 \t123 \t930 \t1089 \t957 \t1 \t1230 \t1715 \t88 \t1584 \t1347 \t1814 \t434 \t476 \t345 \t908 \t768 \t20 \t1567 \t728 \t1007 \t691 \t842",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 1, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t0 \t4 \t0 \t6 \t6 \t6 \t5 \t1 \t7 \t1 \t8 \t6 \t5 \t1 \t3 \t4 \t2 \t0 \t7 \t6 \t5 \t6 \t6 \t5 \t1 \t1 \t6 \t0 \t0 \t0",
                "293 \t191 \t143 \t283 \t56 \t325 \t247 \t300 \t14 \t2 \t195 \t382 \t77 \t62 \t215 \t20 \t125 \t246 \t66 \t284 \t242 \t351 \t264 \t30 \t388 \t4 \t1 \t214 \t0 \t0 \t0",
                "1359 \t806 \t1206 \t833 \t1305 \t1672 \t409 \t835 \t1277 \t1039 \t1187 \t874 \t434 \t1781 \t525 \t914 \t722 \t1337 \t222 \t922 \t1518 \t1390 \t1363 \t949 \t858 \t1039 \t987 \t1209 \t0 \t0 \t0",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 2, tableName, fieldNames, values));


        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "2 \t1 \t1 \t9 \t8 \t7 \t1 \t8 \t5 \t3 \t6 \t7 \t8 \t2 \t7 \t7 \t3 \t2 \t1 \t6 \t4 \t1 \t2 \t6 \t6 \t8 \t0 \t7 \t7 \t5 \t5",
                "243 \t316 \t186 \t152 \t143 \t372 \t376 \t326 \t157 \t247 \t80 \t163 \t5 \t49 \t68 \t143 \t275 \t33 \t197 \t175 \t113 \t356 \t33 \t147 \t344 \t352 \t68 \t286 \t245 \t90 \t30",
                "557 \t897 \t246 \t1817 \t892 \t1784 \t1398 \t1803 \t1701 \t813 \t85 \t936 \t1434 \t95 \t302 \t250 \t1363 \t1497 \t1181 \t339 \t1565 \t366 \t1295 \t1460 \t1189 \t379 \t1582 \t1372 \t833 \t151 \t1054",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 3, tableName, fieldNames, values));


        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "5 \t5 \t7 \t2 \t1 \t7 \t9 \t0 \t1 \t0 \t5 \t0 \t3 \t8 \t8 \t3 \t8 \t7 \t7 \t4 \t1 \t8 \t8 \t0 \t8 \t0 \t7 \t2 \t4 \t5 \t0",
                "140 \t130 \t223 \t68 \t76 \t194 \t201 \t98 \t417 \t46 \t231 \t161 \t390 \t23 \t188 \t150 \t292 \t420 \t36 \t373 \t53 \t164 \t318 \t194 \t75 \t294 \t382 \t274 \t161 \t36 \t0",
                "845 \t203 \t1428 \t513 \t143 \t1575 \t1355 \t1772 \t1621 \t2103 \t1986 \t1351 \t1953 \t395 \t1031 \t1052 \t2101 \t11 \t330 \t2074 \t1522 \t80 \t478 \t1827 \t147 \t93 \t1627 \t1449 \t1463 \t1229 \t0",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 4, tableName, fieldNames, values));


        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "6 \t4 \t9 \t1 \t8 \t8 \t2 \t4 \t1 \t3 \t5 \t2 \t4 \t6 \t3 \t1 \t6 \t4 \t5 \t6 \t4 \t5 \t6 \t5 \t5 \t8 \t6 \t0 \t3 \t4 \t7",
                "25 \t49 \t145 \t98 \t121 \t344 \t292 \t379 \t259 \t101 \t143 \t39 \t132 \t311 \t274 \t368 \t322 \t245 \t165 \t135 \t255 \t195 \t307 \t399 \t195 \t360 \t134 \t116 \t98 \t262 \t316",
                "51 \t987 \t1477 \t1576 \t534 \t168 \t1374 \t980 \t1795 \t408 \t1376 \t1269 \t1478 \t1693 \t1558 \t98 \t242 \t1441 \t2126 \t2277 \t1838 \t2550 \t1088 \t1287 \t1181 \t721 \t386 \t2235 \t697 \t269 \t2172",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 5, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t12 \t0 \t9 \t8 \t0 \t1 \t11 \t11 \t9 \t2 \t6 \t6 \t4 \t12 \t9 \t5 \t5 \t13 \t1 \t5 \t2 \t4 \t13 \t8 \t10 \t6 \t7 \t12 \t7 \t0",
                "426 \t614 \t187 \t4 \t133 \t42 \t585 \t560 \t188 \t458 \t343 \t178 \t298 \t28 \t18 \t140 \t190 \t3 \t487 \t123 \t390 \t158 \t438 \t397 \t1 \t531 \t461 \t453 \t369 \t533 \t0",
                "641 \t1312 \t2680 \t215 \t878 \t247 \t325 \t2197 \t1252 \t468 \t1794 \t306 \t1066 \t1212 \t2501 \t334 \t389 \t553 \t1507 \t1236 \t1408 \t2180 \t2323 \t2677 \t601 \t2980 \t2320 \t770 \t1170 \t1448 \t0",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 6, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t12 \t0 \t9 \t8 \t0 \t1 \t11 \t11 \t9 \t2 \t6 \t6 \t4 \t12 \t9 \t5 \t5 \t13 \t1 \t5 \t2 \t4 \t13 \t8 \t10 \t6 \t7 \t12 \t7 \t0",
                "187 \t616 \t236 \t449 \t540 \t211 \t320 \t444 \t317 \t329 \t195 \t115 \t612 \t171 \t678 \t122 \t171 \t192 \t293 \t225 \t282 \t642 \t629 \t253 \t79 \t593 \t331 \t197 \t266 \t18 \t122",
                "1195 \t2645 \t1241 \t786 \t2661 \t2520 \t2848 \t2773 \t326 \t406 \t1400 \t69 \t334 \t1124 \t1765 \t2793 \t2423 \t254 \t115 \t56 \t1871 \t642 \t812 \t2200 \t2468 \t341 \t98 \t1798 \t1067 \t1774 \t2762",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 7, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "12 \t4 \t11 \t11 \t5 \t12 \t11 \t11 \t5 \t8 \t6 \t9 \t6 \t1 \t3 \t12 \t8 \t2 \t3 \t11 \t10 \t9 \t1 \t11 \t1 \t7 \t7 \t1 \t1 \t9 \t1",
                "68 \t489 \t172 \t955 \t433 \t741 \t47 \t546 \t480 \t337 \t717 \t397 \t234 \t110 \t576 \t849 \t623 \t225 \t581 \t150 \t457 \t857 \t273 \t146 \t172 \t325 \t588 \t450 \t415 \t300 \t135",
                "503 \t295 \t2580 \t2571 \t2313 \t1039 \t1609 \t665 \t837 \t2630 \t878 \t2951 \t41 \t1903 \t2024 \t2960 \t2500 \t1254 \t1816 \t189 \t33 \t19 \t2971 \t1677 \t1844 \t2634 \t61 \t751 \t2852 \t462 \t851",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 8, tableName, fieldNames, values));


        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "7 \t14 \t8 \t2 \t9 \t1 \t10 \t6 \t10 \t6 \t12 \t12 \t3 \t1 \t2 \t1 \t15 \t14 \t9 \t6 \t8 \t0 \t0 \t11 \t4 \t11 \t10 \t13 \t10 \t12 \t0",
                "986 \t206 \t725 \t602 \t72 \t223 \t144 \t181 \t37 \t487 \t502 \t629 \t283 \t899 \t777 \t767 \t385 \t644 \t760 \t26 \t398 \t454 \t689 \t778 \t720 \t130 \t127 \t521 \t794 \t899 \t0",
                "2173 \t1384 \t1497 \t1526 \t2696 \t2507 \t3206 \t2330 \t3004 \t1359 \t2669 \t2082 \t2175 \t41 \t139 \t2828 \t2818 \t136 \t1210 \t2744 \t24 \t273 \t359 \t2148 \t171 \t4 \t1931 \t2579 \t2554 \t1506 \t0",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 9, tableName, fieldNames, values));


        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "1 \t13 \t15 \t12 \t11 \t13 \t3 \t7 \t6 \t0 \t16 \t5 \t9 \t15 \t10 \t11 \t12 \t7 \t6 \t7 \t8 \t3 \t1 \t1 \t9 \t8 \t9 \t6 \t2 \t3 \t15",
                "126 \t448 \t912 \t397 \t930 \t467 \t314 \t951 \t385 \t692 \t243 \t242 \t756 \t689 \t575 \t349 \t15 \t696 \t720 \t1052 \t466 \t189 \t942 \t683 \t349 \t76 \t761 \t468 \t116 \t204 \t713",
                "1035 \t3012 \t396 \t10 \t2747 \t1021 \t785 \t3638 \t267 \t1259 \t4 \t2306 \t529 \t3722 \t2551 \t2651 \t3433 \t2097 \t2783 \t529 \t2513 \t2798 \t2791 \t117 \t2359 \t2371 \t329 \t2384 \t3234 \t1698 \t905",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 10, tableName, fieldNames, values));


        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "2 \t19 \t9 \t1 \t11 \t5 \t7 \t6 \t2 \t2 \t2 \t2 \t13 \t15 \t15 \t10 \t8 \t15 \t18 \t16 \t13 \t7 \t11 \t9 \t13 \t5 \t6 \t2 \t12 \t9 \t0",
                "433 \t728 \t235 \t393 \t1184 \t454 \t241 \t839 \t311 \t341 \t1318 \t1337 \t439 \t467 \t115 \t934 \t1143 \t1193 \t1054 \t280 \t257 \t1195 \t241 \t429 \t1041 \t1091 \t269 \t839 \t766 \t268 \t0",
                "3892 \t343 \t1389 \t4164 \t3985 \t3551 \t1842 \t1748 \t2877 \t913 \t3710 \t983 \t1124 \t1728 \t2077 \t3555 \t905 \t602 \t2272 \t1208 \t3192 \t1467 \t3513 \t3150 \t1836 \t782 \t475 \t794 \t1365 \t796 \t0",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 11, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "16 \t19 \t14 \t17 \t12 \t9 \t18 \t14 \t6 \t4 \t13 \t18 \t19 \t0 \t11 \t1 \t1 \t1 \t1 \t1 \t1 \t11 \t10 \t1 \t16 \t16 \t1 \t15 \t14 \t17 \t1",
                "1352 \t664 \t1551 \t5 \t334 \t1510 \t153 \t48 \t1281 \t1269 \t1268 \t629 \t958 \t280 \t1182 \t405 \t158 \t10 \t730 \t1480 \t987 \t1615 \t963 \t427 \t201 \t450 \t1398 \t680 \t732 \t225 \t652",
                "3569 \t2386 \t1451 \t1709 \t1239 \t3011 \t1144 \t749 \t2804 \t1862 \t2280 \t2123 \t149 \t1283 \t4107 \t3722 \t2144 \t1521 \t2125 \t1751 \t203 \t708 \t2911 \t3851 \t3646 \t344 \t573 \t3853 \t3593 \t2179 \t1159",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2020, 12, tableName, fieldNames, values));

        fieldNames = new String[]{"premium_amount", "high_quality_amount", "active_amount", "type", "create_date"};
        values = new String[]{
                "3 \t20 \t1 \t2 \t5 \t9 \t12 \t11 \t2 \t14 \t4 \t13 \t3 \t1 \t1 \t6 \t19 \t16 \t20 \t12 \t6 \t4 \t7 \t9 \t20 \t20 \t16 \t20 \t15 \t13 \t3",
                "1016 \t941 \t447 \t1430 \t872 \t941 \t967 \t760 \t1407 \t127 \t1435 \t1082 \t387 \t839 \t1084 \t324 \t1246 \t1452 \t939 \t633 \t121 \t11 \t699 \t1532 \t997 \t1170 \t1043 \t1292 \t336 \t290 \t48",
                "598 \t1180 \t45 \t1953 \t4097 \t296 \t2740 \t3288 \t120 \t2922 \t3742 \t1087 \t1046 \t3257 \t4400 \t1466 \t2284 \t3249 \t1767 \t2020 \t4183 \t2130 \t1364 \t2660 \t1451 \t2109 \t1354 \t3772 \t3175 \t743 \t2582",
                "1",
                "1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25 \t26 \t27 \t28 \t29 \t30 \t31",
        };

        printSql(ExcelSqlUtil.generateInsertSqlByTimeCondition(2021, 1, tableName, fieldNames, values));


    }

    /*
        遗留：
            商家端 - 商家留存(代码写死) ， 榜单看板(没数据)
            主播端 - show_anchor_data 各平台主播数量(手动计算)
                  - show_anchor_live_data 开播看板 ：主播开播数(数据缺失)、总寄样开播率(数据缺失)
            运营端 - 以下缺失数据都需要日维度数据：客单价(只看到月维度)，累计商品数，累计品牌数、动销活跃sku数、交易金额
                  - 不同商品分类的交易额、不同等级主播的交易额(饼图和柱图，需要日维度还是月维度？)
     */

}
