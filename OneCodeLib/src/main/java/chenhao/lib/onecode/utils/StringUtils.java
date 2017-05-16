package chenhao.lib.onecode.utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class StringUtils {

    public static String encodeUTF(String str) {
        if (isNotEmpty(str)) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return str;
            }
        } else {
            return "";
        }
    }

    public static String decodeUTF(String str) {
        if (isNotEmpty(str)) {
            try {
                return URLDecoder.decode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return str;
            }
        } else {
            return "";
        }
    }

    public static String decodeURL(String str) {
        return URLDecoder.decode(str);
    }

    public static boolean isEmpty(Object str) {
        return null == str || str.toString().length() == 0 || "None".equals(str.toString()) || str.toString().trim().length() == 0;
    }

    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

    public static boolean equals(String str1, String str2) {
        return isNotEmpty(str1)&&isNotEmpty(str2)&&str1.equals(str2);
    }

    public static int toInt(String str) {
        int i = -1;
        if (isNotEmpty(str)) {
            try {
                i = Integer.parseInt(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public static long toLong(String str) {
        long i = -1;
        if (isNotEmpty(str)) {
            try {
                i = Long.parseLong(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public static double toDouble(String str) {
        double i = -1;
        if (isNotEmpty(str)) {
            try {
                i = Double.parseDouble(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public static float toFloat(String str) {
        float i = -1;
        if (isNotEmpty(str)) {
            try {
                i = Float.parseFloat(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public static boolean startWithFile(Object str) {
        return isNotEmpty(str) && str.toString().toLowerCase().startsWith("file://");
    }

    public static boolean startWithHttp(Object str) {
        return isNotEmpty(str) && (str.toString().toLowerCase().startsWith("http://") || str.toString().toLowerCase().startsWith("https://"));
    }

    public static boolean startWithTag(Object str, String tag) {
        return isNotEmpty(str) && isNotEmpty(tag) && str.toString().startsWith(tag);
    }

    public static boolean isMobile(Object str) {
        boolean one = isNotEmpty(str) && str.toString().length() == 11;
        if (one) {
//            one=startWithTag(str,"134")||startWithTag(str,"135")||startWithTag(str,"136")||startWithTag(str,"137")
//                    ||startWithTag(str,"138")||startWithTag(str,"139")||startWithTag(str,"150")||startWithTag(str,"151")
//                    ||startWithTag(str,"152")||startWithTag(str,"158")||startWithTag(str,"159")||startWithTag(str,"182")
//                    ||startWithTag(str,"183")||startWithTag(str,"184")||startWithTag(str,"157")||startWithTag(str,"187")
//                    ||startWithTag(str,"188")||startWithTag(str,"147")||startWithTag(str,"178")||startWithTag(str,"170")
//                    ||startWithTag(str,"130")||startWithTag(str,"131")||startWithTag(str,"132")||startWithTag(str,"133")
//                    ||startWithTag(str,"155")||startWithTag(str,"156")||startWithTag(str,"185")||startWithTag(str,"186")
//                    ||startWithTag(str,"145")||startWithTag(str,"176")||startWithTag(str,"153")||startWithTag(str,"180")
//                    ||startWithTag(str,"181")||startWithTag(str,"189")||startWithTag(str,"177");
            one = startWithTag(str, "13") || startWithTag(str, "14") || startWithTag(str, "15") || startWithTag(str, "17")
                    || startWithTag(str, "18");
        }
        return one;
    }

    public static String MD5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            return plainText;
        }
    }

    public static String buildUrl(String url, Map<String, Object> params) {
        if (null == params || params.size() <= 0) {
            return url;
        }
        StringBuffer sb = new StringBuffer();
        Iterator<String> keys = params.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = params.get(key);
            if (value != null) {
                sb.append("&" + key + "=" + StringUtils.encodeUTF(value.toString()));
            }
        }
        if (isNotEmpty(url)) {
            return url + sb.replace(0, 1, "?");
        } else {
            return sb.replace(0, 1, "").toString();
        }
    }

    public static Map<String, String> parseParams(String url) {
        Map<String, String> result = new LinkedHashMap();
        String[] params = url.replaceAll("(^[^\\?]*\\?)|(#[^#]*$)", "").split("&");
        for (String keyValue : params) {
            String[] param = keyValue.split("=");
            if (param.length >= 1) {
                result.put(param[0], param.length >= 2 ? StringUtils.decodeUTF(param[1]) : "");
            }
        }
        return result;
    }


    public static String gzip(String str) {
        if (isEmpty(str)) {
            return str;
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
            gzip.close();
            return out.toString("ISO-8859-1");
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    //yyyy-MM-dd HH:mm:ss
    public static String getTimeStr(long time, String formatterType) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatterType);
        if (time > 0) {
            return formatter.format(new Date(time));
        } else {
            return formatter.format(new Date(System.currentTimeMillis()));
        }
    }

    public static String videoForTime(int timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

}
