package com.demo.photogallerytest.utils;

import rx.Observable;
import rx.Observable.Transformer;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * RxUtils class uses for the transformer of the RxAndroid and RxJava. Transformer using the apply
 * Scheduler, subscribe , observer
 */

/**
 * Created by jaivignesh.m.jt on 9/26/2016.
 */
public class RxUtils {
    private RxUtils() throws IllegalAccessException {
        throw new IllegalAccessException("No Instances!");
    }

    @SuppressWarnings("WeakerAccess")
    public static <T> Transformer<T, T> applySchedulers(final Scheduler observeOnScheduler) {
        return new Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.observeOn(observeOnScheduler).subscribeOn(Schedulers.io());
            }
        };
    }

    public static <T> Transformer<T, T> applySchedulers() {
        return applySchedulers(mainThread());
    }

   /* public static <T> Transformer<T, T> raiseErrorIfFailed() {
        return new Transformer<Meta<T>, Meta<T>>() {
            @Override
            public Observable<Meta<T>> call(Observable<Meta<T>> metaObservable) {
                return metaObservable.map(new Func1<T, T>() {
                    @Override
                    public T call(T meta) {
                        if (meta != null) {
                            String error = "Error messag eto be displayed";
                            if (TextUtils.isEmpty(error) || error.equalsIgnoreCase("null")) {
                                error = "Server Error, Please try again";
                            }
                            throw new ApiFailException(error);
                        }
                        return meta;
                    }
                });
            }
        };*/
    // }
}
