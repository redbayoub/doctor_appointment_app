package com.pro0inter.heydoc.api.Error;

import com.pro0inter.heydoc.api.ServiceGenerator;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by root on 5/1/19.
 */

public class ErrorUtils {
    public static RestErrorResponse parseError(Response<?> response) {
        Converter<ResponseBody, RestErrorResponse> converter =
                ServiceGenerator.retrofit.responseBodyConverter(RestErrorResponse.class, new Annotation[0]);

        RestErrorResponse apiError;

        try {
            apiError = converter.convert(response.errorBody());
        } catch (IOException e) {
            apiError = new RestErrorResponse();
        }

        return apiError;
    }
}
