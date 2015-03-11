package com.example.ben.sosheartv4;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PatientListActivity extends Activity {
    EditText search;
    ListView listView;
    PatientListAdapter patientListAdapter;
    ArrayList<Patient> patientList;
    final Context context=this;
    private ProgressDialog pDialog;
    HttpRequest request=new HttpRequest();
    //put your addrese
    String url="http://<write your ip>/Real2/patient_list.php",userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        search=(EditText) findViewById(R.id.aplETSearch);

        listView=(ListView) findViewById(R.id.aplLVList);
        patientList =new ArrayList<Patient>();
        userName=getIntent().getExtras().getString("name");
        new PatientListActivityAsyncTask().execute("list",userName);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(context,PatientStateActivity.class);

                intent.putExtra("name",patientList.get(position).getPatinetName());
                intent.putExtra("state",patientList.get(position).getState());
                startActivity(intent);
                //Toast.makeText(context,"Patient Name: "+patientList.get(position).getPatinetName(),Toast.LENGTH_LONG).show();
            }

        });
    }

    public void ActionPatientListActivity(View v){
        switch (v.getId()){
            case R.id.aplBAddNewPatient:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                //alert.setTitle("connecting");
                alert.setMessage("Who patient are you caring him\\her?");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String patientName = input.getText().toString();
                        // Do something with value!
                        //send value to service
                        new PatientListActivityAsyncTask().execute("care",userName,patientName);
                        //SystemClock.sleep(7000);
                        //new PatientListActivityAsyncTask().execute("list",userName);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();
                break;
        }
    }

    public void callAlert(final String title,String message){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setMessage(message);

        alert.setNegativeButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.show();
    }

    public class PatientListActivityAsyncTask extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(PatientListActivity.this);
            pDialog.setMessage("Connection To Server..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> post = new ArrayList<NameValuePair>();
            post.add(new BasicNameValuePair("task", params[0]));
            post.add(new BasicNameValuePair("name", params[1]));
            try {
                if(params[0].equals("list")){
                    Log.d("Single POST LIST Detai:", post.toString());
                    JSONObject jObj=request.requestToPHP(url,"POST",post);
                    Log.d("Single JSON Details", jObj.toString());
                    if(jObj.getInt("success")==1){
                        JSONArray jArray = jObj.getJSONArray("patient");
                        for (int i = 0; i < jArray.length(); i++) {
                            Patient patient = new Patient();
                            JSONObject jReal = jArray.getJSONObject(i);
                            patient.setPatinetName(jReal.getString("pUserName"));
                            patient.setState(jReal.getString("currentStatus"));
                            patientList.add(patient);
                        }
                        return params[0];
                    }
                }else if(params[0].equals("care")){
                    post.add(new BasicNameValuePair("patientName", params[2]));
                    Log.d("Single POST CARE  Deta:", post.toString());
                    JSONObject jObj=request.requestToPHP(url,"POST",post);
                    Log.d("Single JSON Details", jObj.toString());
                    if(jObj.getInt("success")==1){
                        Patient patient = new Patient();
                        patient.setPatinetName(jObj.getString("pUserName"));
                        patient.setState(jObj.getString("currentStatus"));
                        patientList.add(patient);
                        return params[0];
                    }else{
                        return "Reject";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            PatientListAdapter adapter=new PatientListAdapter(context,R.layout.adapter_patient,patientList);

            if(s.equals("list")){

                listView.setAdapter(adapter);
            }else if(s.equals("care")) {
                adapter.notifyDataSetChanged();

                listView.refreshDrawableState();

            }else if(s.equals("Reject")){
                callAlert("Reject","Patient name is incorrect");
            }
        }
    }


}
