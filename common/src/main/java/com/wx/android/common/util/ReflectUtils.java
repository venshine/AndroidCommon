/*
 * Copyright (C) 2016 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wx.android.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 反射操作
 *
 * @author venshine
 */
public class ReflectUtils {

    /**
     * create new instance, use default constructor
     *
     * @param clazz
     * @return
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static Object newInstance(Class<?> clazz) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        return newInstance(clazz, null, null);
    }

    /**
     * create new instance, use custom constructor with arguments
     *
     * @param clazz
     * @param parameterTypes
     * @param initargs
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static Object newInstance(Class<?> clazz, Class<?>[] parameterTypes, Object[] initargs) throws
            NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (clazz == null) {
            throw new IllegalArgumentException("arguments cannot be null.");
        }
        if (parameterTypes != null && initargs != null && parameterTypes.length > 0 && initargs.length > 0) {
            if (parameterTypes.length == initargs.length) {
                Constructor<?> constructor = clazz.getDeclaredConstructor(parameterTypes);
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                return constructor.newInstance(initargs);
            }
        } else {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        }
        return null;
    }

    /**
     * get field value
     *
     * @param clazz
     * @param obj
     * @param name
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Object getField(Class<?> clazz, Object obj, String name) throws NoSuchFieldException,
            IllegalAccessException {
        if (clazz == null || obj == null || name == null) {
            throw new IllegalArgumentException("arguments cannot be null.");
        }
        Field field = clazz.getDeclaredField(name);
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        return field.get(obj);
    }

    /**
     * get static field value
     *
     * @param clazz
     * @param name
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Object getStaticField(Class<?> clazz, String name) throws NoSuchFieldException,
            IllegalAccessException {
        return getField(clazz, clazz, name);
    }

    /**
     * set value in field
     *
     * @param clazz
     * @param obj
     * @param name
     * @param value
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static void setField(Class<?> clazz, Object obj, String name, Object value) throws NoSuchFieldException,
            IllegalAccessException {
        if (clazz == null || obj == null || name == null || value == null) {
            throw new IllegalArgumentException("arguments cannot be null.");
        }
        Field field = clazz.getDeclaredField(name);
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        field.set(obj, value);
    }

    /**
     * set value in static field
     *
     * @param clazz
     * @param name
     * @param value
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static void setStaticField(Class<?> clazz, String name, Object value) throws NoSuchFieldException,
            IllegalAccessException {
        if (clazz == null || name == null || value == null) {
            throw new IllegalArgumentException("arguments cannot be null.");
        }
        Field field = clazz.getDeclaredField(name);
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(clazz, value);
    }

    /**
     * invoke method without arguments
     *
     * @param clazz
     * @param obj
     * @param name
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokeMethod(Class<?> clazz, Object obj, String name) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        return invokeMethod(clazz, obj, name, null, null);
    }

    /**
     * invoke static method without arguments
     *
     * @param clazz
     * @param name
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokeStaticMethod(Class<?> clazz, String name) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        return invokeStaticMethod(clazz, name, null, null);
    }

    /**
     * invoke static method with arguments
     *
     * @param clazz
     * @param name
     * @param parameterTypes
     * @param args
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokeStaticMethod(Class<?> clazz, String name, Class<?>[] parameterTypes, Object[]
            args) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        return invokeMethod(clazz, clazz, name, parameterTypes, args);
    }

    /**
     * invoke method with arguments
     *
     * @param clazz
     * @param obj
     * @param name
     * @param parameterTypes
     * @param args
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokeMethod(Class<?> clazz, Object obj, String name, Class<?>[] parameterTypes, Object[]
            args) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        if (clazz == null || obj == null || name == null) {
            throw new IllegalArgumentException("arguments cannot be null.");
        }
        if (parameterTypes != null && args != null && parameterTypes.length > 0 && args.length > 0) {
            if (parameterTypes.length == args.length) {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                return method.invoke(obj, args);
            }
        } else {
            Method method = clazz.getDeclaredMethod(name);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method.invoke(obj);
        }
        return null;
    }

}
