package com.dfcj.videoim.rx;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

public class DateNullAdapter extends TypeAdapter<Date>{

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings("unchecked") // we use a runtime check to make sure the 'T's equal
        @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return typeToken.getRawType() == Date.class ? (TypeAdapter<T>) new DateTypeAdapter() : null;
        }
    };

    private final DateFormat enUsFormat
            = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US);
    private final DateFormat localFormat
            = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);

    @Override public Date read(JsonReader in) throws IOException {

        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String jsonStr = in.nextString();
        if(TextUtils.isEmpty(jsonStr)) {
            return null;
        }else {
            return deserializeToDate(jsonStr);
        }
    }

    private synchronized Date deserializeToDate(String json) {
        try {
            return localFormat.parse(json);
        } catch (Exception ignored) {
        }
        try {
            return enUsFormat.parse(json);
        } catch (Exception ignored) {
        }
        try {
            return ISO8601Utils.parse(json, new ParsePosition(0));
        } catch (Exception e) {
            throw new JsonSyntaxException(json, e);
        }
    }

    @Override public synchronized void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        String dateFormatAsString = enUsFormat.format(value);
        out.value(dateFormatAsString);
    }

}
