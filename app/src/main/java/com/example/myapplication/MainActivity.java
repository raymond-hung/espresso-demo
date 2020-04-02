package com.example.myapplication;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.espresso.idling.CountingIdlingResource;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn;
    private EchoService service;
    private Retrofit retrofit;

    private CountingIdlingResource idlingResource = new CountingIdlingResource("Network Call");;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(this);

        OkHttpClient client = new OkHttpClient();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.URL)
                .addConverterFactory(StringConverterFactory.create())
                .client(client)
                .build();
        service = retrofit.create(EchoService.class);

    }

    protected void onResume(){
        super.onResume();
    }

    private void sendEchoRequest(){
        idlingResource.increment();
        Call<String> call = service.getEchoResponse();
        final Context ctx= this;
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {

                    (new AlertDialog.Builder(ctx)).setPositiveButton(R.string.label_btn_ok,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setTitle(R.string.title_success).setMessage(response.body()).create().show();

                } else {

                    (new AlertDialog.Builder(ctx)).setPositiveButton(R.string.label_btn_ok,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setTitle(R.string.title_error).setMessage(getString(R.string.msg_http_error,response.code())).create().show();

                }
                idlingResource.decrement();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                idlingResource.decrement();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.equals(btn)){
            sendEchoRequest();
        }
    }

    //Only called from test
    @VisibleForTesting
    public CountingIdlingResource getIdlingResourceForTest() {
        return idlingResource;
    }


}
