package com.abt.util;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.marshalling.Marshaller;
import akka.http.javadsl.model.HttpEntity;
import akka.http.javadsl.model.RequestEntity;
import akka.http.javadsl.unmarshalling.Unmarshaller;

public class CustomJacksonSupport {

    public static <T> Unmarshaller<HttpEntity, T> customJacksonUnmarshaller(Class<T> clazz) {
        return Jackson.unmarshaller(CustomJacksonObjectMapper.mapper, clazz);
    }

    public static <T> Marshaller<T, RequestEntity> customJacksonMarshaller() {
        return Jackson.marshaller(CustomJacksonObjectMapper.mapper);
    }
}
