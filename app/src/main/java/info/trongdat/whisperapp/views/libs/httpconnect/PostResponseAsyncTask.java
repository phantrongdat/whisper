package info.trongdat.whisperapp.views.libs.httpconnect;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Alone on 4/30/2017.
 */

public class PostResponseAsyncTask extends AsyncTask<String, Void, String> {
    private String LOG = "PostResponseAsyncTask";
//    private ProgressDialog progressDialog;
    private AsyncResponse asyncResponse;
    private Context context;
    private HashMap<String, String> postData = new HashMap();
    private String loadingMessage = "Loading...";
    private boolean showLoadingMessage = true;
    private ExceptionHandler exceptionHandler;
    private EachExceptionsHandler eachExceptionsHandler;
    private Exception exception = new Exception();

    public PostResponseAsyncTask(Context context, AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
        this.context = context;
    }

    public PostResponseAsyncTask(Context context, boolean showLoadingMessage, AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
        this.context = context;
        this.showLoadingMessage = showLoadingMessage;
    }

    public PostResponseAsyncTask(Context context, HashMap<String, String> postData, AsyncResponse asyncResponse) {
        this.context = context;
        this.postData = postData;
        this.asyncResponse = asyncResponse;
    }

    public PostResponseAsyncTask(Context context, HashMap<String, String> postData, boolean showLoadingMessage, AsyncResponse asyncResponse) {
        this.context = context;
        this.postData = postData;
        this.asyncResponse = asyncResponse;
        this.showLoadingMessage = showLoadingMessage;
    }

    public PostResponseAsyncTask(Context context, String loadingMessage, AsyncResponse asyncResponse) {
        this.context = context;
        this.loadingMessage = loadingMessage;
        this.asyncResponse = asyncResponse;
    }

    public PostResponseAsyncTask(Context context, HashMap<String, String> postData, String loadingMessage, AsyncResponse asyncResponse) {
        this.context = context;
        this.postData = postData;
        this.loadingMessage = loadingMessage;
        this.asyncResponse = asyncResponse;
    }

    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }

    public HashMap<String, String> getPostData() {
        return this.postData;
    }

    public void setPostData(HashMap<String, String> postData) {
        this.postData = postData;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void setEachExceptionsHandler(EachExceptionsHandler eachExceptionsHandler) {
        this.eachExceptionsHandler = eachExceptionsHandler;
    }

    public String getLoadingMessage() {
        return this.loadingMessage;
    }

    public Context getContext() {
        return this.context;
    }

    public AsyncResponse getAsyncResponse() {
        return this.asyncResponse;
    }

    protected void onPreExecute() {
        if(this.showLoadingMessage) {
//            this.progressDialog = new ProgressDialog(this.context);
//            this.progressDialog.setMessage(this.loadingMessage);
//            this.progressDialog.show();
        }

        super.onPreExecute();
    }

    protected String doInBackground(String... urls) {
        String result = "";

        for(int i = 0; i <= 0; ++i) {
            result = this.invokePost(urls[i], this.postData);
        }

        return result;
    }

    private String invokePost(String requestURL, HashMap<String, String> postDataParams) {
        String response = "";

        try {
            URL url = new URL(requestURL);
            HttpURLConnection e = (HttpURLConnection)url.openConnection();
            e.setReadTimeout(15000);
            e.setConnectTimeout(15000);
            e.setRequestMethod("POST");
            e.setDoInput(true);
            e.setDoOutput(true);
            OutputStream os = e.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(this.getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();
            int responseCode = e.getResponseCode();
            String line;
            if(responseCode == 200) {
                for(BufferedReader br = new BufferedReader(new InputStreamReader(e.getInputStream())); (line = br.readLine()) != null; response = response + line) {
                    ;
                }
            } else {
                response = "";
                Log.d("PostResponseAsyncTask", responseCode + "");
            }
        } catch (MalformedURLException var11) {
            Log.d("PostResponseAsyncTask", "MalformedURLException Error: " + var11.toString());
            this.exception = var11;
        } catch (ProtocolException var12) {
            Log.d("PostResponseAsyncTask", "ProtocolException Error: " + var12.toString());
            this.exception = var12;
        } catch (UnsupportedEncodingException var13) {
            Log.d("PostResponseAsyncTask", "UnsupportedEncodingException Error: " + var13.toString());
            this.exception = var13;
        } catch (IOException var14) {
            Log.d("PostResponseAsyncTask", "IOException Error: " + var14.toString());
            this.exception = var14;
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator var4 = params.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry entry = (Map.Entry)var4.next();
            if(first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String)entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    protected void onPostExecute(String result) {
//        if(this.showLoadingMessage && this.progressDialog.isShowing()) {
//            this.progressDialog.dismiss();
//        }

        result = result.trim();
        if(this.asyncResponse != null) {
            this.asyncResponse.processFinish(result);
        }

        if(this.exception != null) {
            if(this.exceptionHandler != null) {
                this.exceptionHandler.handleException(this.exception);
            }

            if(this.eachExceptionsHandler != null) {
                Log.d(this.LOG, "" + this.exception.getClass().getSimpleName());
                if(this.exception instanceof MalformedURLException) {
                    this.eachExceptionsHandler.handleMalformedURLException((MalformedURLException)this.exception);
                } else if(this.exception instanceof ProtocolException) {
                    this.eachExceptionsHandler.handleProtocolException((ProtocolException)this.exception);
                } else if(this.exception instanceof UnsupportedEncodingException) {
                    this.eachExceptionsHandler.handleUnsupportedEncodingException((UnsupportedEncodingException)this.exception);
                } else if(this.exception instanceof IOException) {
                    this.eachExceptionsHandler.handleIOException((IOException)this.exception);
                }
            }
        }

    }

    /** @deprecated */
    @Deprecated
    public PostResponseAsyncTask(AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
        this.context = (Context)asyncResponse;
    }

    /** @deprecated */
    @Deprecated
    public PostResponseAsyncTask(AsyncResponse asyncResponse, HashMap<String, String> postData) {
        this.asyncResponse = asyncResponse;
//        this.context = (Context)asyncResponse;
        this.postData = postData;
    }

    /** @deprecated */
    @Deprecated
    public PostResponseAsyncTask(AsyncResponse asyncResponse, String loadingMessage) {
        this.asyncResponse = asyncResponse;
        this.context = (Context)asyncResponse;
        this.loadingMessage = loadingMessage;
    }

    /** @deprecated */
    @Deprecated
    public PostResponseAsyncTask(AsyncResponse asyncResponse, HashMap<String, String> postData, String loadingMessage) {
        this.asyncResponse = asyncResponse;
        this.context = (Context)asyncResponse;
        this.postData = postData;
        this.loadingMessage = loadingMessage;
    }
}
