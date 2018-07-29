package com.demskigroup.guitaramps.utility;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * <h1>OkHttp3Connection</h1>
 * <P>
 *     this class handel all the service call in this app using okhttp3 class provided
 *     by the third party lib.
 * </P>
 * @author 3Embed.
 * @since  10/6/16.
 */
public class OkHttp3Connection
{
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    /**
     * <h1>OkHttpRequestData</h1>
     * <p>
     *     Class is use to hold three parameter i.e String object,RequestBody object and OkHttpRequestCallback
     *     in a single place.
     *     Because async Task takes only single parameter nad i have to send three parameter so.
     *     Wrapping three things into a single object and sending one object to async task.
     * </p>
     * @see RequestBody*/
    private static  class OkHttpRequestData
    {
        String Tag;
        String request_Url;
        String header;
        JSONObject requestBody;
        OkHttp3RequestCallback callbacks;
        Request_type request_type;
    }

    /**
     * Contains the result  data Packet with the given tag from the user to provide the
     * right destination.*/
    private static class DataPacket
    {
        String result_data;
        String Tag;
        String header;
        boolean isError=false;
        OkHttp3RequestCallback callbacks_packet;
    }
    /**
     * <h2>doOkhttpRequest</h2>
     * <p>
     * This method receive all the data and Store then into to single
     * array of class
     * Service Call using okHttp Request.
     * </p>
     * <p>
     *     this Method Take a Request Body and a url,and OkHttpRequestCallback and does a Asyntask,
     *     and does a request to the given Url
     * </p>
     * @param TAG contains the Tag for the the calling Service extra tag.
     * @param request_Url contains the url of the given Service link to do performance.
     * @param requestBody contains the require data to send the given Url link.
     * @param callbacks contains the reference to set the call back response to the calling class.*/
    public static void doOkHttp3Connection(String TAG, String request_Url, Request_type request_type, JSONObject requestBody, OkHttp3RequestCallback callbacks)
    {
        try {
            requestBody.put(VariableConstants.BODY_AUT_KEY,VariableConstants.BODY_AUT_PASSWORD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        printLog("Request_data"+" "+requestBody);
        OkHttpRequestData data = new OkHttpRequestData();
        data.request_Url = request_Url;
        data.requestBody = requestBody;
        data.callbacks = callbacks;
        data.request_type=request_type;
        data.Tag=TAG;
        /*
         * Calling the Async task to perform the Service call.*/
        new OkHttpRequest().execute(data);
    }




    /**
     * <h1>OkHttpRequest</h1>
     * OkHttpRequest extends async task to perform the function indecently .
     * Does a service call using OkHttp client.
     * <P>
     *     This class extends async task and override the method of async task .
     *     on doInBackground method of async task.
     *     performing a service call to th given url and sending data given to the class.
     *     By the help of the OkHttpClient and sending the call back method to the calling Activity by setting
     *     data to the given reference of call-Back Interface object.
     * </P>
     * If Any thing Happened to the service call like Connection Failed or any thin else.
     * Telling to the User that connection is too slow when handling Exception.
     *@see Response
     * @see OkHttpClient
     * */
    private  static class OkHttpRequest extends AsyncTask<OkHttpRequestData, Void, DataPacket>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected DataPacket doInBackground(OkHttpRequestData... params)
        {
            DataPacket dataPacket=new DataPacket();
            dataPacket.callbacks_packet =params[0].callbacks;
            dataPacket.isError=false;
            dataPacket.Tag=params[0].Tag;
            dataPacket.header = params[0].header;

            String result;
            try
            {
                OkHttpClient.Builder builder=new OkHttpClient.Builder();
                builder.connectTimeout(60, TimeUnit.SECONDS);
                builder.readTimeout(60, TimeUnit.SECONDS);
                builder.writeTimeout(60, TimeUnit.SECONDS);
//                builder.addInterceptor(new BasicAuthInterceptor(VariableConstants.BODY_AUT_KEY,VariableConstants.BODY_AUT_PASSWORD));
                OkHttpClient httpClient=getSafeOkHttpClient( builder);
                Request request;

                if(params[0].request_type.equals(Request_type.URl))
                {
                    String url=getUrl(params[0].request_Url,params[0].requestBody);
                    request = new Request.Builder()
                            .url(url)
                            .build();
                }
                // post request
                else if(params[0].request_type.equals(Request_type.POST))
                {
                    RequestBody body = RequestBody.create(JSON,params[0].requestBody.toString());
                    request = new Request.Builder()
                            .url(params[0].request_Url)
                            .header("Content-Type", "application/json; Charset=UTF-8")
                            .header( "Authorization","Basic YmFzaWNBdXRoOiZqbm8tQDhhej13U28qTkhZVkdwRl5BUT80eW4zNlp2VzVUb1VDVU4rWEdPdUM/c3ojU0Ukb3hYVmJ3UUdQfDNXRnlqY1RBajJTSVJRbkxFfHZvXi18LUFUVjVGWlVmMio1QTNPaXV8X0VPTW1HPT0maUFwelFMM1I3SEhRaj9qdGIwbWMybVQkWSVJc3Jncnh2ZWxkI1peZzMtdWxefDB4QUlUZ2FuSXVGMjNKMHdhU2E2ejZhUF8rJURlNUxxdHVZJnB0eD9xaFp5U0VDZHlFXio0Ul5iKmhGalEtOT9jQ1NKTmZST3p6dEVZYlJ5Tj1TcUR5aGhwelNtbVB8RWI=" )
                            .post(body)
                            .build();
                }
                // put request
                else if (params[0].request_type.equals(Request_type.PUT))
                {
                    RequestBody body = RequestBody.create(JSON,params[0].requestBody.toString());
                    request = new Request.Builder()
                            .url(params[0].request_Url)
                            .addHeader("Content-Type", "application/json; Charset=UTF-8")
                            .header( "Authorization","Basic YmFzaWNBdXRoOiZqbm8tQDhhej13U28qTkhZVkdwRl5BUT80eW4zNlp2VzVUb1VDVU4rWEdPdUM/c3ojU0Ukb3hYVmJ3UUdQfDNXRnlqY1RBajJTSVJRbkxFfHZvXi18LUFUVjVGWlVmMio1QTNPaXV8X0VPTW1HPT0maUFwelFMM1I3SEhRaj9qdGIwbWMybVQkWSVJc3Jncnh2ZWxkI1peZzMtdWxefDB4QUlUZ2FuSXVGMjNKMHdhU2E2ejZhUF8rJURlNUxxdHVZJnB0eD9xaFp5U0VDZHlFXio0Ul5iKmhGalEtOT9jQ1NKTmZST3p6dEVZYlJ5Tj1TcUR5aGhwelNtbVB8RWI=" )
                            .put(body)
                            .build();
                }
                // delete request
                else if (params[0].request_type.equals(Request_type.DELETE))
                {
                    RequestBody body =RequestBody.create(JSON,params[0].requestBody.toString());
                    request=new Request.Builder()
                            .url(params[0].request_Url)
                            .header("Content-Type", "application/json; Charset=UTF-8")
                            .header( "Authorization","Basic YmFzaWNBdXRoOiZqbm8tQDhhej13U28qTkhZVkdwRl5BUT80eW4zNlp2VzVUb1VDVU4rWEdPdUM/c3ojU0Ukb3hYVmJ3UUdQfDNXRnlqY1RBajJTSVJRbkxFfHZvXi18LUFUVjVGWlVmMio1QTNPaXV8X0VPTW1HPT0maUFwelFMM1I3SEhRaj9qdGIwbWMybVQkWSVJc3Jncnh2ZWxkI1peZzMtdWxefDB4QUlUZ2FuSXVGMjNKMHdhU2E2ejZhUF8rJURlNUxxdHVZJnB0eD9xaFp5U0VDZHlFXio0Ul5iKmhGalEtOT9jQ1NKTmZST3p6dEVZYlJ5Tj1TcUR5aGhwelNtbVB8RWI=")
                            .delete(body)
                            .build();
                }
                else
                {
                    RequestBody body = RequestBody.create(JSON,params[0].requestBody.toString());

                        request = new Request.Builder()
                                .url(params[0].request_Url)
                                .header("Content-Type", "application/json; Charset=UTF-8")
                                .header( "Authorization","Basic YmFzaWNBdXRoOiZqbm8tQDhhej13U28qTkhZVkdwRl5BUT80eW4zNlp2VzVUb1VDVU4rWEdPdUM/c3ojU0Ukb3hYVmJ3UUdQfDNXRnlqY1RBajJTSVJRbkxFfHZvXi18LUFUVjVGWlVmMio1QTNPaXV8X0VPTW1HPT0maUFwelFMM1I3SEhRaj9qdGIwbWMybVQkWSVJc3Jncnh2ZWxkI1peZzMtdWxefDB4QUlUZ2FuSXVGMjNKMHdhU2E2ejZhUF8rJURlNUxxdHVZJnB0eD9xaFp5U0VDZHlFXio0Ul5iKmhGalEtOT9jQ1NKTmZST3p6dEVZYlJ5Tj1TcUR5aGhwelNtbVB8RWI=")
                                .put(body)
                                .get()
                                .build();



                    System.out.println("OKHTTP"+" "+"get url="+params[0].request_Url);
                }
                Response response = httpClient.newCall(request).execute();
                int statusCode=response.code();
                System.out.println("statusCode="+statusCode);
                switch (statusCode)
                {
                    case 502 :
                        dataPacket.isError=true;
                        result ="Server Error (502 Bad Gateway)";
                        break;
                    default:
                        result = response.body().string();
                }
            }
            catch (UnsupportedEncodingException e)
            {
                dataPacket.isError=true;
                OkHttp3Connection.printLog("UnsupportedEncodingException" + e.toString());
                result ="Connection Failed..Retry !";
                e.printStackTrace();
            }
            catch (IOException e)
            {

                dataPacket.isError=true;
                OkHttp3Connection.printLog("Read IO exception" + e.toString());
                result ="Connection is too slow...Retry!";
                e.printStackTrace();
            }
            catch (Exception e)
            {
                dataPacket.isError=true;
                printLog("Read Exception"+e.toString());
                result ="Connection is too slow...Retry!";
                e.printStackTrace();
            }
            /*
             * Adding result data.*/
            dataPacket.result_data=result;
            return dataPacket;
        }

        @Override
        protected void onPostExecute(DataPacket dataPacket)
        {
            super.onPostExecute(dataPacket);
            /*
             * Comment for live app.*/
            printLog(dataPacket.result_data);

            if(!dataPacket.isError)
            {
                if(dataPacket.callbacks_packet!=null)
                    dataPacket.callbacks_packet.onSuccess(dataPacket.result_data,dataPacket.Tag);
            }
            else
            {
                if(dataPacket.callbacks_packet!=null)
                    dataPacket.callbacks_packet.onError(dataPacket.result_data,dataPacket.Tag);
            }
        }

    }
    /**
     * interface for Session Call back request
     * */
    public interface OkHttp3RequestCallback
    {
        /**
         * Called When Success result of JSON request
         *
         * @param result contains the service result.
         * @param user_tag contains the user given Tag.
         */
        void onSuccess(String result,String user_tag);
        /**
         * Called When Error result of JSON request
         *
         * @param error contain the error message.
         * @param user_tag contains the user given tag .
         */
        void onError(String error,String user_tag);
    }

    /**
     * <h2></h2>*/
    private static String getUrl(String url,JSONObject jsonObject)
    {
        String service_url=url+"?";
        String query="";
        Iterator<String> object_keys=jsonObject.keys();
        try
        {
            while (object_keys.hasNext())
            {
                String keys_value=object_keys.next();
                query=query+keys_value+"="+jsonObject.getString(keys_value)+"&";
            }

        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.d("Value",service_url+query);

        return service_url+query;
    }
    /**
     * <h2>BasicAuthInterceptor</h2>
     * <P>
     *     Authenticator provider .
     * </P>*/
    private static class BasicAuthInterceptor implements Interceptor
    {
        private String credentials;
        BasicAuthInterceptor(String user, String password)
        {
            this.credentials = Credentials.basic(user, password);
        }

        @Override
        public Response intercept(Chain chain) throws IOException
        {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials).build();
            return chain.proceed(authenticatedRequest);
        }
    }

    /**
     * Creating the safe connection*/
    private static OkHttpClient getSafeOkHttpClient(OkHttpClient.Builder builder)
    {
        try {
            X509TrustManager x509TrustManager= new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            final TrustManager[] trustAllCerts = new TrustManager[]{x509TrustManager};
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory,x509TrustManager);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session)
                {
                    HostnameVerifier hv=HttpsURLConnection.getDefaultHostnameVerifier();
                    return hv.verify("api.yelo-app.xyz", session);
                }
            });
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <H3>Request_type</H3>
     * <p>
     *   Request type enum class.
     * </p>*/
    public enum Request_type
    {
        GET("getRequest"),
        URl("urlRequest"),
        PUT("putRequest"),
        DELETE("deleteRequest"),
        POST("postRequest");
        public String value;
        Request_type(String value)
        {
            this.value = value;
        }
    }
    private static void printLog(String message)
    {
        Log.d("OKHTTPCONNECTION",""+message);
    }
}
