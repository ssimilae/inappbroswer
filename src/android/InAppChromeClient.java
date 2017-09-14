/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package org.apache.cordova.inappbrowser;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.GeolocationPermissions.Callback;
import android.app.Activity;
import android.content.Context;
import android.widget.FrameLayout;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup ;
import 	android.view.MotionEvent;

public final class InAppChromeClient extends WebChromeClient {
    private View mCustomView;
    private Activity mActivity;
    
    public InAppChromeClient(Activity activity) {
        this.mActivity = activity;
    }
    
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        result.confirm();
        return super.onJsAlert(view, url, message, result);
    }
    
     private int mOriginalOrientation;
     private FullscreenHolder mFullscreenContainer;
     private CustomViewCallback mCustomViewCollback;
 
     @Override
     public void onShowCustomView(View view, CustomViewCallback callback) {
 
         if (mCustomView != null) {
             callback.onCustomViewHidden();
             return;
         }
 
         mOriginalOrientation = mActivity.getRequestedOrientation();
 
         FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
 
         mFullscreenContainer = new FullscreenHolder(mActivity);
         mFullscreenContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT);
         decor.addView(mFullscreenContainer, ViewGroup.LayoutParams.MATCH_PARENT);
         mCustomView = view;
         mCustomViewCollback = callback;
         mActivity.setRequestedOrientation(mOriginalOrientation);
 
     }
 
     @Override
     public void onHideCustomView() {
         if (mCustomView == null) {
             return;
         }
 
         FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
         decor.removeView(mFullscreenContainer);
         mFullscreenContainer = null;
         mCustomView = null;
         mCustomViewCollback.onCustomViewHidden();
    
         mActivity.setRequestedOrientation(mOriginalOrientation);
     }
 
 
     static class FullscreenHolder extends FrameLayout {
 
         public FullscreenHolder(Context ctx) {
             super(ctx);
             setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
         }
    
         @Override
         public boolean onTouchEvent(MotionEvent evt) {
             return true;
         }
    }
}


 