/*  Copyright (c) 2014, Leo Kuznetsov <Leo.Kuznetsov@gmail.com>
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

 * Neither the name of the {organization} nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.undeadlifts;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.webkit.*;

import java.io.*;

import static android.view.ViewGroup.LayoutParams.*;

@SuppressWarnings("NullableProblems")
public class Act extends Activity {

    private Handler handler = new Handler();
    private View cv;
    private WebView wv;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cv = new Splash(this);
        setContentView(cv);
    }

    @SuppressLint("SetJavaScriptEnabled")

    private void init() {
        wv = new WebView(this);
        wv.setLayoutParams(new ViewGroup.LayoutParams(FILL_PARENT, FILL_PARENT));
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setDatabaseEnabled(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setBuiltInZoomControls(false);
        wv.getSettings().setAppCacheEnabled(true);
        wv.getSettings().setAppCacheMaxSize(100 * 1024 * 1024); // 100MB
        wv.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, final String url) {
                if (cv instanceof Splash) {
                    cv = wv;
                    handler.postDelayed(new Runnable() { public void run() { setContentView(cv); } }, 750);
                }
            }
        });
        wv.loadUrl("https://cleanlifts.firebaseapp.com/#");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv != null && wv.canGoBack()) {
            wv.goBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    private class Splash extends View {

        private boolean posted;
        private Bitmap bitmap;

        public Splash(Context context) {
            super(context);
            setBackgroundColor(Color.BLACK);
        }

        public void draw(Canvas c) {
            super.draw(c);
            if (!posted) {
                handler.post(new Runnable() { public void run() { init(); } });
                posted = true;
            }
            if (bitmap == null) {
                InputStream is = null;
                try {
                    is = getResources() != null ? getResources().openRawResource(R.drawable.undeadlifts) : null;
                    bitmap = is != null ? BitmapFactory.decodeStream(is) : null;
                } finally {
                    if (is != null) {
                        try { is.close(); } catch (IOException e) { /* ignore */ }
                    }
                }
            }
            if (bitmap != null) {
                int x = (getWidth() - bitmap.getWidth()) / 2;
                int y = (getHeight() - bitmap.getHeight()) / 2;
                c.drawBitmap(bitmap, x, y, null);
            }
        }

    }

}
