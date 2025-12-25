package org.nahap.library.reader.ui

import android.annotation.SuppressLint
import android. content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import org.nahap.library.reader.model.ReadingSettings

class ReaderWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "ReaderWebView"
    }

    private var onPositionChanged: ((Double) -> Unit)?  = null
    private var onPageInfoChanged: ((String) -> Unit)?  = null
    private var currentSettings: ReadingSettings = ReadingSettings()
    private var isPaginated = false
    private var isPageLoaded = false

    init {
        setupWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        settings. apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = false
            useWideViewPort = true
            builtInZoomControls = false
            displayZoomControls = false
            setSupportZoom(false)
            textZoom = 100
        }

        setInitialScale(100)
        addJavascriptInterface(WebAppInterface(), "Android")

        webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d(TAG, "JS: ${consoleMessage.message()}")
                return true
            }
        }

        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isPageLoaded = true
                Log.d(TAG, " Page loaded")

                postDelayed({
                    applySettings(currentSettings)
                    injectScrollTracker()
                }, 100)
            }
        }
    }

    fun loadBookContent(htmlContent: String) {
        isPageLoaded = false
        loadDataWithBaseURL(
            "file:///android_asset/",
            htmlContent,
            "text/html",
            "UTF-8",
            null
        )
    }


    fun applySettings(settings: ReadingSettings) {
        Log. d(TAG, " APPLY: fontSize=${settings.fontSize}, line=${settings.lineHeight}")

        currentSettings = settings
        isPaginated = settings.flow == ReadingSettings.ReadingFlow.PAGINATED

        if (! isPageLoaded) {
            Log. w(TAG, "⚠ Page not loaded, skipping")
            return
        }

        val fontSize = settings. fontSize.toInt()
        val lineHeight = settings.lineHeight
        val bgColor = if (settings. theme == ReadingSettings.ReadingTheme.DARK) "#1A1A1A" else "#FFFFFF"
        val textColor = if (settings.theme == ReadingSettings.ReadingTheme.DARK) "#E0E0E0" else "#000000"
        val maxWidth = settings.maxInlineSize

        val jsCode = """
            (function() {
              
                var scrollTop = window.pageYOffset || document.documentElement.scrollTop;
                var docHeight = document.documentElement. scrollHeight - window. innerHeight;
                var scrollFraction = docHeight > 0 ? scrollTop / docHeight : 0;
                console.log(' Save position: ' + (scrollFraction * 100).toFixed(1) + '%');
                
             
                var old = document.getElementById('dynamic-style');
                if (old) old. remove();
                
               
                var style = document.createElement('style');
                style.id = 'dynamic-style';
                style.innerHTML = 
                    '*, p, div, span, li, blockquote { ' +
                        'font-size: ${fontSize}px ! important; ' +
                        'line-height: ${lineHeight} !important; ' +
                    '}' +
                    'html, body { ' +
                        'background-color: ${bgColor} !important; ' +
                        'color: ${textColor} !important; ' +
                    '}' +
                    'body { ' +
                        'max-width: ${maxWidth}px !important; ' +
                        'margin: 0 auto !important; ' +
                        'padding: 16px !important; ' +
                    '}' +
                    '. title { ' +
                        'font-size: ${(fontSize * 1.5).toInt()}px !important; ' +
                        'font-weight: 800 !important; ' +
                    '}' +
                    'h1 { font-size: ${(fontSize * 1.6).toInt()}px !important; font-weight: 700 !important; }' +
                    'h2 { font-size: ${(fontSize * 1.4).toInt()}px !important; font-weight: 700 !important; }' +
                    'h3 { font-size: ${(fontSize * 1.2).toInt()}px !important; font-weight: 700 !important; }';
                
                document.head.appendChild(style);
                
               
                requestAnimationFrame(function() {
                    requestAnimationFrame(function() {
                        var newDocHeight = document.documentElement.scrollHeight - window.innerHeight;
                        var newScrollTop = Math.round(scrollFraction * newDocHeight);
                        window.scrollTo(0, newScrollTop);
                        console.log(' Restored to: ' + (scrollFraction * 100).toFixed(1) + '%');
                        
                        setTimeout(function() {
                            if (typeof window.updatePosition === 'function') {
                                window.updatePosition();
                            }
                        }, 50);
                    });
                });
                
                return 'OK';
            })();
        """. trimIndent()

        evaluateJavascript(jsCode) { result ->
            Log.d(TAG, "Result: $result")
        }
    }

    fun scrollToElement(elementId: String) {
        Log.d(TAG, " Scrolling to: $elementId")

        evaluateJavascript("""
            (function() {
                var el = document.getElementById('$elementId');
                if (el) {
                    el.scrollIntoView({ behavior: 'smooth', block: 'start' });
                    return 'found';
                }
                console.error('Element not found: $elementId');
                return 'not found';
            })();
        """. trimIndent()) { result ->
            Log.d(TAG, " Scroll result: $result")
        }
    }

    fun scrollToFraction(fraction: Double) {
        evaluateJavascript("""
            (function() {
                var max = document.documentElement.scrollHeight - window.innerHeight;
                window.scrollTo({ top: max * $fraction, behavior: 'smooth' });
            })();
        """.trimIndent(), null)
    }

    fun nextPage() {
        evaluateJavascript("window.scrollBy({top: window.innerHeight * 0. 9, behavior: 'smooth'});", null)
    }

    fun prevPage() {
        evaluateJavascript("window.scrollBy({top: -window.innerHeight * 0.9, behavior: 'smooth'});", null)
    }

    fun getCurrentPosition(callback: (Double) -> Unit) {
        evaluateJavascript("""
            (function() {
                var st = window.pageYOffset || document. documentElement.scrollTop;
                var max = document.documentElement.scrollHeight - window.innerHeight;
                return max > 0 ? st / max : 0;
            })();
        """.trimIndent()) { result ->
            val pos = result?. trim()?.removeSurrounding("\"")?.toDoubleOrNull() ?: 0.0
            callback(pos)
        }
    }

    private fun injectScrollTracker() {
        evaluateJavascript("""
            (function() {
                var lastPos = 0;
                var timer;
                
                window.updatePosition = function() {
                    var st = window.pageYOffset || document. documentElement.scrollTop;
                    var max = document.documentElement.scrollHeight - window.innerHeight;
                    var pos = max > 0 ? st / max : 0;
                    
                    if (Math.abs(pos - lastPos) > 0.001) {
                        lastPos = pos;
                        Android.onScrollPositionChanged(pos);
                    }
                    
                    var cp = Math.floor(st / window.innerHeight) + 1;
                    var tp = Math.ceil(document.documentElement.scrollHeight / window.innerHeight);
                    Android.onPageInfoChanged(cp + ' / ' + tp);
                };
                
                window.removeEventListener('scroll', window._scrollHandler);
                window._scrollHandler = function() {
                    clearTimeout(timer);
                    timer = setTimeout(window.updatePosition, 250);
                };
                window.addEventListener('scroll', window._scrollHandler, {passive: true});
                
                window.updatePosition();
            })();
        """.trimIndent(), null)
    }

    inner class WebAppInterface {
        @JavascriptInterface
        fun onScrollPositionChanged(position: Double) {
            post { onPositionChanged?. invoke(position) }
        }

        @JavascriptInterface
        fun onPageInfoChanged(pageInfo: String) {
            post { onPageInfoChanged?.invoke(pageInfo) }
        }
    }

    fun setOnPositionChangedListener(listener: (Double) -> Unit) {
        onPositionChanged = listener
    }

    fun setOnPageInfoChangedListener(listener: (String) -> Unit) {
        onPageInfoChanged = listener
    }
}