package cn.link.excel.easyexcel;

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
     * @param tableName
     * @param fieldNames 字段和数据的顺序必须一致
     * @param values     数据之间顺序必须一致
     * @return
     */
    public static List<String> generateInsertSql(String tableName, String[] fieldNames, String[] values) {

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

    public static void printSql(List<String> sqlList) {

        for (String sql : sqlList) {
            System.out.println(sql);
        }

    }

    public static void main(String[] args) {

        skuCategory();
        //trailAmount();

    }

    /**
     * 商家看板 商品数量 / 选中率(暂缺平台商家数量)
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

        printSql(ExcelSqlUtil.generateInsertSql(tableName, fieldNames, values));

        tableName = "show_seller_data";
        fieldNames = new String[]{"product_amount", "selected_rate", "category_name", "create_date"};
        values = new String[]{
                "31\t361\t591\t828\t1362\t2546\t3273\t3713\t4973\t6520\t7972\t9632\t12554\t15399\t14485\t15332\t15550\t17241\t20770\t22778\t24685\t27919\t19759\t22525\t23709",
                "0.226 \t0.227 \t0.210 \t0.208 \t0.248 \t0.230 \t0.211 \t0.210 \t0.214 \t0.225 \t0.240 \t0.243 \t0.206 \t0.215 \t0.214 \t0.237 \t0.219 \t0.223 \t0.204 \t0.220 \t0.244 \t0.221 \t0.221 \t0.248 \t0.190",
                "日用百货",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/1\n",
        };

        printSql(ExcelSqlUtil.generateInsertSql(tableName, fieldNames, values));

        tableName = "show_seller_data";
        fieldNames = new String[]{"product_amount", "selected_rate", "category_name", "create_date"};
        values = new String[]{
                "21\t277\t322\t422\t833\t1165\t1425\t2225\t3316\t3260\t5646\t6822\t6757\t8553\t11191\t11682\t13999\t15646\t15577\t17311\t15191\t16618\t17963\t18214\t19563",
                "0.286 \t0.184 \t0.180 \t0.192 \t0.187 \t0.182 \t0.183 \t0.199 \t0.197 \t0.199 \t0.197 \t0.195 \t0.190 \t0.194 \t0.191 \t0.188 \t0.196 \t0.194 \t0.188 \t0.198 \t0.190 \t0.181 \t0.198 \t0.193 \t0.141",
                "美妆",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/1\n",
        };

        printSql(ExcelSqlUtil.generateInsertSql(tableName, fieldNames, values));

        tableName = "show_seller_data";
        fieldNames = new String[]{"product_amount", "selected_rate", "category_name", "create_date"};
        values = new String[]{
                "23\t251\t418\t594\t1069\t2015\t2528\t2826\t4474\t4925\t3374\t5438\t7237\t6365\t6584\t7520\t9555\t9031\t10038\t12345\t9494\t10968\t12479\t12871\t13554",
                "0.217 \t0.223 \t0.220 \t0.205 \t0.203 \t0.222 \t0.214 \t0.223 \t0.209 \t0.224 \t0.221 \t0.206 \t0.229 \t0.217 \t0.200 \t0.205 \t0.219 \t0.211 \t0.217 \t0.214 \t0.203 \t0.205 \t0.226 \t0.221 \t0.169",
                "家居家纺",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/1\n",
        };

        printSql(ExcelSqlUtil.generateInsertSql(tableName, fieldNames, values));

        tableName = "show_seller_data";
        fieldNames = new String[]{"product_amount", "selected_rate", "category_name", "create_date"};
        values = new String[]{
                "29\t251\t344\t330\t474\t787\t849\t1243\t1492\t2631\t2624\t3567\t3471\t3827\t5985\t5052\t5909\t3144\t4197\t3644\t3750\t2991\t3119\t3217\t3485",
                "0.217 \t0.223 \t0.220 \t0.205 \t0.203 \t0.222 \t0.214 \t0.223 \t0.209 \t0.224 \t0.221 \t0.206 \t0.229 \t0.217 \t0.200 \t0.205 \t0.219 \t0.211 \t0.217 \t0.214 \t0.203 \t0.205 \t0.226 \t0.221 \t0.169",
                "服饰穿搭",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/1\n",
        };

        printSql(ExcelSqlUtil.generateInsertSql(tableName, fieldNames, values));

        tableName = "show_seller_data";
        fieldNames = new String[]{"product_amount", "selected_rate", "category_name", "create_date"};
        values = new String[]{
                "25\t169\t342\t431\t832\t1528\t3128\t4542\t4148\t5504\t6090\t4706\t8016\t10287\t11176\t16066\t13914\t20511\t17177\t13011\t20997\t19771\t27966\t27777\t29334",
                "0.200 \t0.231 \t0.213 \t0.258 \t0.213 \t0.229 \t0.218 \t0.249 \t0.282 \t0.221 \t0.264 \t0.219 \t0.227 \t0.205 \t0.229 \t0.212 \t0.226 \t0.250 \t0.262 \t0.210 \t0.235 \t0.275 \t0.227 \t0.232 \t0.174",
                "其他",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/1\n",
        };

        printSql(ExcelSqlUtil.generateInsertSql(tableName, fieldNames, values));

    }

    /**
     * 商家 - 申样金额，拼量承担样品费
     */
    public static void trailAmount() {

        String tableName = "show_trail_amount_data";
        String[] fieldNames = new String[]{"pl_afforded_amount", "trail_product_amount", "create_date"};
        String[] values = new String[]{
                "623590.52\t177571.26\t787577.64\t1141376.23\t2212468.19\t2036978.39\t2493424.96\t4089603.57\t4827273.29\t5168983.62\t9155426.32\t7028448.01\t4396207.32\t1702360.9\t3337899.6878\t2571137.25\t3047367.578\t5944355.9\t7221479.22\t9744117.9\t6196366.14\t9536212.87\t21091644.452\t19698122.5\t12181038.58",
                "750977.51\t328556.8\t1077443\t2051652.09\t4184250\t5122981.14\t6201273.6\t7898020.01\t10052520\t12850560\t27852371.91\t21814472.61\t11546198.08\t2830616.47\t8871167.39\t9641772.75\t10059918.52\t22011765.08\t25294274.95\t30662442.89\t40012376.53\t47215136.74\t79801090.23\t81533423.33\t54265768.27",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSql(tableName, fieldNames, values));

    }

    /**
     * 商家 - 申请寄样数/金额
     */
    public static void trailProductAmount() {

        String tableName = "show_seller_trail_data";
        String[] fieldNames = new String[]{"name", "trail_amount", "trail_product_amount", "type", "create_date"};
        String[] values = new String[]{
                "食品",
                "5491\t2568\t7802\t15293\t30001\t36865\t41744\t54250\t70650\t94593\t195748\t170348\t79508\t18985\t59717\t66228\t78049\t178732\t165200\t222028\t298252\t313369\t521691\t598103\t318567",
                "232052.1\t101524.1\t329697.6\t646270.4\t1267827.8\t1583001.2\t1934797.4\t2440488.2\t2985598.4\t3739513.0\t8272154.5\t7198776.0\t3359943.6\t815217.5\t2767804.2\t2979307.8\t3138694.6\t7065776.6\t6981219.9\t9382707.5\t12603898.6\t13456314.0\t24179730.3\t26906029.7\t12810964.2",
                "1",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSql(tableName, fieldNames, values));

        tableName = "show_seller_trail_data";
        fieldNames = new String[]{"name", "trail_amount", "trail_product_amount", "type", "create_date"};
        values = new String[]{
                "日用百货",
                "4108\t1501\t5491\t12185\t26501\t25784\t36029\t38043\t51694\t62536\t165604\t140821\t73832\t16724\t45336\t45484\t59427\t118048\t148961\t193980\t243675\t247967\t407825\t445357\t304526",
                "165215.1\t66499.9\t213333.7\t415254.4\t957356.4\t1036891.4\t1473422.6\t1685437.5\t2078861.1\t2770580.7\t6433897.9\t4799184.0\t2667171.8\t672554.5\t1854074.0\t2015130.5\t2146786.6\t5229995.4\t5787330.1\t6610822.7\t8802722.8\t9971836.9\t16678427.9\t19731088.4\t11000950.4",
                "1",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSql(tableName, fieldNames, values));

        tableName = "show_seller_trail_data";
        fieldNames = new String[]{"name", "trail_amount", "trail_product_amount", "type", "create_date"};
        values = new String[]{
                "日用百货",
                "4108\t1501\t5491\t12185\t26501\t25784\t36029\t38043\t51694\t62536\t165604\t140821\t73832\t16724\t45336\t45484\t59427\t118048\t148961\t193980\t243675\t247967\t407825\t445357\t304526",
                "165215.1\t66499.9\t213333.7\t415254.4\t957356.4\t1036891.4\t1473422.6\t1685437.5\t2078861.1\t2770580.7\t6433897.9\t4799184.0\t2667171.8\t672554.5\t1854074.0\t2015130.5\t2146786.6\t5229995.4\t5787330.1\t6610822.7\t8802722.8\t9971836.9\t16678427.9\t19731088.4\t11000950.4",
                "1",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSql(tableName, fieldNames, values));

        tableName = "show_seller_trail_data";
        fieldNames = new String[]{"name", "trail_amount", "trail_product_amount", "type", "create_date"};
        values = new String[]{
                "美妆",
                "4108\t1501\t5491\t12185\t26501\t25784\t36029\t38043\t51694\t62536\t165604\t140821\t73832\t16724\t45336\t45484\t59427\t118048\t148961\t193980\t243675\t247967\t407825\t445357\t304526",
                "165215.1\t66499.9\t213333.7\t415254.4\t957356.4\t1036891.4\t1473422.6\t1685437.5\t2078861.1\t2770580.7\t6433897.9\t4799184.0\t2667171.8\t672554.5\t1854074.0\t2015130.5\t2146786.6\t5229995.4\t5787330.1\t6610822.7\t8802722.8\t9971836.9\t16678427.9\t19731088.4\t11000950.4",
                "1",
                "2019/1/31\t2019/2/28\t2019/3/31\t2019/4/30\t2019/5/31\t2019/6/30\t2019/7/31\t2019/8/31\t2019/9/30\t2019/10/31\t2019/11/30\t2019/12/31\t2020/1/31\t2020/2/29\t2020/3/31\t2020/4/30\t2020/5/31\t2020/6/30\t2020/7/31\t2020/8/31\t2020/9/30\t2020/10/31\t2020/11/30\t2020/12/31\t2021/1/31",
        };

        printSql(ExcelSqlUtil.generateInsertSql(tableName, fieldNames, values));

    }

}
