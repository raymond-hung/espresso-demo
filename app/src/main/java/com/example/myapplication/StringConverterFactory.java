package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class StringConverterFactory extends Converter.Factory {
    private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain");
    private static final String UTF_8 = "UTF-8";
    private static final int BUFFER_SIZE = 4096;

    public static StringConverterFactory create() {
        return new StringConverterFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (String.class.equals(type)) {
            return new Converter<ResponseBody, String>() {
                @Override
                public String convert(ResponseBody value) throws IOException {
                    return getStringFrom(value);
                }
            };
        }
        return null;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        if (String.class.equals(type)) {
            return new Converter<String, RequestBody>() {
                @Override
                public RequestBody convert(String value) throws IOException {
                    return RequestBody.create(MEDIA_TYPE, value);
                }
            };
        }
        return null;
    }

    @Nullable
    private String getStringFrom(ResponseBody value) throws IOException {
        InputStream inputStream = value.byteStream();
        return getStringFrom(inputStream);
    }

    @Nullable
    private String getStringFrom(@Nullable InputStream inputStream) throws IOException {
        String result = null;
        try {
            if (inputStream != null) {
                result = writeStreamToString(inputStream);
            }
        } finally {
            closeQuietly(inputStream);
        }
        return result;
    }

    @Nullable
    private String writeStreamToString(@NonNull InputStream inputStream) throws IOException {
        String result;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            result = readStreamAndConvert(inputStream, outputStream);
        } finally {
            closeQuietly(outputStream);
        }
        return result;
    }

    @Nullable
    private String readStreamAndConvert(@NonNull InputStream inputStream, @NonNull ByteArrayOutputStream outputStream) throws IOException {
        String result;
        byte[] buffer = new byte[BUFFER_SIZE];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        result = outputStream.toString(UTF_8);
        return result;
    }

    private void closeQuietly(@Nullable Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
