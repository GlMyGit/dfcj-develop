package com.dfcj.videoim.rx;

import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GsonTools {

    /**
     * 解析jsonArray
     *
     * @param in json数据
     *
     * @throws IOException
     */
    static void readArray(JsonReader in)
            throws IOException {
        in.beginArray();
        readJson(in);
        in.endArray();
    }

    /**
     * 解析jsonObject
     *
     * @param in json数据
     *
     * @throws IOException
     */
    static void readObject(JsonReader in)
            throws IOException {
        in.beginObject();
        readJson(in);
        in.endObject();
    }

    /**
     * 解析整个json数据
     *
     * @param in json数据
     *
     * @throws IOException
     */
    private static void readJson(JsonReader in)
            throws IOException {
        while (in.hasNext()) {
            if (in.peek() == JsonToken.BEGIN_ARRAY) {
                readArray(in);
            } else if (in.peek() == JsonToken.NUMBER) {
                in.nextDouble();
            } else if (in.peek() == JsonToken.STRING) {
                in.nextString();
            } else if (in.peek() == JsonToken.NULL) {
                in.nextNull();
            } else if (in.peek() == JsonToken.NAME) {
                in.nextName();
            } else if (in.peek() == JsonToken.BOOLEAN) {
                in.nextBoolean();
            } else if (in.peek() == JsonToken.BEGIN_OBJECT) {
                readObject(in);
            }
        }
    }

    /**
     * @param type 0(int.class, Integer.class ) 1(short.class, Short.class) 2(long.class,
     * Long.class) 3(double.class, Double.class) 4(float.class, Float.class)
     *
     * @return
     */
    public static TypeAdapter<Number> longAdapter(final int type) {

        return new TypeAdapter<Number>() {

            @Override
            public Number read(JsonReader in)
                    throws IOException {
                boolean isNot = false;
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    isNot = true;
                } else if (in.peek() == JsonToken.BEGIN_OBJECT) {
                    //增加判断是错误OBJECT的类型（应该是Number）,移动in的下标到结束，移动下标的代码在下方
                    GsonTools.readObject(in);
                    isNot = true;
                } else if (in.peek() == JsonToken.NAME) {
                    //增加判断是错误的name的类型（应该是Number）,移动in的下标到结束，移动下标的代码在下方
                    in.nextName();
                    isNot = true;
                } else if (in.peek() == JsonToken.BOOLEAN) {
                    //增加判断是错误的boolean的类型（应该是Number）,移动in的下标到结束，移动下标的代码在下方
                    in.nextBoolean();
                    isNot = true;
                } else if (in.peek() == JsonToken.BEGIN_ARRAY) {
                    //增加判断是错误的array的类型（应该是Number）,移动in的下标到结束，移动下标的代码在下方
                    readArray(in);
                    isNot = true;
                }
                if (isNot) {
                    switch (type) {
                        case 0:
                            return 0;
                        case 1:
                            return (short) 0;
                        case 2:
                            return 0;
                        case 3:
                            return (double) 0;
                        case 4:
                            return (float) 0;
                        default:
                            return 0;
                    }
                }
                try {
                    switch (type) {
                        case 0:
                            if (in.peek() == JsonToken.STRING) {
                                //暂不做处理
                                return toInt(in.nextString());
                            }
                            return in.nextInt();
                        case 1:
                            if (in.peek() == JsonToken.STRING) {
                                //暂不做处理
                                return toInt(in.nextString()).shortValue();
                            }
                            return (short) in.nextInt();
                        case 2:
                            if (in.peek() == JsonToken.STRING) {
                                //暂不做处理
                                return toLong(in.nextString()).longValue();
                            }
                            return in.nextLong();
                        case 3:
                            if (in.peek() == JsonToken.STRING) {
                                //暂不做处理
                                return toDouble(in.nextString()).doubleValue();
                            }
                            return in.nextDouble();
                        case 4:
                            if (in.peek() == JsonToken.STRING) {
                                //暂不做处理
                                return toFloat(in.nextString()).floatValue();
                            }
                            return (float) in.nextDouble();
                    }
                    return in.nextLong();
                } catch (NumberFormatException e) {
                    throw new JsonSyntaxException(e);
                }
            }

            @Override
            public void write(JsonWriter out, Number value)
                    throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }
                out.value(value.toString());
            }
        };
    }

    /**
     * 处理字符的适配器
     */
    public static TypeAdapter<String> stringTypeAdapter() {

        return new TypeAdapter<String>() {

            @Override
            public String read(JsonReader in)
                    throws IOException {
                JsonToken peek = in.peek();
                if (peek == JsonToken.NULL) {
                    in.nextNull();
                    return "";
                }
                if (peek == JsonToken.BOOLEAN) {
                    return Boolean.toString(in.nextBoolean());
                }
                if (in.peek() == JsonToken.BEGIN_OBJECT) {
                    GsonTools.readObject(in);
                    return "";
                }
                //增加判断是错误的name的类型（应该是object）,移动in的下标到结束，移动下标的代码在下方
                if (in.peek() == JsonToken.NAME) {
                    in.nextName();
                    return "";
                }
                //增加判断是错误的ARRAY的类型（应该是object）,移动in的下标到结束，移动下标的代码在下方
                if (in.peek() == JsonToken.BEGIN_ARRAY) {
                    GsonTools.readArray(in);
                    return "";
                }

                return in.nextString();
            }

            @Override
            public void write(JsonWriter out, String value)
                    throws IOException {
                out.value(value);
            }
        };
    }

    public static String errorTrack(JsonReader in, String type) {
        StackTraceElement[] stackTrace = Looper.getMainLooper()
                .getThread()
                .getStackTrace();
        StringBuilder stringBuilder = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            stringBuilder.append(element.getClassName() + "." + element
                    .getMethodName() + "(" + element
                    .getLineNumber() + ")\n");
        }
        try {
            stringBuilder.append("Expected a " + type + " but was " + in.peek() + " path " + in
                    .getPath());
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }



    public static int String2Int(String data) {
        int result = -1;

        try {
            result = Integer.valueOf(data);
        } catch (Exception e) {
            // TODO
        }

        return result;
    }

    /**
     * 整形转换
     *
     * @param data 输入
     * @return Integer
     */
    public static Integer toInt(String data) {
        Integer result = 0;

        try {
            result = Integer.valueOf(data);
        } catch (Exception e) {
            // TODO
        }

        return result;
    }

    /**
     * Long转换
     *
     * @param data 输入
     * @return Long
     */
    public static Long toLong(String data) {
        Long result = 0L;

        try {
            result = Long.valueOf(data);
        } catch (Exception e) {
            // TODO
        }

        return result;
    }

    /**
     * 浮点转换
     *
     * @param data 输入
     * @return Float
     */
    public static Float toFloat(String data) {
        Float result = 0.0f;

        if (data != null && data.length() > 0) {
            try {
                result = Float.valueOf(data);
            } catch (Exception e) {
                // TODO
            }
        }

        return result;
    }

    /**
     * 浮点转换
     *
     * @param data 输入
     * @return Double
     */
    public static Double toDouble(String data) {
        Double result = 0.0;

        try {
            result = Double.valueOf(data);
        } catch (Exception e) {
            // TODO
        }

        return result;
    }

    /**
     * 判断是否是有效的值，0和0.0均视为false
     *
     * @param data 输入
     * @return boolean
     */
    public static boolean isEmpty(String data) {
        String tmp = (data != null ? data.replaceAll(" ", "") : "");

        if (TextUtils.isEmpty(tmp)) {
            return true;
        } else {
            try {
                return Double.valueOf(tmp) < 0.00001;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 字符串截取
     *
     * @param in  原始数据
     * @param max 最大文字截取尺寸
     */
    public static String subString(String in, int max) {
        float j = 0;  // 半角数目
        int offset = 0;

        if (max == -1) max = Integer.MAX_VALUE;

        if (in != null && max > 0) {
            for (offset = 0; offset < in.codePointCount(0, in.length()); offset++) {
                int c = in.codePointAt(offset);

                if (j > max) {
                    break;
                } else {
                    if (c > 32 && c <= 127) {
                        j += 0.5;
                    } else if (c == 10 || c == 13) {
                        break;
                    } else {
                        j += 1;
                    }
                }
            }
        }

        if (in != null) {
            offset = offset > in.length() ? in.length() : offset;
            return in.substring(0, offset);
        } else {
            return "";
        }
    }

    /**
     * 字符串截取
     *
     * @param in           原始数据
     * @param maxLine      做多行数
     * @param eachLineSize 每行字数
     */
    public static String[] subString(String in, int maxLine, int eachLineSize) {
        String inputString = in;
        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < maxLine; i++) {
            String tmp = subString(inputString, eachLineSize);

            if (i == maxLine - 1 && i > 0) {
                tmp += ".";
            }

            arrayList.add(tmp);

            inputString = inputString.substring(Math.min(inputString.length(), tmp.length()),
                    inputString.length());
            if (inputString.startsWith("\r\n")) {
                inputString = inputString.substring(2, inputString.length());
            } else if (inputString.startsWith("\r")) {
                inputString = inputString.substring(1, inputString.length());
            } else if (inputString.startsWith("\n")) {
                inputString = inputString.substring(1, inputString.length());
            }

            if (inputString.length() == 0) {
                break;
            }
        }

        String[] ret = new String[arrayList.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = arrayList.get(i);
        }

        return ret;
    }

    public static String getString(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        return s;
    }


    public static String listToString(List<String> stringList) {
        StringBuilder content = new StringBuilder();
        if (stringList != null && stringList.size() > 0) {
            for (int i = 0; i < stringList.size(); i++) {
                if (!TextUtils.isEmpty(stringList.get(i))) {
                    content.append(stringList.get(i));
                    content.append(",");
                }
            }
        } else {
            return "";
        }
        return content.toString().length() > 0 ? content.toString().substring(0, content.length() - 1) : "";
    }



}
