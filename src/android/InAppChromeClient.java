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

public class InAppChromeClient extends WebChromeClient {

    private CordovaWebView webView;


    private Activity mActivity;



private MyWebChromeClient mWebChromeClient = null;
private View mCustomView;
private RelativeLayout mContentView;
private FrameLayout mCustomViewContainer;
private WebChromeClient.CustomViewCallback mCustomViewCallback;


    private String LOG_TAG = "InAppChromeClient";
    private long MAX_QUOTA = 100 * 1024 * 1024;

    public InAppChromeClient(CordovaWebView webView, Activity activity) {
        super();
        this.webView = webView; 
        this.mActivity = activity;
  
    }
    /**
     * Handle database quota exceeded notification.
     *
     * @param url
     * @param databaseIdentifier
     * @param currentQuota
     * @param estimatedSize
     * @param totalUsedQuota
     * @param quotaUpdater
     */
    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize,
            long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater)
    {
        LOG.d(LOG_TAG, "onExceededDatabaseQuota estimatedSize: %d  currentQuota: %d  totalUsedQuota: %d", estimatedSize, currentQuota, totalUsedQuota);
        quotaUpdater.updateQuota(MAX_QUOTA);
    }

    /**
     * Instructs the client to show a prompt to ask the user to set the Geolocation permission state for the specified origin.
     *
     * @param origin
     * @param callback
     */
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
        callback.invoke(origin, true, false);
    }

    /**
     * Tell the client to display a prompt dialog to the user.
     * If the client returns true, WebView will assume that the client will
     * handle the prompt dialog and call the appropriate JsPromptResult method.
     *
     * The prompt bridge provided for the InAppBrowser is capable of executing any
     * oustanding callback belonging to the InAppBrowser plugin. Care has been
     * taken that other callbacks cannot be triggered, and that no other code
     * execution is possible.
     *
     * To trigger the bridge, the prompt default value should be of the form:
     *
     * gap-iab://<callbackId>
     *
     * where <callbackId> is the string id of the callback to trigger (something
     * like "InAppBrowser0123456789")
     *
     * If present, the prompt message is expected to be a JSON-encoded value to
     * pass to the callback. A JSON_EXCEPTION is returned if the JSON is invalid.
     *
     * @param view
     * @param url
     * @param message
     * @param defaultValue
     * @param result
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        // See if the prompt string uses the 'gap-iab' protocol. If so, the remainder should be the id of a callback to execute.
        if (defaultValue != null && defaultValue.startsWith("gap")) {
            if(defaultValue.startsWith("gap-iab://")) {
                PluginResult scriptResult;
                String scriptCallbackId = defaultValue.substring(10);
                if (scriptCallbackId.startsWith("InAppBrowser")) {
                    if(message == null || message.length() == 0) {
                        scriptResult = new PluginResult(PluginResult.Status.OK, new JSONArray());
                    } else {
                        try {
                            scriptResult = new PluginResult(PluginResult.Status.OK, new JSONArray(message));
                        } catch(JSONException e) {
                            scriptResult = new PluginResult(PluginResult.Status.JSON_EXCEPTION, e.getMessage());
                        }
                    }
                    this.webView.sendPluginResult(scriptResult, scriptCallbackId);
                    result.confirm("");
                    return true;
                }
            }
            else
            {
                // Anything else with a gap: prefix should get this message
                LOG.w(LOG_TAG, "InAppBrowser does not support Cordova API calls: " + url + " " + defaultValue); 
                result.cancel();
                return true;
            }
        }
        return false;
    }

	@Override
    public void onCloseWindow(WebView window) {
         super.onCloseWindow(window);
    }

	


	 private int mOriginalOrientation;
     private FullscreenHolder mFullscreenContainer;
     private CustomViewCallback mCustomViewCollback;
 
     @Override
     public void onShowCustomView(View view, CustomViewCallback callback) {
		/*
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

		*/

		//

		  // if a view already exists then immediately terminate the new one
        if (mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }
        mContentView = (RelativeLayout) findViewById(R.id.activity_main);
        mContentView.setVisibility(View.GONE);
        mCustomViewContainer = new FrameLayout(MainActivity.this);
        mCustomViewContainer.setLayoutParams(LayoutParameters);
        mCustomViewContainer.setBackgroundResource(android.R.color.black);
        view.setLayoutParams(LayoutParameters);
        mCustomViewContainer.addView(view);
        mCustomView = view;
        mCustomViewCallback = callback;
        mCustomViewContainer.setVisibility(View.VISIBLE);
        setContentView(mCustomViewContainer);
 
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
