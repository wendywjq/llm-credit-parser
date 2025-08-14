/**
 *
 */
package headers;


import com.shuoen.varshow.shvar.beans.LoanInfo;
import com.shuoen.varshow.shvar.beans.PbccrcReportEntity;
import com.shuoen.varshow.shvar.vars.params.CalcAcctType;
import com.shuoen.varshow.shvar.vars.params.StatType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dgod
 */
public class Utils {


    /**
     * 日期时间格式12小时制:MMDDhhmmss
     */
    public static String DF_MMDDhhmmss = "MMddhhmmss";
    public static String DF_yyyyMMddHHmmss = "yyyyMMddHHmmss";

    public static String CY_yyyyMMdd = "yyyyMMdd";
    public static String CY_yyyyMM = "yyyyMM";
    public static String CY_LINE_yyyyMMdd = "yyyy-MM-dd";
    public static String CY_LINE_yyyyMM = "yyyy-MM";
    public static String CY_POINT_yyyyMM = "yyyy.MM";


    public static String dateFormat(String dateString, String formatFrom, String formatTo) {
        if (dateString == null)
            return null;
        try {
            Date date = parseDateTime(formatFrom, dateString);
            return formatDateTime(formatTo, date);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * @param d1
     * @param d2
     * @return d2-d1的差值
     */
    public static Integer getDeltaDateInDays(Date d1, Date d2) {


        return Long.valueOf(getTimeSpan(d1, d2)[0]).intValue();
    }

    public static Integer getRepayInfoIntStatus(String n) {
        if(checkByPattern(n,"[\\d]")) {
            return Integer.parseInt(n);
        }else if ("B".equals(n)) {
            return 8;
        }else {
            return 0;
        }

    }

    public static int diffInYear(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return 0;

        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(startDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);

        return diffYear;
    }

    public static Double getDbtcrAcbaByLoanInfoIsNull(LoanInfo info) {
        Double result = null;
        if (info.getBalance() != null) {
            result = info.getBalance();
        } else if (info.getLatest_Pay_Info() != null && info.getLatest_Pay_Info().getLatest_Balance() != null) {
            result = info.getLatest_Pay_Info().getLatest_Balance().doubleValue();
        }
        return result;
    }


    /**
     * 返回指定的天数
     */
    public static Integer calcDayCount(int monthCountStd) {
        switch (monthCountStd) {
            case 1:
                return 30;
            case 3:
                return 90;
            case 6:
                return 180;
            case 9:
                return 270;
            case 12:
                return 356;
            case 18:
                return 547;
            case 24:
                return 730;
            case 36:
                return 1095;
            default:
                return 0;
        }
    }

    /**
     * @param p1
     * @param number
     * @return 四舍五入保留小数点后几位
     */
    public static Double resultDouble(Double p1, Integer number) {
        BigDecimal num = new BigDecimal(p1);

        return
                num.setScale(number, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double getDoubleByType(StatType statType, Stream<Double> payStream) {
        double result = 0;
        if (payStream == null || statType == null) return Consts.NO_REQUIRED_DATA_DOUBLE;

        List<Double> statStream = payStream.collect(Collectors.toList());
        if (Utils.isEmptyList(statStream)) return Consts.NO_REQUIRED_DATA_DOUBLE;

        switch (statType) {
            case MAX:
                result = statStream.stream().max(Comparator.comparing(Double::valueOf)).orElse(0d);
                break;
            case MIN:
                result = statStream.stream().min(Comparator.comparing(Double::valueOf)).orElse(0d);
                break;
            case AVG:
                result = statStream.stream().mapToDouble(Double::doubleValue).average().orElse(0d);
                result = round(result, 6);
                break;
            case SUM:
                result = statStream.stream().mapToDouble(Double::doubleValue).sum();
                break;
            default:
                return Consts.NO_REQUIRED_DATA_DOUBLE;
        }
        return result;
    }

    /**
     * 计算百分比
     *
     * @param n1 分子
     * @param n2 分母
     * @return
     */
    public static Double calcPercentage(Long n1, Long n2) {
        return new BigDecimal(n1).divide(new BigDecimal(n2), 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue();
    }

    /**
     * 获取两个时间中的每一天
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<String> getPerDay(String startTime, String endTime) {
        //定义一个接收时间的集合
        List<String> result = new ArrayList<String>();
        Date start = parseDateTime(CY_yyyyMMdd, startTime);
        Date end = parseDateTime(CY_yyyyMMdd, endTime);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(start);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(end);
        // 测试此日期是否在指定日期之后
        while (end.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            result.add(formatDateTime(CY_yyyyMMdd, calBegin.getTime()));
        }
        if (!result.contains(startTime)) {
            result.add(startTime);
        }
        if (!result.contains(endTime)) {
            result.add(endTime);
        }
        return result;
    }

    /**
     * 将 Date 转为 LocalDate
     *
     * @param date
     * @return java.time.LocalDate;
     */
    public static LocalDate dateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String getLastMonthDate1(int lastMonths, Date reportDate) {
        return reportDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .minusMonths(lastMonths)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static String addressResolution2(String address, String levelType) {
        Map<String, Map<String, List<String>>> dic = AddressUtils.getAddressMap();
        String result = "";
        Map<String, List<String>> dicMap = new HashMap<>();
        List<String> provinceList = new ArrayList<>();
        List<String> cityList = new ArrayList<>();
        List<String> areaList = new ArrayList<>();
        if (dic != null) {
            for (Map.Entry<String, Map<String, List<String>>> proEntry : dic.entrySet()) {
                provinceList.add(proEntry.getKey());
                cityList.addAll(proEntry.getValue().get("city"));
                areaList.addAll(proEntry.getValue().get("area"));
            }
        }
        dicMap.put("province", provinceList);
        dicMap.put("city", cityList);
        dicMap.put("area", areaList);

        if (!isEmpty(address)) {
            String province = "";
            for (String pro : dicMap.get("province")) {
                if (address.contains(pro)) {
                    province = pro;
                    break;
                }
            }

            if ("province".equals(levelType)) {
                result = province;
            } else if ("city".equals(levelType)) {
                List<String> tempCityList = new ArrayList<>();
                tempCityList = (!"".equals(province) && dic != null && dic.get(province) != null) ?
                        dic.get(province).get("city") : dicMap.get("city");
                for (String city : tempCityList) {
                    String substring = city.substring(0, 2);
                    if (address.contains(substring)) {
                        String[] municipality = {"北京市", "上海市", "天津市", "重庆市"};
                        if (Arrays.asList(municipality).contains(substring)) result = province;
                        else result = substring;
                        break;
                    }
                }
            } else {
                List<String> tempAreaList = new ArrayList<>();
                tempAreaList = (!"".equals(province) && dic != null && dic.get(province) != null) ?
                        dic.get(province).get("area") : dicMap.get("area");
                for (String area : tempAreaList) {
                    if (address.contains(area)) {
                        result = area;
                        break;
                    }
                }
            }
        }
        return result;

    }

    public static Integer getDeltaDateInMonths(String start, String end, String format) {

        if (start == null || end == null) return 0;

        Date startDate = parseDateTime(format, start);
        Date endDate = parseDateTime(format, end);

        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(startDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

        return diffMonth;
    }


    public static Integer S00319NUM(PbccrcReportEntity report) {
        if (report == null || report.getCredit_Transaction_Info() == null || Utils.isEmptyList(report.getCredit_Transaction_Info().getLoan_Info()))
            return 0;
        return 1;
    }

    public static Integer S00320NUM(PbccrcReportEntity report) {
        if (report == null || report.getSummary_Info() == null || report.getSummary_Info().getCreditTran_Credit_Debt_Info() == null ||
                report.getSummary_Info().getCreditTran_Credit_Debt_Info().getQuassiCC_Info_Summary() == null) return 0;
        return 1;
    }

    public static Map<String, Object> mapper(Object[] data, String[] keys) {
        Map<String, Object> ret = new HashMap<String, Object>();
        for (int i = 0; i < keys.length; i++) {
            if (i < data.length) {
                ret.put(keys[i], data[i]);
            } else {
                ret.put(keys[i], null);
            }
        }
        return ret;
    }

    public static Integer min(Integer... nums) {
        Integer rst = null;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != null) {
                if (rst == null) {
                    rst = nums[i];
                } else {
                    rst = Math.min(rst, nums[i]);
                }
            }
        }
        return rst;
    }

    public static Integer max(Integer... nums) {
        Integer rst = null;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != null) {
                if (rst == null) {
                    rst = nums[i];
                } else {
                    rst = Math.max(rst, nums[i]);
                }
            }
        }
        return rst;
    }

    public static Object min(Object[] nums, Object[] descs) {
        int index = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != null && ((Number) nums[index]).doubleValue() > ((Number) nums[i]).doubleValue()) {
                index = i;
            }
        }
        return descs.length > index ? descs[index] : null;
    }

    public static Object max(Object[] nums, Object[] descs) {
        int index = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != null && ((Number) nums[index]).doubleValue() < ((Number) nums[i]).doubleValue()) {
                index = i;
            }
        }
        return descs.length > index ? descs[index] : null;
    }

    public static boolean isValidDateStr(String dateStr, String format) {
        boolean convertSuccess = true;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            sdf.setLenient(false);
            sdf.parse(dateStr);
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }


    @SuppressWarnings("rawtypes")
    public static <T> List<T> castCollection(List srcList, Class<T> clas) {
        List<T> list = new ArrayList<T>();
        for (Object obj : srcList) {
            if (obj != null && clas.isAssignableFrom(obj.getClass())) {
                list.add(clas.cast(obj));
            }
        }
        return list;
    }

    public static String getStackTrace(Exception t) {
        if (t != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            try {
                t.printStackTrace(pw);
                return sw.toString();
            } finally {
                pw.close();
            }
        } else {
            return null;
        }
    }

    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        //将一组数据平均分成n组
        List<List<T>> result = new ArrayList<List<T>>();
        int remainder = source.size() % n;  //(先计算出余数)
        int number = source.size() / n;  //然后是商
        int offset = 0; //偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remainder > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remainder--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

    public static <T> List<T> findInList(@SuppressWarnings("rawtypes") List srcList, Predicate<T> listComparator) {
        List<T> list = new ArrayList<T>();
        for (Object obj : srcList) {
            if (obj != null && listComparator.test((T) obj))
                list.add((T) obj);
        }
        return list;
    }

    public static boolean isEmptyList(List<?> list) {
        return list == null || list.size() == 0;
    }

    public static String strNotNull(String str) {
        return str == null ? "" : str;
    }


    public static long[] getTimeSpan(Date start, Date end) {
        long diffInSeconds = (end.getTime() - start.getTime()) / 1000;

        long diff[] = new long[]{0, 0, 0, 0};
        /* sec */
        diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        /* min */
        diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours */
        diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days */
        diff[0] = (diffInSeconds = (diffInSeconds / 24));
        return diff;
    }


    public static Integer getTimeSpan(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return null;
        }
        Long hours = ChronoUnit.HOURS.between(start, end);
        return hours.intValue();
    }


    @SuppressWarnings("deprecation")
    public static boolean isDateEqual(Date d1, Date d2) {
        return d1.getYear() == d2.getYear() && d1.getMonth() == d2.getMonth() && d1.getDate() == d2.getDate();
    }

    public static long getTimeSpanDaysWithNoTime(Date d1, Date d2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        d1 = cal.getTime();

        cal.setTime(d2);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        d2 = cal.getTime();

        return getTimeSpan(d1, d2)[0];
    }

    public static SortedMap<String, String> getProperites(Object obj) {
        SortedMap<String, String> retMap = new TreeMap<String, String>();
        try {
            BeanInfo targetbean = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = targetbean.getPropertyDescriptors();
            for (PropertyDescriptor targetPd : propertyDescriptors) {
                if (targetPd.getReadMethod() != null) {
                    Method readMethod = targetPd.getReadMethod();
                    if (!Modifier.isPublic(readMethod.getDeclaringClass()
                            .getModifiers())) {
                        readMethod.setAccessible(true);
                    }
                    Object value = readMethod.invoke(obj);
                    if ("class".equals(targetPd.getName())) {
                        continue;
                    }
                    if (value == null) {
                        retMap.put(targetPd.getName(), null);
                    } else {
                        if (value instanceof Date) {
                            retMap.put(targetPd.getName(), formatDateTime("yyyyMMddHHmmss", (Date) value));
                        } else {
                            retMap.put(targetPd.getName(), value.toString());
                        }
                    }
                }
            }

        } catch (Throwable t) {
            throw new RuntimeException("属性映射出错", t);
        }
        return retMap;
    }


    public static String[] findNullProperties(Object obj) {
        List<String> nullPropList = new ArrayList<>();

        try {
            BeanInfo targetbean = Introspector.getBeanInfo(obj.getClass());

            PropertyDescriptor[] propertyDescriptors = targetbean
                    .getPropertyDescriptors();
            for (PropertyDescriptor targetPd : propertyDescriptors) {
                if (targetPd.getReadMethod() != null) {
                    Method readMethod = targetPd.getReadMethod();
                    if (!Modifier.isPublic(readMethod.getDeclaringClass()
                            .getModifiers())) {
                        readMethod.setAccessible(true);
                    }
                    Object value = readMethod.invoke(obj);
                    if (null == value) {
                        nullPropList.add(targetPd.getName());
                    }
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("属性映射出错", t);
        }

        String[] ret = new String[nullPropList.size()];
        nullPropList.toArray(ret);
        return ret;
    }

    public static String[] findPropertieNames(Object obj, String regex) {
        List<String> ret = new ArrayList<>();
        try {
            BeanInfo targetbean = Introspector.getBeanInfo(obj.getClass());

            PropertyDescriptor[] propertyDescriptors = targetbean
                    .getPropertyDescriptors();
            for (PropertyDescriptor targetPd : propertyDescriptors) {
                if (targetPd.getReadMethod() != null && !"class".equals(targetPd.getName())) {
                    Method readMethod = targetPd.getReadMethod();
                    if (!Modifier.isPublic(readMethod.getDeclaringClass()
                            .getModifiers())) {
                        readMethod.setAccessible(true);
                    }
                    if (regex.matches(targetPd.getName())) {
                        ret.add(targetPd.getName());
                    }
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("获取属性出错", t);
        }

        String[] arr = new String[ret.size()];
        ret.toArray(arr);
        return arr;
    }

    public static Object getProperty(Object obj, String name) {
        try {
            BeanInfo targetbean = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = targetbean
                    .getPropertyDescriptors();
            for (PropertyDescriptor targetPd : propertyDescriptors) {
                if (targetPd.getReadMethod() != null && !"class".equals(targetPd.getName())) {
                    Method readMethod = targetPd.getReadMethod();
                    if (!Modifier.isPublic(readMethod.getDeclaringClass()
                            .getModifiers())) {
                        readMethod.setAccessible(true);
                    }
                    if (targetPd.getName().equals(name)) {
                        return readMethod.invoke(obj);
                    }
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("获取属性出错", t);
        }
        return null;
    }

    public static Map<String, Object> findProperties(Object obj, String regex) {
        Map<String, Object> ret = new HashMap<>();
        try {
            BeanInfo targetbean = Introspector.getBeanInfo(obj.getClass());

            PropertyDescriptor[] propertyDescriptors = targetbean
                    .getPropertyDescriptors();
            for (PropertyDescriptor targetPd : propertyDescriptors) {
                if (targetPd.getReadMethod() != null && !"class".equals(targetPd.getName())) {
                    Method readMethod = targetPd.getReadMethod();
                    if (!Modifier.isPublic(readMethod.getDeclaringClass()
                            .getModifiers())) {
                        readMethod.setAccessible(true);
                    }
                    if (targetPd.getName().matches(regex)) {
                        ret.put(targetPd.getName(), readMethod.invoke(obj));
                    }
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("获取属性出错", t);
        }
        return ret;
    }

    public static Map<String, Object> findProperties(Object obj, String[] names, boolean isIgnoreCase) {
        Map<String, Object> ret = new HashMap<>();
        try {
            BeanInfo targetbean = Introspector.getBeanInfo(obj.getClass());

            PropertyDescriptor[] propertyDescriptors = targetbean
                    .getPropertyDescriptors();
            for (PropertyDescriptor targetPd : propertyDescriptors) {
                if (targetPd.getReadMethod() != null && !"class".equals(targetPd.getName())) {
                    Method readMethod = targetPd.getReadMethod();
                    if (!Modifier.isPublic(readMethod.getDeclaringClass()
                            .getModifiers())) {
                        readMethod.setAccessible(true);
                    }
                    for (String name : names) {
                        if ((!isIgnoreCase && name.equals(targetPd.getName())) || (isIgnoreCase && name.equalsIgnoreCase(targetPd.getName()))) {
                            ret.put(targetPd.getName(), readMethod.invoke(obj));
                            break;
                        }
                    }
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("获取属性出错", t);
        }
        return ret;
    }

    public static Long IntegerToLong(Integer val) {
        return val == null ? null : val.longValue();
    }

    /**
     * 将MAP中key-value设置为Object的属性和值
     *
     * @param map
     * @param obj
     * @param isCaseSensitive
     * @throws IntrospectionException
     * @throws Exception
     */
    public static void mapProperties(Map<String, ? extends Object> map,
                                     Object obj, boolean isCaseSensitive) {
        try {
            // 获取目标类的属性信息
            BeanInfo targetbean = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = targetbean
                    .getPropertyDescriptors();
            // 对每个目标类的属性查找set方法，并进行处理
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor pro = propertyDescriptors[i];
                Method wm = pro.getWriteMethod();
                if (wm != null) {// 当目标类的属性具有set方法时，查找源类中是否有相同属性的get方法
                    Iterator<String> ite = map.keySet().iterator();
                    while (ite.hasNext()) {
                        String key = ite.next();
                        // 判断匹配
                        if (isCaseSensitive ? key.equals(pro.getName()) : key.equalsIgnoreCase(pro.getName())) {
                            if (!Modifier.isPublic(wm.getDeclaringClass()
                                    .getModifiers())) {
                                wm.setAccessible(true);
                            }
                            try {
                                Object value = convertByType(map.get(key),
                                        pro.getPropertyType());
                                // 调用目标类对应属性的set方法对该属性进行填充
                                wm.invoke(obj, value);
                            } catch (Exception e) {
                                throw new RuntimeException("parse " + key + " err, value is " + map.get(key) +
                                        ", need " + pro.getPropertyType().getSimpleName(), e);
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("映射出错：" + t.getMessage(), t);
        }
    }

    public static Object convertByType(Object source,
                                       Class<? extends Object> destType) {
        if (null == source || "".equals(source.toString().trim())) {
            return null;
        }
        if (Integer.class.equals(destType) || int.class.equals(destType)) {
            return new Integer(source.toString());
        }
        if (Long.class.equals(destType) || long.class.equals(destType)) {
            return new Long(source.toString());
        }
        if (Float.class.equals(destType) || long.class.equals(destType)) {
            return new Float(source.toString());
        }
        if (Double.class.equals(destType) || long.class.equals(destType)) {
            return new Double(source.toString());
        }
        if (String.class.equals(destType)) {
            return source.toString();
        }
        return source;
    }


    /**
     * 数组合并
     *
     * @param first
     * @param rest
     * @return
     */
    public static <T> T[] concat(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static Object[] concatIf(Object[] first, Predicate test, Object... rest) {
        Object[] rrst = filter(test, rest);
        int totalLength = first.length + rrst.length;
        Object[] result = Arrays.copyOf(first, totalLength);
        System.arraycopy(rrst, 0, result, first.length, rrst.length);
        return result;
    }

    public static Object[] concatIfNotEmpty(Object[] first, Object... rest) {
        return concatIf(first, notEmpty, rest);
    }

    /**
     * 解析文件路径, 返回[文件路径,文件名]
     *
     * @param filePath
     * @return
     */
    public static String[] parseFilePath(String filePath) {
        if (!isEmpty(filePath)) {
            String[] tmp = filePath.split("/");
            if (tmp.length > 1) {
                return new String[]{join(tmp, "/", 0, tmp.length - 1),
                        tmp[tmp.length - 1]};
            } else {
                tmp = filePath.split("\\\\");
                if (tmp.length > 1) {
                    return new String[]{join(tmp, "\\\\", 0, tmp.length - 1),
                            tmp[tmp.length - 1]};
                } else {
                    return new String[]{filePath, filePath};
                }
            }
        }
        return null;
    }

    /**
     * 将数组按照特定字符分割进行拼接, 以字符串返回
     *
     * @param array
     * @param sep
     * @return
     */
    public static String join(Object[] array, String sep) {
        return join(array, sep, 0, array.length);
    }

    public static String join(Object[] array, String sep, int start, int end) {
        StringBuilder sb = new StringBuilder();
        end = Math.min(end, array.length);
        for (int i = start; i < end; i++) {
            sb.append(array[i].toString()).append(sep);
        }
        return sb.substring(0, sb.length() - sep.length());
    }

    public static byte[] filterBOM(byte[] bytes) {
        int bomLen = 0;
        if ((bytes[0] == (byte) 0x00) && (bytes[1] == (byte) 0x00)
                && (bytes[2] == (byte) 0xFE) && (bytes[3] == (byte) 0xFF)) {
            // encoding = "UTF-32BE";
            bomLen = 4;
        } else if ((bytes[0] == (byte) 0xFF) && (bytes[1] == (byte) 0xFE)
                && (bytes[2] == (byte) 0x00) && (bytes[3] == (byte) 0x00)) {
            // encoding = "UTF-32LE";
            bomLen = 4;
        } else if ((bytes[0] == (byte) 0xEF) && (bytes[1] == (byte) 0xBB)
                && (bytes[2] == (byte) 0xBF)) {
            // encoding = "UTF-8";
            bomLen = 3;
        } else if ((bytes[0] == (byte) 0xFE) && (bytes[1] == (byte) 0xFF)) {
            // encoding = "UTF-16BE";
            bomLen = 2;
        } else if ((bytes[0] == (byte) 0xFF) && (bytes[1] == (byte) 0xFE)) {
            // encoding = "UTF-16LE";
            bomLen = 2;
        }
        bytes = Arrays.copyOfRange(bytes, bomLen, bytes.length);
        return bytes;
    }

    public static void filterEmpty(Map<String, String> map) {
        Iterator<String> ks = map.keySet().iterator();
        String tmp;
        while (ks.hasNext()) {
            tmp = ks.next();
            if (isEmpty(map.get(tmp))) {
                ks.remove();
            }
        }
    }

    public static String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(Charset.forName("UTF-8"))));
    }

    /**
     * @param insertPos 插入位置, -1表示从末尾插入
     * @param oriVal    原始字符串
     * @param wrapStr   插入字符
     * @param totalLen  总长度
     * @return
     */
    public static String wrap(int insertPos, String oriVal, String wrapStr,
                              int totalLen) {
        StringBuilder retVal = new StringBuilder().append(oriVal);
        while (retVal.length() < totalLen) {
            if (-1 == insertPos || insertPos >= retVal.length()) {
                retVal.insert(retVal.length() - 1, wrapStr);
            } else {
                retVal.insert(insertPos, wrapStr);
            }
        }
        return retVal.toString();
    }

    public static Document readXml(String path)
            throws ParserConfigurationException, SAXException, IOException {
        return readXml(Utils.class.getResourceAsStream(path));
    }

    public static Document readXml(InputStream in)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory
                .newInstance();
        builderFactory.setNamespaceAware(true);
        Document document = null;
        // DOM parser instance
        DocumentBuilder builder = builderFactory.newDocumentBuilder();

        // parse an XML file into a DOM tree
        document = builder.parse(in);
        return document;
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int i = 0; i < b.length; i++) {
            stmp = Integer.toHexString(b[i] & 0xFF);
            if (stmp.length() == 1) {
                hs += "0" + stmp;
            } else {
                hs += stmp;
            }
        }
        return hs.toUpperCase();
    }

    public static byte[] hex2byte(String hex) throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }

    final public static Predicate isEmpty = str -> null == str || "".equals(str.toString().trim());

    final public static Predicate notEmpty = str -> !isEmpty(str);

    public static boolean isAllEmpty(Object... strs) {
        if (strs == null) {
            return true;
        }
        return all(isEmpty, strs);
    }

    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str.toString().trim());
    }

    public static boolean isNull(Object... objs) {
        if (objs == null) {
            return true;
        }
        for (int i = 0; i < objs.length; i++) {
            if (objs[i] != null) return false;
        }
        return true;
    }

    public static boolean hasEmpty(Object... objs) {
        for (int i = 0; i < objs.length; i++) {
            if (objs[i] == null || "".equals(objs[i].toString().trim())) return true;
        }
        return false;
    }

    public static boolean hasNull(Object... objs) {
        return hasValue(objs, null);
    }

    public static boolean hasValue(Object[] objs, Object value) {
        for (int i = 0; i < objs.length; i++) {
            if (value == objs[i] || (value != null && value.equals(objs[i]))) return true;
        }
        return false;
    }

    public static boolean match(String src, String regex) {
        if (src == null) {
            return false;
        } else {
            return Pattern.matches(regex, src);
        }
    }

    public static boolean eq(Object a, Object b) {
        if (a == null || b == null) {
            return a == null && b == null;
        } else {
            if (a instanceof Number && b instanceof Number) {
                return (((Number) a).doubleValue()) == ((Number) b).doubleValue();
            } else {
                return a.equals(b);
            }
        }
    }

    public static boolean allEQ(Object a, Object[] datas) {
        return all(d -> eq(a, d), datas);
    }

    public static boolean anyEQ(Object a, Object[] datas) {
        return any(d -> eq(a, d), datas);
    }

    public static boolean neq(Object a, Object b) {
        return !eq(a, b);
    }

    public static boolean gt(Comparable a, Comparable b) {
        return compare(a, b) > 0;
    }

    public static boolean allGT(Comparable compared, Comparable[] datas) {
        return all(data -> compared.compareTo(data) < 0, datas);
    }

    public static boolean anyGT(Comparable compared, Comparable[] datas) {
        return any(data -> compared.compareTo(data) < 0, datas);
    }

    public static boolean gte(Comparable a, Comparable b) {
        return compare(a, b) >= 0;
    }

    public static boolean allGTE(Comparable compared, Comparable[] datas) {
        return all(data -> compared.compareTo(data) <= 0, datas);
    }

    public static boolean anyGTE(Comparable compared, Comparable[] datas) {
        return any(data -> compared.compareTo(data) <= 0, datas);
    }

    public static boolean lt(Comparable a, Comparable b) {
        return compare(a, b) < 0;
    }

    public static boolean allLT(Comparable compared, Comparable[] datas) {
        return all(data -> compared.compareTo(data) > 0, datas);
    }

    public static boolean anyLT(Comparable compared, Comparable[] datas) {
        return any(data -> compared.compareTo(data) > 0, datas);
    }

    public static boolean lte(Comparable a, Comparable b) {
        return compare(a, b) <= 0;
    }

    public static boolean allLTE(Comparable compared, Comparable[] datas) {
        return all(data -> compared.compareTo(data) >= 0, datas);
    }

    public static boolean anyLTE(Comparable compared, Comparable[] datas) {
        return any(data -> compared.compareTo(data) >= 0, datas);
    }

    /**
     * 所有数据都满足judger条件时返回 true, 否则 false
     *
     * @param judger
     * @param datas  数据
     * @param <T>
     * @return
     */
    public static <T> boolean all(Predicate<T> judger, T[] datas) {
        for (T data : datas) {
            if (!judger.test(data)) {
                return false;
            }
        }
        return true;
    }

    public static <T> int count(Predicate<T> judger, T[] datas) {
        int rst = 0;
        for (T data : datas) {
            if (judger.test(data)) {
                rst++;
            }
        }
        return rst;
    }

    public static <T> int countEQ(T obj, T[] datas) {
        return count(data -> eq(obj, data), datas);
    }

    public static <T> int countNeq(T obj, T[] datas) {
        return count(data -> !eq(obj, data), datas);
    }

    /**
     * 任意数据满足judger条件时返回 true, 否则 false
     *
     * @param judger
     * @param datas  数据
     * @param <T>
     * @return
     */
    public static <T> boolean any(Predicate<T> judger, T[] datas) {
        for (T data : datas) {
            if (judger.test(data)) {
                return true;
            }
        }
        return false;
    }

    public static Object get(Predicate judger, Object... datas) {
        for (Object data : datas) {
            if (judger.test(data)) {
                return data;
            }
        }
        return null;
    }

    public static Object[] filter(Predicate judger, Object... datas) {
        return Stream.of(datas).filter(judger).toArray();
    }

    public static int compare(Comparable a, Comparable b) {
        if (a == null && b == null) {
            return 0;
        } else if (a == null || b == null) {
            return -1;
        } else {
            if (a instanceof Number && b instanceof Number) {
                double rst = ((Number) a).doubleValue() - ((Number) b).doubleValue();
                return rst > 0 ? 1 : (rst < 0 ? -1 : 0);
            } else {
                return a.compareTo(b);
            }
        }
    }


    /**
     * 把参数date指定的日期格式化为参数pattern指定的日期格式字符串.
     *
     * @param pattern
     * @param date
     * @return the formatted date-time string
     * @see SimpleDateFormat
     */
    public static String formatDateTime(String pattern, Date date) {
        try {
            String strDate = null;
            String strFormat = pattern;
            SimpleDateFormat dateFormat = null;

            if (date == null)
                return "";

            dateFormat = new SimpleDateFormat(strFormat);
            strDate = dateFormat.format(date);

            return strDate;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据日期字符串获取日期对象
     *
     * @param pattern
     * @param dateStr
     * @return
     */
    public static Date parseDateTime(String pattern, String dateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }


    public static boolean getBooleanSt(String status, String p1) {

        if (p1.equals("1")) {
            switch (status) {
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                case "6":
                case "7":
                case "B":
                    return true;
                default:
                    return false;
            }
        } else if (p1.equals("2")) {
            switch (status) {
                case "2":
                case "3":
                case "4":
                case "5":
                case "6":
                case "7":
                case "B":
                    return true;
                default:
                    return false;
            }
        } else if (p1.equals("3")) {
            switch (status) {
                case "3":
                case "4":
                case "5":
                case "6":
                case "7":
                case "B":
                    return true;
                default:
                    return false;
            }
        } else if (p1.equals("4")) {
            switch (status) {
                case "4":
                case "5":
                case "6":
                case "7":
                case "B":
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }

    }

    /**
     * 根据日期字符串获取日期对象
     * Java8 API
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static LocalDate parseLocalDate(String dateStr, String pattern) {
        if (Utils.isEmpty(dateStr) || Utils.isEmpty(pattern)) {
            return null;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDate localDate = LocalDate.parse(dateStr, formatter);
            return localDate;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据日期时间字符串获取日期时间对象
     * Java8 API
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static LocalDateTime parseLocalDateTime(String dateStr, String pattern) {
        if (Utils.isEmpty(dateStr) || Utils.isEmpty(pattern)) {
            return null;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
            return localDateTime;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将 Long 类型的时间戳根据传入的 pattern 转换成 String 类型的时间格式
     *
     * @param time
     * @param pattern
     * @return
     */
    public static String convertTimeToString(Long time, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取2个日期直接间隔的月份数，一般用来算账龄
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public static Integer getSpecificMonthsWithLocalDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        startDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), 1);
        endDate = LocalDate.of(endDate.getYear(), endDate.getMonth(), 1);
        return (int) ChronoUnit.MONTHS.between(startDate, endDate);
    }

    /**
     * 获取2个日期直接间隔的月份数，一般用来算账龄
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public static Long getAbsoluteDays(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        return days > 0 ? days : -days;
    }

    /**
     * 变量替换 <br/>
     * eg1: replaceVariable("a${var1}b${var2}", {var1=KKK, var2=YYY}) -->
     * aKKKbYYY <br/>
     * eg2: replaceVariable("a${var1}b${var2}", {var1=KK\\$1K, var2=YYY}) -->
     * aKK$1KbYYY
     *
     * @param str
     * @return
     */
    private static Pattern STRING_PATTERN = Pattern.compile("\\$\\{.*?\\}");

    public static String replaceVariable(String str,
                                         Map<String, String> variantMap) {
        Matcher m = STRING_PATTERN.matcher(str);
        str = Matcher.quoteReplacement(str);
        StringBuffer rtn = new StringBuffer();
        while (m.find()) {
            String foundStr = m.group();
            String key = foundStr.substring("${".length(),
                    foundStr.length() - 1).trim();
            String value = variantMap.get(key);
            if (null == value) {
                value = "";
            }
            m.appendReplacement(rtn, value);
        }
        m.appendTail(rtn);
        return rtn.toString();
    }

    /**
     * 生成随机数字串
     *
     * @param len
     * @return
     */
    public static String randomNum(int len) {
        int rst = 1;
        for (int i = 1; i < len; i++) {
            rst *= 10;
        }
        return String.valueOf((int) ((Math.random() * 9 + 1) * rst));
    }


    public static <E> E[] toArray(List<E> list, Class<E> clasz) {
        @SuppressWarnings("unchecked")
        E[] rst = (E[]) Array.newInstance(clasz, list.size());
        return list.toArray(rst);
    }

    /**
     * 获取下一天
     *
     * @param now
     * @return
     */
    public static Date getNextDatetime(Date now) {
        return getXDatetime(now, 1);
    }

    /**
     * 获取当前时间的前或后X天
     *
     * @param now
     * @return
     */
    public static Date getXDatetime(Date now, Integer xDays) {
        return new Date(now.getTime() + 24L * 3600 * 1000 * xDays);
    }

    /**
     * 获取date的年月日时间 yyyyMMdd000000
     *
     * @param date
     * @return
     */
    public static Date getDay(Date date) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            return df.parse(df.format(date));
        } catch (ParseException e) {
            return null;
        }
    }

    public static Integer toInt(String str, Integer defaultValue) {
        Integer intVal;
        try {
            intVal = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            intVal = defaultValue;
        }

        return intVal;
    }

    public static Long toLong(String str, Long defaultValue) {
        Long intVal;
        try {
            intVal = Long.parseLong(str);
        } catch (NumberFormatException e) {
            intVal = defaultValue;
        }

        return intVal;
    }

    public static int diffInMonths(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return 0;

        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(startDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

        return diffMonth;
    }

    public static int diffInDays(LocalDate startDate, LocalDate endDate) {
        if (null == startDate || null == endDate) return 0;
        Long diffDay = endDate.toEpochDay() - startDate.toEpochDay();
        return diffDay.intValue();
    }


    public static String normalizeValue(String val) {
        return val != null && val.endsWith(".0") ? val.substring(0, val.length() - 2) : val;
    }

    public static String getLastMonthDate(int lastMonths, Date reportDate) {
        return reportDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .minusMonths(lastMonths)
                .format(DateTimeFormatter.ofPattern("yyyyMM"));
    }


    public static String getLastMonthDate(int lastMonths, String date, String dateFormat) {
        return parseDateTime(dateFormat, date).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .minusMonths(lastMonths)
                .format(DateTimeFormatter.ofPattern(dateFormat));
    }

    public static String getAfterMonthDate(int afterMonths, String date, String dateFormat) {
        return parseDateTime(dateFormat, date).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .plusMonths(afterMonths)
                .format(DateTimeFormatter.ofPattern(dateFormat));
    }

    public static String getLastDaysDate(int days, Date date, String dateFormat) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .minusDays(days)
                .format(DateTimeFormatter.ofPattern(dateFormat));
    }

    public static int getResultByStatType(StatType statType, Stream<Integer> payStream) {
        int result = 0;
        if (payStream == null || statType == null) return Consts.NO_REQUIRED_DATA_INT;

        List<Integer> statStream = payStream.collect(Collectors.toList());
        if (Utils.isEmptyList(statStream)) return Consts.NO_REQUIRED_DATA_INT;

        switch (statType) {
            case MAX:
                result = statStream.stream().max(Comparator.comparing(Integer::valueOf)).orElse(0);
                break;
            case MIN:
                result = statStream.stream().min(Comparator.comparing(Integer::valueOf)).orElse(0);
                break;
            case AVG:
                result = (int) statStream.stream().mapToInt(Integer::intValue).average().orElse(0);
                break;
            case SUM:
                result = statStream.stream().mapToInt(Integer::intValue).sum();
                break;
            default:
                return Consts.NO_REQUIRED_DATA_INT;
        }
        return result;
    }


    public static double getDoubleByStatType(StatType statType, Stream<Double> payStream) {
        double result = 0;
        if (payStream == null || statType == null) return Consts.NO_REQUIRED_DATA_DOUBLE;

        List<Double> statStream = payStream.collect(Collectors.toList());
        if (Utils.isEmptyList(statStream)) return Consts.NO_REQUIRED_DATA_DOUBLE;

        switch (statType) {
            case MAX:
                result = statStream.stream().max(Comparator.comparing(Double::valueOf)).orElse(0d);
                break;
            case MIN:
                result = statStream.stream().min(Comparator.comparing(Double::valueOf)).orElse(0d);
                break;
            case AVG:
                result = statStream.stream().mapToInt(Double::intValue).average().orElse(0d);
                result = round(result, 4);
                break;
            case SUM:
                result = statStream.stream().mapToInt(Double::intValue).sum();
                break;
            default:
                return Consts.NO_REQUIRED_DATA_DOUBLE;
        }
        return result;
    }

    static public String toString(Reader reader) {
        try (BufferedReader br = new BufferedReader(reader)) {
            StringBuilder sb = new StringBuilder();
            String tmp = null;
            while ((tmp = br.readLine()) != null) {
                sb.append(tmp);
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    static public String toString(InputStream in, String charset) {
        try {
            return toString(new InputStreamReader(in, charset));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static Double getDoubleFromIntPercent(Integer percent) {
        if (percent == null) {
            return null;
        }

        try {
            BigDecimal bigDecimal = new BigDecimal(percent);
            bigDecimal = bigDecimal.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
            return bigDecimal.doubleValue();
        } catch (Exception ex) {
            return null;
        }
    }

    public static Double getDoubleFromPercent(String percent) {
        if (percent == null || percent.indexOf("%") != percent.length() - 1)
            return null;
        percent = percent.substring(0, percent.length() - 1);
        try {
            BigDecimal bigDecimal = new BigDecimal(percent);
            bigDecimal = bigDecimal.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
            return bigDecimal.doubleValue();
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getQueryReasonByAcctTypeCN(CalcAcctType acctType) {
        switch (acctType) {
            case NonrevolvingCredit:
            case SubAccountUnderRevolvingQuota:
            case RevolvingCredit:
            case Loan:
                return "贷款审批";
            case CreditCard:
            case AllCreditCard:
            case QuasiCreditCard:
                return "信用卡审批";
            default:
                return "";
        }
    }

    /**
     * 正则表达式校验
     *
     * @param s
     * @param pattern
     * @return
     */
    public static boolean checkByPattern(String s, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(s);
        return matcher.matches();
    }

    public static void print(Object... objects) {
        for (Object obj : objects)
            System.out.print(obj);
        System.out.println();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String getDayOfWeek(int weeks, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_MONTH, weeks);
        //1取的是周日
        calendar.set(Calendar.DAY_OF_WEEK, day);
        return formatDateTime("yyyyMMdd", calendar.getTime());
    }

    public static double toDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (Exception exception) {
            return defaultValue;
        }
    }

    public static boolean checkAcctType(LoanInfo loanInfo) {

        boolean returnBool = false;

        if (ParameterMapping.isFitAcctType(loanInfo.getAcct_Type(), CalcAcctType.NonrevolvingCredit)
                || ParameterMapping.isFitAcctType(loanInfo.getAcct_Type(), CalcAcctType.SubAccountUnderRevolvingQuota)) {
            if (loanInfo.getAmt() != null) {
                returnBool = true;
            }
        } else if (ParameterMapping.isFitAcctType(loanInfo.getAcct_Type(), CalcAcctType.RevolvingCredit)) {
            if (loanInfo.getCredit_Limit() != null) {
                returnBool = true;
            }
        }
        return returnBool;
    }

    public static int getAcctTypeOverdraft(LoanInfo loanInfo) {

        int returnValue = 0;
        if (ParameterMapping.isFitAcctType(loanInfo.getAcct_Type(), CalcAcctType.NonrevolvingCredit)
                || ParameterMapping.isFitAcctType(loanInfo.getAcct_Type(), CalcAcctType.SubAccountUnderRevolvingQuota)) {
            returnValue = loanInfo.getAmt().intValue();

        } else if (ParameterMapping.isFitAcctType(loanInfo.getAcct_Type(), CalcAcctType.RevolvingCredit)) {
            returnValue = loanInfo.getCredit_Limit().intValue();
        }
        return returnValue;
    }

    public static double getUsageRate(LoanInfo loanInfo) {

        int a = loanInfo.getBalance().intValue();

        int b = 0;
        if (ParameterMapping.isFitAcctType(loanInfo.getAcct_Type(), CalcAcctType.NonrevolvingCredit)
                || ParameterMapping.isFitAcctType(loanInfo.getAcct_Type(), CalcAcctType.SubAccountUnderRevolvingQuota)) {
            b = loanInfo.getAmt().intValue();

        } else if (ParameterMapping.isFitAcctType(loanInfo.getAcct_Type(), CalcAcctType.RevolvingCredit)) {
            b = loanInfo.getCredit_Limit().intValue();
        }

        if (b == 0) {
            return Consts.NO_REQUIRED_DATA_DOUBLE;
        }

        return BigDecimal.valueOf(a)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(b), 4, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    /**
     * 计算阶乘数，即n! = n * (n-1) * ... * 2 * 1
     *
     * @param n
     * @return
     */
    private static long factorial(int n) {
        return (n > 1) ? n * factorial(n - 1) : 1;
    }

    /**
     * 计算排列数，即A(n, m) = n!/(n-m)!
     *
     * @param n
     * @param m
     * @return
     */
    public static long arrangement(int n, int m) {
        return (n >= m) ? factorial(n) / factorial(n - m) : 0;
    }

    /**
     * 计算组合数，即C(n, m) = n!/((n-m)! * m!)
     *
     * @param n
     * @param m
     * @return
     */
    public static long combination(int n, int m) {
        return (n >= m) ? factorial(n) / factorial(n - m) / factorial(m) : 0;
    }

    /**
     * 计算2个字符串的相似度
     *
     * @param text1
     * @param text2
     * @param useHanlp 是否使用HanLP分词
     * @return 相似度（0~1，越接近1就说明越相似）
     */
    public static double getSimilarity(String text1, String text2, boolean useHanlp) {
        return CosineSimilarity.getSimilarity(text1, text2, useHanlp);
    }

    /**
     * 计算一组字符串的相似度
     *
     * @param texts
     * @param threshold
     * @param useHanlp
     * @return
     */
    public static List<List<String>> getSimilarityOfList(List<String> texts, double threshold, boolean useHanlp) {
        if (Utils.isEmptyList(texts) || threshold < 0 || threshold > 1) return null;

        List<SimilarText> similarTexts = new ArrayList<>((int) combination(texts.size(), 2));

        for (int i = 0; i < texts.size(); i++) {
            for (int j = 0; j < texts.size(); j++) {
                if (i >= j) continue;
                SimilarText similarText = new SimilarText();
                similarText.setText1Index(i);
                similarText.setText2Index(j);
                similarText.setSimilarity(getSimilarity(texts.get(i), texts.get(j), useHanlp));
                similarTexts.add(similarText);
            }
        }

        Comparator<SimilarText> comparator = (t1, t2) -> t1.getSimilarity().compareTo(t2.getSimilarity());
        similarTexts = similarTexts.stream().sorted(comparator.reversed()).collect(Collectors.toList());

        List<Set<Integer>> assignedInxList = new ArrayList<>();
        for (int i = 0; i < similarTexts.size(); i++) {
            int text1Index = similarTexts.get(i).getText1Index();
            int text2Index = similarTexts.get(i).getText2Index();

            Integer text1AssignedInx = null;
            Integer text2AssignedInx = null;

            for (int j = 0; j < assignedInxList.size(); j++) {
                if (assignedInxList.get(j).contains(text1Index)) {
                    text1AssignedInx = j;
                }
                if (assignedInxList.get(j).contains(text2Index)) {
                    text2AssignedInx = j;
                }
            }

            if (similarTexts.get(i).getSimilarity() >= threshold) {

                if (text1AssignedInx != null && text2AssignedInx != null) {
                    if (!text1AssignedInx.equals(text2AssignedInx)) {// 此时需要合并2个set
                        assignedInxList.get(text1AssignedInx).addAll(assignedInxList.get(text2AssignedInx));
                        assignedInxList.remove(text2AssignedInx.intValue());// 注意传入Integer对象则会移除元素而不是索引
                    }
                } else if (text1AssignedInx != null) {// 此时将另一个textInx添加到set中
                    assignedInxList.get(text1AssignedInx).add(text2Index);
                } else if (text2AssignedInx != null) {
                    assignedInxList.get(text2AssignedInx).add(text1Index);
                } else {// 此时向assignedInxList中新增一个set
                    Set<Integer> inxSet = new HashSet<>();
                    inxSet.add(text1Index);
                    inxSet.add(text2Index);
                    assignedInxList.add(inxSet);
                }

            } else {

                if (text1AssignedInx == null) {
                    Set<Integer> inxSet = new HashSet<>();
                    inxSet.add(text1Index);
                    assignedInxList.add(inxSet);
                }
                if (text2AssignedInx == null) {
                    Set<Integer> inxSet = new HashSet<>();
                    inxSet.add(text2Index);
                    assignedInxList.add(inxSet);
                }

            }
        }

        List<List<String>> assignedList = new ArrayList<>();

        assignedInxList.stream().forEach(inxSet -> {
            List<String> textlist = new ArrayList<>();
            inxSet.stream().forEach(inx -> textlist.add(texts.get(inx)));
            assignedList.add(textlist);
        });

        return assignedList;
    }

    /**
     * 计算一组字符串的相似度
     *
     * @param texts
     * @param threshold
     * @param useHanlp
     * @return
     */
    public static List<String> getSimilarityOfListByDistinct(List<String> texts, double threshold, boolean useHanlp) {
        List<String> rstList = texts.stream().map(text -> new Text(text, threshold, useHanlp))
                .distinct().map(text -> text.getName())
                .collect(Collectors.toList());

        return rstList;
    }

    public static String addressResolution(String address, String levelType) {
        Map<String, Map<String, List<String>>> dic = AddressUtils.getAddressMap();
        String result = "";
        Map<String, List<String>> dicMap = new HashMap<>();
        List<String> provinceList = new ArrayList<>();
        List<String> cityList = new ArrayList<>();
        List<String> areaList = new ArrayList<>();
        if (dic != null) {
            for (Map.Entry<String, Map<String, List<String>>> proEntry : dic.entrySet()) {
                provinceList.add(proEntry.getKey());
                cityList.addAll(proEntry.getValue().get("city"));
                areaList.addAll(proEntry.getValue().get("area"));
            }
        }
        dicMap.put("province", provinceList);
        dicMap.put("city", cityList);
        dicMap.put("area", areaList);

        if (!isEmpty(address)) {
            String province = "";
            for (String pro : dicMap.get("province")) {
                if (address.contains(pro)) {
                    province = pro;
                    break;
                }
            }

            if ("province".equals(levelType)) {
                result = province;
            } else if ("city".equals(levelType)) {
                List<String> tempCityList = new ArrayList<>();
                tempCityList = (!"".equals(province) && dic != null && dic.get(province) != null) ?
                        dic.get(province).get("city") : dicMap.get("city");
                for (String city : tempCityList) {
                    if (address.contains(city)) {
                        String[] municipality = {"北京市", "上海市", "天津市", "重庆市"};
                        if (Arrays.asList(municipality).contains(city)) result = province;
                        else result = city;
                        break;
                    }
                }
            } else {
                List<String> tempAreaList = new ArrayList<>();
                tempAreaList = (!"".equals(province) && dic != null && dic.get(province) != null) ?
                        dic.get(province).get("area") : dicMap.get("area");
                for (String area : tempAreaList) {
                    if (address.contains(area)) {
                        result = area;
                        break;
                    }
                }
            }
        }
        return result;

    }

    /*private static Map<String, List<String>> getAddrInfos() {

        Map<String, List<String>> addressMap = new HashMap<>();

        try(
            FileInputStream inputStream = new FileInputStream("D:\\address.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"GBK"))) {

            List<Address> addressList = new ArrayList<>();
            String line = null;
            while((line = reader.readLine()) != null){
                String[] cells = line.split(",");
                Address address = new Address();
                address.setAreaCode(cells[0]);
                address.setAreaName(cells[1]);
                address.setClassCode(cells[2]);
                if(cells.length > 3){
                    address.setHigherCode(cells[3]);
                }
                addressList.add(address);
            }

            Map<String, List<String>> tempMap =  addressList.stream()
                    .collect(Collectors.groupingBy(
                            Address::getClassCode, Collectors.mapping(Address::getAreaName, Collectors.toList())));
            addressMap.put("province",tempMap.get("0"));
            addressMap.put("city",tempMap.get("1"));
            addressMap.put("area",tempMap.get("2"));
            tempMap.clear();
            LogUtil.info("parsing address success");
        }catch (Exception e) {
            LogUtil.err("parsing address failed : "+e.getMessage(),e);
        }

        return addressMap;
    }*/


    //判断查询日期在那个区间
    public static Boolean getMonthsBetween(String ph010r01, Date reportDate, String p1) {
        if (ph010r01 == null) {
            return false;
        }
        String s = Utils.formatDateTime("yyyy-MM-dd", reportDate);
        String lastMonthDate;
        String startMonthDate;
        int i = Utils.diffInMonths(Utils.parseDateTime("yyyy-MM-dd", ph010r01), reportDate);
        switch (p1) {
            case "3":
                lastMonthDate = Utils.getLastMonthDate(3, s, "yyyy-MM-dd");
                return ph010r01.compareTo(lastMonthDate)>0;
            case "4_6":
                lastMonthDate = Utils.getLastMonthDate(3, s, "yyyy-MM-dd");
                startMonthDate = Utils.getLastMonthDate(6, s, "yyyy-MM-dd");
                return ph010r01.compareTo(startMonthDate)>0 && ph010r01.compareTo(lastMonthDate)<=0;
            case "7_12":
                lastMonthDate = Utils.getLastMonthDate(6, s, "yyyy-MM-dd");
                startMonthDate = Utils.getLastMonthDate(12, s, "yyyy-MM-dd");
                return ph010r01.compareTo(startMonthDate)>0 && ph010r01.compareTo(lastMonthDate)<=0;
            default:
                return false;
        }
    }

    //判断查询原因
    public static Boolean getQueryReasonByAcctTypeCN(String p1, String PH010Q03) {
        switch (p1) {

            case "plq":
                return PH010Q03.equals("贷款审批") || PH010Q03.equals("02");//贷款审批02
            case "accq":
                return PH010Q03.equals("信用卡审批") || PH010Q03.equals("03");//信用卡审批03
            default:
                return false;
        }
    }

    //筛选贷款账户['非循环贷账户', 'D1', '循环贷账户', 'R1', '循环额度下分账户', 'R4']  贷款账户
    public static Boolean getAccountType(String pdo1ado1) {
        switch (pdo1ado1) {
            case "D1":
            case "R1":
            case "R4":
            case "非循环贷账户":
            case "循环贷账户":
            case "循环额度下分账户":
                return true;
            default:
                return false;
        }
    }

    //账户类型(PD01AD01) in ['R2', 'R3', '贷记卡账户', '准贷记卡账户']   信用卡账户
    public static Boolean getAccountType1(String pdo1ado1) {
        switch (pdo1ado1) {
            case "R2":
            case "R3":
            case "贷记卡账户":
            case "准贷记卡账户":
                return true;
            default:
                return false;
        }
    }


    public static Boolean getAccountAll(String pdo1ado1) {
        switch (pdo1ado1) {
            case "R2":
            case "R3":
            case "贷记卡账户":
            case "准贷记卡账户":
            case "D1":
            case "R1":
            case "R4":
            case "非循环贷账户":
            case "循环贷账户":
            case "循环额度下分账户":
                return true;
            default:
                return false;
        }
    }

    //筛选业务种类(PD01AD03) in ['个人住房商业贷款', '11', '个人商用房（含商住两用）贷款', '12', '个人住房公积金贷款', '13', '个人汽车消费贷款', '21']
    public static boolean getBusiType(String pd01ad03) {
        switch (pd01ad03) {
            case "个人住房商业贷款":
            case "个人住房公积金贷款":
            case "个人商用房（含商住两用）贷款":
            case "个人汽车消费贷款":
            case "11":
            case "12":
            case "13":
            case "21":
                return true;
            default:
                return false;
        }
    }

    //筛选业务种类(PD01AD03) in ['个人住房商业贷款', '11', '个人商用房（含商住两用）贷款', '12', '个人住房公积金贷款', '13']
    public static boolean getHouseLoan(String pd01ad03) {
        switch (pd01ad03) {
            case "个人住房商业贷款":
            case "个人住房公积金贷款":
            case "个人商用房（含商住两用）贷款":
            case "11":
            case "12":
            case "13":
                return true;
            default:
                return false;
        }
    }

    public static boolean getpd01ad01(String pd01ad01, String p1) {

        //pl|cc|qcc贷款|贷记卡|准贷记卡
        if (p1.equals("pl")) {
            return getAccountType(pd01ad01);
        }
        if (p1.equals("cc")) {
            switch (pd01ad01) {
                case "R2":
                case "贷记卡账户":
                    return true;
                default:
                    return false;
            }
        }
        if (p1.equals("qcc")) {
            switch (pd01ad01) {
                case "R3":
                case "准贷记卡账户":
                    return true;
                default:
                    return false;
            }
        }

        return false;
    }

    public static boolean getPLAccAll(String pd01ad01, String p1) {

        //pl|cc|qcc贷款|贷记卡|准贷记卡
        if (p1.equals("pl")) {
            return getAccountType(pd01ad01);
        }
        if (p1.equals("acc")) {
            return getAccountType1(pd01ad01);
        }
        if (p1.equals("all")) {
            return getAccountType(pd01ad01) || getAccountType1(pd01ad01);
        }

        return false;
    }

    //     * p1 perbizl    |carl         |pershl        |csml        |perstul    |comhl      |fundhl            |agril    |othl
//     * 个人经营性贷款|个人汽车贷款|个人商用房贷款|个人消费贷款|个人助学贷款|个人住房贷款|个人住房公积金贷款|农户贷款|其他贷款
    //['41', u'个人经营性贷款']
//     * ['21', u'个人汽车消费贷款']['12', u'个人商用房（含商住两用）贷款'']['91', u'其他个人消费贷款']
//     * ['31', u'个人助学贷款']['11', u'个人住房商业贷款']['13', u'个人住房公积金贷款']['51', u'农户贷款']['99', u'其他贷款']
    public static boolean getBusiType(String pd01ad03, String p) {
        switch (p) {
            case "perbizl":
                return pd01ad03.equals("41") || pd01ad03.equals("个人经营性贷款");
            case "carl":
                return pd01ad03.equals("21") || pd01ad03.equals("个人汽车消费贷款");
            case "pershl":
                return pd01ad03.equals("12") || pd01ad03.equals("个人商用房（含商住两用）贷款");
            case "csml":
                return pd01ad03.equals("91") || pd01ad03.equals("其他个人消费贷款");
            case "perstul":
                return pd01ad03.equals("31") || pd01ad03.equals("个人助学贷款");
            case "comhl":
                return pd01ad03.equals("11") || pd01ad03.equals("个人住房商业贷款");
            case "fundhl":
                return pd01ad03.equals("13") || pd01ad03.equals("个人住房公积金贷款");
            case "agril":
                return pd01ad03.equals("51") || pd01ad03.equals("农户贷款");
            case "othl":
                return pd01ad03.equals("99") || pd01ad03.equals("其他贷款");
            default:
                return false;
        }
    }


    /**
     *        计算两个日期之间相差月份 （例子  2020-09-05  减 2020-06-04  为 3   ，2020-09-05  减 2020-06-06  为 2 ）
     * @param startDate
     * @param endDate
     * @return
     */
    public static Integer getAbsoluteMonths(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        int months = (int)ChronoUnit.MONTHS.between(startDate, endDate);
        return months > 0 ? months : -months;
    }


    /**
     * 取值逻辑：
     * select count(*)  into dk_count from  TMP_I_R_LOANACCOUNTINFO_SESSION where msgidno=IN_MSGIDNO  and dbtcr_acc_tp in ('D1','R1','R4');
     *
     * @return 计数结果
     */
    public static Integer dk_count(PbccrcReportEntity report) {
        if (report.getCredit_Transaction_Info() == null || Utils.isEmptyList(report.getCredit_Transaction_Info().getLoan_Info()))
            return 0;
        return report.getCredit_Transaction_Info().getLoan_Info().stream().filter(x -> x.getAcct_Type() != null)
                .filter(x -> (ParameterMapping.isFitAcctType(x.getAcct_Type(), CalcAcctType.NonrevolvingCredit) ||
                        ParameterMapping.isFitAcctType(x.getAcct_Type(), CalcAcctType.RevolvingCredit) ||
                        ParameterMapping.isFitAcctType(x.getAcct_Type(), CalcAcctType.SubAccountUnderRevolvingQuota)))
                .collect(Collectors.toList()).size();
    }


    /**
     * 取值逻辑：
     * select count(*)  into zdjk_count from  TMP_I_R_LOANACCOUNTINFO_SESSION where msgidno=IN_MSGIDNO and dbtcr_acc_tp='R3' ;
     *
     * @param report 报文
     * @return 计数结果
     */
    public static Integer zdjk_count(PbccrcReportEntity report) {
        if (report.getCredit_Transaction_Info() == null || Utils.isEmptyList(report.getCredit_Transaction_Info().getLoan_Info()))
            return 0;
        return report.getCredit_Transaction_Info().getLoan_Info().stream().filter(x -> x.getAcct_Type() != null)
                .filter(x -> ParameterMapping.isFitAcctType(x.getAcct_Type(), CalcAcctType.QuasiCreditCard)).collect(Collectors.toList()).size();
    }

    public static Double getDbtcrAcbaByLoanInfo(LoanInfo info) {
        double result = 0;
        if (info.getBalance() != null) {
            result = info.getBalance();
        } else if (info.getLatest_Pay_Info() != null && info.getLatest_Pay_Info().getLatest_Balance() != null) {
            result = info.getLatest_Pay_Info().getLatest_Balance();
        }
        return result;
    }

    public static Double divide(Integer p1, Integer p2) {
        return new BigDecimal(p1).divide(new BigDecimal(p2), 6, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static Double divide(Double p1, Double p2) {
        return new BigDecimal(p1).divide(new BigDecimal(p2), 6, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 取值逻辑：
     * select count(*)  into djk_count from  TMP_I_R_LOANACCOUNTINFO_SESSION where msgidno=IN_MSGIDNO and dbtcr_acc_tp='R2' ;
     *
     * @param report
     * @return
     */
    public static Integer djk_count(PbccrcReportEntity report) {
        if (report.getCredit_Transaction_Info() == null || Utils.isEmptyList(report.getCredit_Transaction_Info().getLoan_Info()))
            return 0;
        return report.getCredit_Transaction_Info().getLoan_Info().stream().filter(x -> x.getAcct_Type() != null)
                .filter(x -> ParameterMapping.isFitAcctType(x.getAcct_Type(), CalcAcctType.CreditCard)).collect(Collectors.toList()).size();
    }

}
