package com.fly.tkuilife.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.fly.tkuilife.activity.ActivityAuthorization;
import com.fly.tkuilife.activity.ActivitySplash;

public class AppFrontBackHelper {

    private OnAppStatusListener onAppStatusListener;

    public AppFrontBackHelper(){

    }

    public void register(Application application, OnAppStatusListener onAppStatusListener){
        this.onAppStatusListener = onAppStatusListener;
        application.registerActivityLifecycleCallbacks(this.activityLifecycleCallbacks);
    }
    public void unregister(Application application){
        application.unregisterActivityLifecycleCallbacks(this.activityLifecycleCallbacks);
    }

    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {

        private int activityStartCount = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (!activity.getClass().equals(ActivitySplash.class)) {
                activityStartCount++;
                if (activityStartCount == 1) {
                    if (onAppStatusListener != null) {
                        onAppStatusListener.onFront();
                    }
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (!activity.getClass().equals(ActivitySplash.class)) {
                activityStartCount--;
                if (activityStartCount == 0) {
                    if (onAppStatusListener != null) {
                        onAppStatusListener.onBack();
                    }
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    };


    public interface OnAppStatusListener{
        void onFront();
        void onBack();
    }
}
