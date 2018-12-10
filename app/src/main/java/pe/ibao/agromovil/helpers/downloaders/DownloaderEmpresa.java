package pe.ibao.agromovil.helpers.downloaders;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

import pe.ibao.agromovil.app.AppController;
import pe.ibao.agromovil.models.dao.EmpresaDAO;

import static pe.ibao.agromovil.utilities.Utilities.URL_DOWNLOAD_TABLE_EMPRESA;

public class DownloaderEmpresa {

    Context ctx;
 //   ProgressDialog progress;
    public static int status;
    public DownloaderEmpresa(Context ctx){
        status=0;
        this.ctx = ctx;
    }

    public void download(){
        status=1;
   //     progress = new ProgressDialog(ctx);
   //     progress.setCancelable(false);
   //     progress.setMessage("Intentando descargar Empresas");
   //     progress.show();
        StringRequest sr = new StringRequest(Request.Method.POST,
                URL_DOWNLOAD_TABLE_EMPRESA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
     //                   progress.dismiss();
                        try {
                            JSONArray main = new JSONArray(response);
                            if(main.length()>0){
                                new EmpresaDAO(ctx).clearTableUpload();
                                status=2;
                            }
                            for(int i=0;i<main.length();i++){
                                JSONObject data = new JSONObject(main.get(i).toString());
                                int id = data.getInt("id");
                                String nombre = String.valueOf(id)+"-"+data.getString("nombre");
                                Log.d("EMPRESADOWN","fila "+i+" : "+id+" "+nombre);
                                if(new EmpresaDAO(ctx).insertarEmpresa(id,nombre)){
                                    Log.d("EMPRESADOWN","logro insertar");
                                }
                            }
                        status=3;
                        } catch (JSONException e) {
                            Log.d("EMPRESADOWN ",e.toString());
                            status=-1;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
       //                 progress.dismiss();
                        Toast.makeText(ctx,"Error conectando con el servidor",Toast.LENGTH_LONG).show();
                        status=-2;
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
               /* params.put(POST_USER, user);
                params.put(POST_PASSWORD, pass);
                */

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(sr);
    }

    public void download(final TextView porcentaje, final TextView mensaje, final int  ini, final int tam) {
       /* progress = new ProgressDialog(ctx);
        progress.setCancelable(false);
        progress.setMessage("Intentando descargar Empresas");
        progress.show();
        */
        status=1;
        StringRequest sr = new StringRequest(Request.Method.POST,
                URL_DOWNLOAD_TABLE_EMPRESA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mensaje.setText("Descargando Empresas");
                       // progress.dismiss();
                        try {
                            JSONArray main = new JSONArray(response);
                            final int length = main.length();
                            if(main.length()>0){
                                new EmpresaDAO(ctx).clearTableUpload();
                                status=2;
                            }
                            for(int i=0;i<main.length();i++){
                                JSONObject data = new JSONObject(main.get(i).toString());
                                int id = data.getInt("id");
                                String nombre = String.valueOf(id)+"-"+data.getString("nombre");
                                Log.d("EMPRESADOWN","fila "+i+" : "+id+" "+nombre);
                                if(new EmpresaDAO(ctx).insertarEmpresa(id,nombre)){
                                    Log.d("EMPRESADOWN","logro insertar");
                                    android.os.Handler handler = new android.os.Handler();
                                    final int finalI = i;
                                    handler.post(new Runnable() {
                                        public void run() {

                                            porcentaje.setText("" + (ini + ((finalI * tam) / length)) + "%");
                                        }
                                    });

                                }
                            }
                            status=3;
                        //    porcentaje.setText(String.valueOf(tam));

                        } catch (JSONException e) {
                            Log.d("EMPRESADOWN ",e.toString());
                            status=-1;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                     //   progress.dismiss();
                        Toast.makeText(ctx,"Error conectando con el servidor",Toast.LENGTH_LONG).show();
                        status=-2;
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
               /* params.put(POST_USER, user);
                params.put(POST_PASSWORD, pass);
                */

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(sr);
    }
}