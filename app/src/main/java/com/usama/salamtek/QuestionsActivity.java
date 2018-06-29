package com.usama.salamtek;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.usama.salamtek.Model.User;
import com.usama.salamtek.Model.UserWeeklyCondition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class QuestionsActivity extends AppCompatActivity {
    EditText weightAfter, percent_of_hemogli, glucose, ptt;
    CheckBox backache, headache, dizzy, breast, tired, vomit, leg, swollen, heartburn, waterBreak, visualDisturbance, darkerPlaces, breath, babyMoves, insomnia, infection, breastLeek, flattenedNavel, toiletMoreOften, thirsty, dehydratio;
    RadioGroup blood_pressure, abdominal_pain, protein_in_urine, rh_type, vaginal_discharge, Bleeding;
    Button questions_apply;
    UserWeeklyCondition userWeeklyCondition;
    TextView diseas_result;
    boolean acceptQuestions = true;
    RadioButton pressureLow, pressureNormal, pressureHigh, once, freq, yes, no, negative, positive, colord, uncolord, highPleeding, mediumPleeding, lowPleeding, noPleeding;


    Response.Listener<String> serverResponse;
    Response.ErrorListener errorListener;
    StringRequest createOrGetUserWeeklyQuestions;
    RequestQueue requestQueue;
    private final String pageUrl = LoginActivity.serverIP + "createUserWeeklyQuestions.php";
    private final String pageWeeklyResultUrl = LoginActivity.serverIP + "getUserWeeklyQuestions.php";
    User user;
    Classifier cls;
    String diagnosticResult;

    List<UserWeeklyCondition> weeklyConditions = new ArrayList<>();
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        //EditText
        weightAfter = findViewById(R.id.weightAfter);
        percent_of_hemogli = findViewById(R.id.percent_of_hemogli);
        glucose = findViewById(R.id.glucose);
        ptt = findViewById(R.id.ptt);
        //CheckBox
        backache = findViewById(R.id.backache);
        headache = findViewById(R.id.headache);
        dizzy = findViewById(R.id.dizzy);
        breast = findViewById(R.id.breast);
        tired = findViewById(R.id.tired);
        vomit = findViewById(R.id.vomit);
        leg = findViewById(R.id.leg);
        swollen = findViewById(R.id.swollen);
        heartburn = findViewById(R.id.heartburn);
        waterBreak = findViewById(R.id.waterBreak);
        visualDisturbance = findViewById(R.id.visualDisturbance);
        darkerPlaces = findViewById(R.id.darkerPlaces);
        breath = findViewById(R.id.breath);
        babyMoves = findViewById(R.id.babyMoves);
        insomnia = findViewById(R.id.insomnia);
        infection = findViewById(R.id.infection);
        breastLeek = findViewById(R.id.breastLeek);
        flattenedNavel = findViewById(R.id.flattenedNavel);
        toiletMoreOften = findViewById(R.id.toiletMoreOften);
        thirsty = findViewById(R.id.thirsty);
        dehydratio = findViewById(R.id.dehydratio);
        //RadioGroup
        blood_pressure = findViewById(R.id.blood_pressure);
        abdominal_pain = findViewById(R.id.abdominal_pain);
        protein_in_urine = findViewById(R.id.protein_in_urine);
        rh_type = findViewById(R.id.rh_type);
        vaginal_discharge = findViewById(R.id.vaginal_discharge);
        Bleeding = findViewById(R.id.Bleeding);
        //button
        questions_apply = findViewById(R.id.questions_apply);
        //text view
        diseas_result = findViewById(R.id.diseas_result);
        //radio btn
        pressureLow = blood_pressure.findViewById(R.id.pressure_radio_low);
        pressureNormal = blood_pressure.findViewById(R.id.pressure_radio_normal);
        pressureHigh = blood_pressure.findViewById(R.id.pressure_radio_high);

        once = abdominal_pain.findViewById(R.id.radio_once);
        freq = abdominal_pain.findViewById(R.id.radio_freq);

        yes = protein_in_urine.findViewById(R.id.radio_yes);
        no = protein_in_urine.findViewById(R.id.radio_no);

        negative = rh_type.findViewById(R.id.radio_negative);
        positive = rh_type.findViewById(R.id.radio_positive);

        colord = vaginal_discharge.findViewById(R.id.radio_Colored);
        uncolord = vaginal_discharge.findViewById(R.id.radio_uncolored);

        highPleeding = Bleeding.findViewById(R.id.highBleeding);
        mediumPleeding = Bleeding.findViewById(R.id.mediumBleeding);
        lowPleeding = Bleeding.findViewById(R.id.lowBleeding);
        noPleeding = Bleeding.findViewById(R.id.noBleeding);


        Intent intent = getIntent();
        if (intent.hasExtra("weeklyQuestions")) {
            if (intent != null && intent.hasExtra("user_data")) {
                user = intent.getParcelableExtra("user_data");

                questions_apply.setOnClickListener(view -> {
                    userWeeklyCondition = new UserWeeklyCondition();
                    acceptQuestions = !weightAfter.getText().toString().equals("")
                            && !percent_of_hemogli.getText().toString().equals("")
                            && !glucose.getText().toString().equals("")
                            && !ptt.getText().toString().equals("")
                            && !(blood_pressure.getCheckedRadioButtonId() == -1)
                            && !(abdominal_pain.getCheckedRadioButtonId() == -1)
                            && !(protein_in_urine.getCheckedRadioButtonId() == -1)
                            && !(rh_type.getCheckedRadioButtonId() == -1)
                            && !(vaginal_discharge.getCheckedRadioButtonId() == -1)
                            && !(Bleeding.getCheckedRadioButtonId() == -1);

                    if (acceptQuestions) {
                        Double weightedGained = Double.parseDouble(weightAfter.getText().toString()) - Double.parseDouble(user.getCurrWeight());
                        if (weightedGained < 0) {
                            weightedGained = 0.0;
                        }
                        userWeeklyCondition.setWeightGained(String.valueOf(weightedGained));
                        userWeeklyCondition.setHeamoglobin(percent_of_hemogli.getText().toString());
                        userWeeklyCondition.setGlucose(glucose.getText().toString());
                        userWeeklyCondition.setPTT(ptt.getText().toString());
                        userWeeklyCondition.setBackache(backache.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setHeadache(headache.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setDizzy(dizzy.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setBreastTenderness(breast.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setTired(tired.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setVomiting(vomit.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setLegCramps(leg.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setSwollen(swollen.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setHeartBurn(heartburn.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setPartialWaterBreak(waterBreak.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setVisualDisturbance(visualDisturbance.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setDarkerPlaces(darkerPlaces.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setOutOfBreath(breath.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setBabyMoves(babyMoves.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setInsomnia(insomnia.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setGynecologicalInfection(infection.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setBreastLeak(breastLeek.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setNavelIsFlattened(flattenedNavel.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setToiletMoreOften(toiletMoreOften.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setThirstyAsLong(thirsty.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setDehydration(dehydratio.isChecked() ? "Yes" : "No");
                        userWeeklyCondition.setPressure(((RadioButton) findViewById(blood_pressure.getCheckedRadioButtonId())).getText().toString());
                        if (((RadioButton) findViewById(abdominal_pain.getCheckedRadioButtonId())).getText().toString().equals(getResources().getString(R.string.once))) {
                            userWeeklyCondition.setAbdominalPain("Yes");
                            userWeeklyCondition.setFrequentAbdominalPain("No");
                        } else {
                            userWeeklyCondition.setAbdominalPain("No");
                            userWeeklyCondition.setFrequentAbdominalPain("Yes");
                        }
                        userWeeklyCondition.setProteinInUrine(((RadioButton) findViewById(protein_in_urine.getCheckedRadioButtonId())).getText().toString());
                        userWeeklyCondition.setRhType(((RadioButton) findViewById(rh_type.getCheckedRadioButtonId())).getText().toString());
                        userWeeklyCondition.setVaginalDischarge(((RadioButton) findViewById(vaginal_discharge.getCheckedRadioButtonId())).getText().toString());
                        userWeeklyCondition.setBleeding(((RadioButton) findViewById(Bleeding.getCheckedRadioButtonId())).getText().toString());

                        try {
                            //to load the arff file that contain the data to train it
                            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(getAssets().open("model_data.arff"));
                            Instances data = dataSource.getDataSet();
                            if (data.classIndex() == -1) {
                                data.setClassIndex(data.numAttributes() - 1);
                            }
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                            if (!sharedPreferences.contains("model_created")) {
                                //To create model
                                cls = new J48();
                                cls.buildClassifier(data);
                                //cross validate to test model
                                Evaluation eval = new Evaluation(data);
                                eval.crossValidateModel(cls, data, 9, new Random(1));
                                System.out.println(eval.toSummaryString("\nResults\n======\n", false));
                                //to store data on app folders (data folder)
                                weka.core.SerializationHelper.write(getBaseContext().getFilesDir() + File.separator + "project.model", cls);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("model_created", true);
                                editor.apply();
                            } else {
                                cls = (Classifier) weka.core.SerializationHelper.read(getBaseContext().getFilesDir() + File.separator + "project.model");
                            }


                            int Number_Of_Atr = 34;
                            int Number_Of_Instance = 1;

                            String[] arr0 = {"No", "Yes"};
                            List<String> global = new ArrayList<>();
                            global.addAll(Arrays.asList(arr0));

                            String[] arr = {"High", "Low", "Normal"};
                            List<String> pressure = new ArrayList<>();
                            pressure.addAll(Arrays.asList(arr));

                            String[] arr1 = {"Negative", "Positive"};
                            List<String> rh_type = new ArrayList<>();
                            rh_type.addAll(Arrays.asList(arr1));


                            String[] arr2 = {"Colored", "Normal"};
                            List<String> vaginal_discharge = new ArrayList<>();
                            vaginal_discharge.addAll(Arrays.asList(arr2));

                            String[] arr3 = {"High", "Low", "Medium", "No"};
                            List<String> bleeding = new ArrayList<>();
                            bleeding.addAll(Arrays.asList(arr3));

                            String[] arr4 = {"Abortion", "Blood Pressure Problem", "Gestational Hypertension", "Gestational diabetes", "Glucose Problem", "Hemoglobin Problem", "High Glucose", "Low Pressure", "No Baby Move", "Normal", "PTT Problem", "Preeclampsia", "Premature Labor", "Problem in breathing or Heart problem", "RH Problem", "Threatened apportion", "Vagina Discharge Problem"};
                            List<String> disease = new ArrayList<>();
                            disease.addAll(Arrays.asList(arr4));

                            FastVector fvglobal = new FastVector(global.size());
                            fvglobal.addAll(global);

                            FastVector fvpressure = new FastVector(pressure.size());
                            fvpressure.addAll(pressure);

                            FastVector fvrh_type = new FastVector(rh_type.size());
                            fvrh_type.addAll(rh_type);

                            FastVector fvvaginal_discharge = new FastVector(vaginal_discharge.size());
                            fvvaginal_discharge.addAll(vaginal_discharge);

                            FastVector fvbleeding = new FastVector(bleeding.size());
                            fvbleeding.addAll(bleeding);

                            FastVector fvdisease = new FastVector(disease.size());
                            fvdisease.addAll(disease);

                            Attribute attribute1 = new Attribute("pregnancy_month");
                            Attribute attribute2 = new Attribute("weight_gained");
                            Attribute attribute3 = new Attribute("heamoglobin");
                            Attribute attribute4 = new Attribute("pressure", fvpressure);
                            Attribute attribute5 = new Attribute("ptt");
                            Attribute attribute6 = new Attribute("rh_type", fvrh_type);
                            Attribute attribute7 = new Attribute("glucose");
                            Attribute attribute8 = new Attribute("breast_tenderness", fvglobal);
                            Attribute attribute9 = new Attribute("dizzy", fvglobal);
                            Attribute attribute10 = new Attribute("tired", fvglobal);
                            Attribute attribute11 = new Attribute("vomiting", fvglobal);
                            Attribute attribute12 = new Attribute("backache", fvglobal);
                            Attribute attribute13 = new Attribute("headache", fvglobal);
                            Attribute attribute14 = new Attribute("leg_cramps", fvglobal);
                            Attribute attribute15 = new Attribute("heartburn", fvglobal);
                            Attribute attribute16 = new Attribute("swollen", fvglobal);
                            Attribute attribute17 = new Attribute("darker_places", fvglobal);
                            Attribute attribute18 = new Attribute("vaginal_discharge", fvvaginal_discharge);
                            Attribute attribute19 = new Attribute("breast_leak", fvglobal);
                            Attribute attribute20 = new Attribute("babymoves", fvglobal);
                            Attribute attribute21 = new Attribute("insomnia", fvglobal);
                            Attribute attribute22 = new Attribute("out_of_breath", fvglobal);
                            Attribute attribute23 = new Attribute("partial_water_break", fvglobal);
                            Attribute attribute24 = new Attribute("navel_is_flattened", fvglobal);
                            Attribute attribute25 = new Attribute("toilet_more_often", fvglobal);
                            Attribute attribute26 = new Attribute("visual_disturbance", fvglobal);
                            Attribute attribute27 = new Attribute("protein_in_urine", fvglobal);
                            Attribute attribute28 = new Attribute("gynecological_infection", fvglobal);
                            Attribute attribute29 = new Attribute("dehydration", fvglobal);
                            Attribute attribute30 = new Attribute("thirsty_as_long", fvglobal);
                            Attribute attribute31 = new Attribute("abdominal_pain", fvglobal);
                            Attribute attribute32 = new Attribute("frequent_abdominal_pain", fvglobal);
                            Attribute attribute33 = new Attribute("bleeding", fvbleeding);
                            Attribute classattribute = new Attribute("disease", fvdisease);

                            FastVector fastVector = new FastVector(Number_Of_Atr);
                            fastVector.add(attribute1);
                            fastVector.add(attribute2);
                            fastVector.add(attribute3);
                            fastVector.add(attribute4);
                            fastVector.add(attribute5);
                            fastVector.add(attribute6);
                            fastVector.add(attribute7);
                            fastVector.add(attribute8);
                            fastVector.add(attribute9);
                            fastVector.add(attribute10);
                            fastVector.add(attribute11);
                            fastVector.add(attribute12);
                            fastVector.add(attribute13);
                            fastVector.add(attribute14);
                            fastVector.add(attribute15);
                            fastVector.add(attribute16);
                            fastVector.add(attribute17);
                            fastVector.add(attribute18);
                            fastVector.add(attribute19);
                            fastVector.add(attribute20);
                            fastVector.add(attribute21);
                            fastVector.add(attribute22);
                            fastVector.add(attribute23);
                            fastVector.add(attribute24);
                            fastVector.add(attribute25);
                            fastVector.add(attribute26);
                            fastVector.add(attribute27);
                            fastVector.add(attribute28);
                            fastVector.add(attribute29);
                            fastVector.add(attribute30);
                            fastVector.add(attribute31);
                            fastVector.add(attribute32);
                            fastVector.add(attribute33);
                            fastVector.add(classattribute);

                            Instances trainingSet = new Instances("Rel", fastVector, Number_Of_Instance);
                            trainingSet.setClassIndex(Number_Of_Atr - 1);

                            Instance instance = new DenseInstance(Number_Of_Atr);
                            instance.setValue(attribute1, Double.valueOf(intent.getIntExtra("pregnancyMonth", 1)));
                            instance.setValue(attribute2, weightedGained);
                            instance.setValue(attribute3, Double.valueOf(userWeeklyCondition.getHeamoglobin()));
                            instance.setValue(attribute4, userWeeklyCondition.getPressure());
                            instance.setValue(attribute5, Double.valueOf(userWeeklyCondition.getPTT()));
                            instance.setValue(attribute6, userWeeklyCondition.getRhType());
                            instance.setValue(attribute7, Double.valueOf(userWeeklyCondition.getGlucose()));
                            instance.setValue(attribute8, userWeeklyCondition.getBreastTenderness());
                            instance.setValue(attribute9, userWeeklyCondition.getDizzy());
                            instance.setValue(attribute10, userWeeklyCondition.getTired());
                            instance.setValue(attribute11, userWeeklyCondition.getVomiting());
                            instance.setValue(attribute12, userWeeklyCondition.getBackache());
                            instance.setValue(attribute13, userWeeklyCondition.getHeadache());
                            instance.setValue(attribute14, userWeeklyCondition.getLegCramps());
                            instance.setValue(attribute15, userWeeklyCondition.getHeartBurn());
                            instance.setValue(attribute16, userWeeklyCondition.getSwollen());
                            instance.setValue(attribute17, userWeeklyCondition.getDarkerPlaces());
                            instance.setValue(attribute18, userWeeklyCondition.getVaginalDischarge());
                            instance.setValue(attribute19, userWeeklyCondition.getBreastLeak());
                            instance.setValue(attribute20, userWeeklyCondition.getBabyMoves());
                            instance.setValue(attribute21, userWeeklyCondition.getInsomnia());
                            instance.setValue(attribute22, userWeeklyCondition.getOutOfBreath());
                            instance.setValue(attribute23, userWeeklyCondition.getPartialWaterBreak());
                            instance.setValue(attribute24, userWeeklyCondition.getNavelIsFlattened());
                            instance.setValue(attribute25, userWeeklyCondition.getToiletMoreOften());
                            instance.setValue(attribute26, userWeeklyCondition.getVisualDisturbance());
                            instance.setValue(attribute27, userWeeklyCondition.getProteinInUrine());
                            instance.setValue(attribute28, userWeeklyCondition.getGynecologicalInfection());
                            instance.setValue(attribute29, userWeeklyCondition.getDehydration());
                            instance.setValue(attribute30, userWeeklyCondition.getThirstyAsLong());
                            instance.setValue(attribute31, userWeeklyCondition.getAbdominalPain());
                            instance.setValue(attribute32, userWeeklyCondition.getFrequentAbdominalPain());
                            instance.setValue(attribute33, userWeeklyCondition.getBleeding());

                            trainingSet.add(instance);
                            double cc = cls.classifyInstance(trainingSet.instance(0));
                            System.out.println(cc);
                            System.out.println(data.classAttribute().value((int) cc));
                            diagnosticResult = data.classAttribute().value((int) cc);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            // Weka "catch'em all!"
                            e.printStackTrace();
                        }


                        serverResponse = response -> {
                            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                            if (!response.equals("null")){
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt("last_visited_question", (int) intent.getLongExtra("week_num", 0));
                                editor.apply();
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                if (notificationManager != null) {
                                    notificationManager.cancelAll();
                                }
                            }
                            requestQueue.stop();
                        };

                        errorListener = error -> {
                            error.printStackTrace();
                            requestQueue.stop();
                        };

                        createOrGetUserWeeklyQuestions = new StringRequest(Request.Method.POST, pageUrl, serverResponse, errorListener) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> data = new HashMap<>();
                                data.put("UID", String.valueOf(user.getId()));
                                data.put("WeakN", String.valueOf(intent.getLongExtra("week_num", 1)));
                                data.put("Weight_before", user.getCurrWeight());
                                data.put("Weight_after", weightAfter.getText().toString());
                                data.put("Pregnancy_month", String.valueOf(intent.getIntExtra("pregnancyMonth", 1)));
                                data.put("Weight_gained", userWeeklyCondition.getWeightGained());
                                data.put("Heamoglobin", userWeeklyCondition.getHeamoglobin());
                                data.put("Pressure", userWeeklyCondition.getPressure());
                                data.put("PTT", userWeeklyCondition.getPTT());
                                data.put("RH_type", userWeeklyCondition.getRhType());
                                data.put("Glucose", userWeeklyCondition.getGlucose());
                                data.put("Breast_tenderness", userWeeklyCondition.getBreastTenderness());
                                data.put("Dizzy", userWeeklyCondition.getDizzy());
                                data.put("Tired", userWeeklyCondition.getTired());
                                data.put("Vomiting", userWeeklyCondition.getVomiting());
                                data.put("Backache", userWeeklyCondition.getBackache());
                                data.put("Headache", userWeeklyCondition.getHeadache());
                                data.put("Leg_Cramps", userWeeklyCondition.getLegCramps());
                                data.put("Heartburn", userWeeklyCondition.getHeartBurn());
                                data.put("Swollen", userWeeklyCondition.getSwollen());
                                data.put("Darker_places", userWeeklyCondition.getDarkerPlaces());
                                data.put("Vaginal_discharge", userWeeklyCondition.getVaginalDischarge());
                                data.put("Breast_leak", userWeeklyCondition.getBreastLeak());
                                data.put("Baby_Moves", userWeeklyCondition.getBabyMoves());
                                data.put("Insomnia", userWeeklyCondition.getInsomnia());
                                data.put("Out_of_breath", userWeeklyCondition.getOutOfBreath());
                                data.put("Partial_water_break", userWeeklyCondition.getPartialWaterBreak());
                                data.put("Navel_is_flattened", userWeeklyCondition.getNavelIsFlattened());
                                data.put("Toilet_more_often", userWeeklyCondition.getToiletMoreOften());
                                data.put("Visual_disturbance", userWeeklyCondition.getVisualDisturbance());
                                data.put("Protein_in_urine", userWeeklyCondition.getProteinInUrine());
                                data.put("Gynecological_infection", userWeeklyCondition.getGynecologicalInfection());
                                data.put("Dehydration", userWeeklyCondition.getDehydration());
                                data.put("Thirsty_as_long", userWeeklyCondition.getThirstyAsLong());
                                data.put("Abdominal_pain", userWeeklyCondition.getAbdominalPain());
                                data.put("Frequent_abdominal_pain", userWeeklyCondition.getFrequentAbdominalPain());
                                data.put("Bleeding", userWeeklyCondition.getBleeding());
                                data.put("diagnosticResult", diagnosticResult);
                                return data;
                            }
                        };

                        requestQueue = Volley.newRequestQueue(this);
                        requestQueue.add(createOrGetUserWeeklyQuestions);
                    } else {
                        Toast.makeText(this, "Please insert all required data", Toast.LENGTH_SHORT).show();
                    }

                });

            }
        } else {
            user = intent.getParcelableExtra("user_data");
            diseas_result.setVisibility(View.VISIBLE);


            serverResponse = response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("user_weekly_data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        userWeeklyCondition = new UserWeeklyCondition();
                        userWeeklyCondition.setWeightGained(jsonArray.getJSONObject(i).getString("Weight_gained"));
                        userWeeklyCondition.setHeamoglobin(jsonArray.getJSONObject(i).getString("Heamoglobin"));
                        userWeeklyCondition.setGlucose(jsonArray.getJSONObject(i).getString("Glucose"));
                        userWeeklyCondition.setPTT(jsonArray.getJSONObject(i).getString("PTT"));
                        userWeeklyCondition.setBackache(jsonArray.getJSONObject(i).getString("Backache"));
                        userWeeklyCondition.setHeadache(jsonArray.getJSONObject(i).getString("Headache"));
                        userWeeklyCondition.setDizzy(jsonArray.getJSONObject(i).getString("Dizzy"));
                        userWeeklyCondition.setBreastTenderness(jsonArray.getJSONObject(i).getString("Breast_tenderness"));
                        userWeeklyCondition.setTired(jsonArray.getJSONObject(i).getString("Tired"));
                        userWeeklyCondition.setVomiting(jsonArray.getJSONObject(i).getString("Vomiting"));
                        userWeeklyCondition.setLegCramps(jsonArray.getJSONObject(i).getString("Leg_Cramps"));
                        userWeeklyCondition.setSwollen(jsonArray.getJSONObject(i).getString("Swollen"));
                        userWeeklyCondition.setHeartBurn(jsonArray.getJSONObject(i).getString("Heartburn"));
                        userWeeklyCondition.setPartialWaterBreak(jsonArray.getJSONObject(i).getString("Partial_water_break"));
                        userWeeklyCondition.setVisualDisturbance(jsonArray.getJSONObject(i).getString("Visual_disturbance"));
                        userWeeklyCondition.setDarkerPlaces(jsonArray.getJSONObject(i).getString("Darker_places"));
                        userWeeklyCondition.setOutOfBreath(jsonArray.getJSONObject(i).getString("Out_of_breath"));
                        userWeeklyCondition.setBabyMoves(jsonArray.getJSONObject(i).getString("Baby_Moves"));
                        userWeeklyCondition.setInsomnia(jsonArray.getJSONObject(i).getString("Insomnia"));
                        userWeeklyCondition.setGynecologicalInfection(jsonArray.getJSONObject(i).getString("Gynecological_infection"));
                        userWeeklyCondition.setBreastLeak(jsonArray.getJSONObject(i).getString("Breast_leak"));
                        userWeeklyCondition.setNavelIsFlattened(jsonArray.getJSONObject(i).getString("Navel_is_flattened"));
                        userWeeklyCondition.setToiletMoreOften(jsonArray.getJSONObject(i).getString("Toilet_more_often"));
                        userWeeklyCondition.setThirstyAsLong(jsonArray.getJSONObject(i).getString("Thirsty_as_long"));
                        userWeeklyCondition.setDehydration(jsonArray.getJSONObject(i).getString("Dehydration"));
                        userWeeklyCondition.setPressure(jsonArray.getJSONObject(i).getString("Pressure"));
                        userWeeklyCondition.setAbdominalPain(jsonArray.getJSONObject(i).getString("Abdominal_pain"));
                        userWeeklyCondition.setFrequentAbdominalPain(jsonArray.getJSONObject(i).getString("Frequent_abdominal_pain"));
                        userWeeklyCondition.setProteinInUrine(jsonArray.getJSONObject(i).getString("Protein_in_urine"));
                        userWeeklyCondition.setRhType(jsonArray.getJSONObject(i).getString("RH_type"));
                        userWeeklyCondition.setVaginalDischarge(jsonArray.getJSONObject(i).getString("Vaginal_discharge"));
                        userWeeklyCondition.setBleeding(jsonArray.getJSONObject(i).getString("Bleeding"));
                        userWeeklyCondition.setWeightAfter(jsonArray.getJSONObject(i).getString("Weight_after"));
                        userWeeklyCondition.setDisease(jsonArray.getJSONObject(i).getString("Disease"));
                        userWeeklyCondition.setWeekNum(jsonArray.getJSONObject(i).getString("WeakN"));
                        weeklyConditions.add(userWeeklyCondition);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                requestQueue.stop();
                Toolbar toolbar = findViewById(R.id.question_toolbar);
                toolbar.setVisibility(View.VISIBLE);
                setSupportActionBar(toolbar);
                setViewsData(userWeeklyCondition);

            };

            errorListener = error -> {
                error.printStackTrace();
                requestQueue.stop();
            };

            createOrGetUserWeeklyQuestions = new StringRequest(Request.Method.POST, pageWeeklyResultUrl, serverResponse, errorListener) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<>();
                    data.put("user_id", String.valueOf(user.getId()));
                    return data;
                }
            };
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(createOrGetUserWeeklyQuestions);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qustions_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        for (int i = 0; i < weeklyConditions.size(); i++) {
            menu.add(0, i, Menu.NONE, "Week" + weeklyConditions.get(i).getWeekNum());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        userWeeklyCondition = weeklyConditions.get(item.getItemId());
        setViewsData(userWeeklyCondition);
        return true;
    }

    private void setViewsData(UserWeeklyCondition userWeeklyCondition) {
        try {
            weightAfter.setText(userWeeklyCondition.getWeightAfter());
            weightAfter.setEnabled(false);
            percent_of_hemogli.setText(userWeeklyCondition.getHeamoglobin());
            percent_of_hemogli.setEnabled(false);
            glucose.setText(userWeeklyCondition.getGlucose());
            glucose.setEnabled(false);
            ptt.setText(userWeeklyCondition.getPTT());
            ptt.setEnabled(false);

            backache.setChecked(userWeeklyCondition.getBackache().equals("Yes"));
            backache.setEnabled(false);
            headache.setChecked(userWeeklyCondition.getHeadache().equals("Yes"));
            headache.setEnabled(false);
            dizzy.setChecked(userWeeklyCondition.getDizzy().equals("Yes"));
            dizzy.setEnabled(false);
            breast.setChecked(userWeeklyCondition.getBreastTenderness().equals("Yes"));
            breast.setEnabled(false);
            tired.setChecked(userWeeklyCondition.getTired().equals("Yes"));
            tired.setEnabled(false);
            vomit.setChecked(userWeeklyCondition.getVomiting().equals("Yes"));
            vomit.setEnabled(false);
            leg.setChecked(userWeeklyCondition.getLegCramps().equals("Yes"));
            leg.setEnabled(false);
            swollen.setChecked(userWeeklyCondition.getSwollen().equals("Yes"));
            swollen.setEnabled(false);
            heartburn.setChecked(userWeeklyCondition.getHeartBurn().equals("Yes"));
            heartburn.setEnabled(false);
            waterBreak.setChecked(userWeeklyCondition.getPartialWaterBreak().equals("Yes"));
            waterBreak.setEnabled(false);
            visualDisturbance.setChecked(userWeeklyCondition.getVisualDisturbance().equals("Yes"));
            visualDisturbance.setEnabled(false);
            darkerPlaces.setChecked(userWeeklyCondition.getDarkerPlaces().equals("Yes"));
            darkerPlaces.setEnabled(false);
            breath.setChecked(userWeeklyCondition.getOutOfBreath().equals("Yes"));
            breath.setEnabled(false);
            babyMoves.setChecked(userWeeklyCondition.getBabyMoves().equals("Yes"));
            babyMoves.setEnabled(false);
            insomnia.setChecked(userWeeklyCondition.getInsomnia().equals("Yes"));
            insomnia.setEnabled(false);
            infection.setChecked(userWeeklyCondition.getGynecologicalInfection().equals("Yes"));
            infection.setEnabled(false);
            breastLeek.setChecked(userWeeklyCondition.getBreastLeak().equals("Yes"));
            breastLeek.setEnabled(false);
            flattenedNavel.setChecked(userWeeklyCondition.getNavelIsFlattened().equals("Yes"));
            flattenedNavel.setEnabled(false);
            toiletMoreOften.setChecked(userWeeklyCondition.getToiletMoreOften().equals("Yes"));
            toiletMoreOften.setEnabled(false);
            thirsty.setChecked(userWeeklyCondition.getThirstyAsLong().equals("Yes"));
            thirsty.setEnabled(false);
            dehydratio.setChecked(userWeeklyCondition.getDehydration().equals("Yes"));
            dehydratio.setEnabled(false);


            Log.e("a77a", userWeeklyCondition.getPressure().equals("Normal") + "");
            pressureLow.setChecked(userWeeklyCondition.getPressure().equals("Low"));
            pressureLow.setEnabled(false);
            pressureNormal.setChecked(userWeeklyCondition.getPressure().equals("Normal"));
            pressureNormal.setEnabled(false);
            pressureHigh.setChecked(userWeeklyCondition.getPressure().equals("High"));
            pressureHigh.setEnabled(false);

            Log.e("a77o", userWeeklyCondition.getAbdominalPain());
            once.setChecked(userWeeklyCondition.getAbdominalPain().equals("Yes"));
            once.setEnabled(false);
            freq.setChecked(userWeeklyCondition.getFrequentAbdominalPain().equals("Yes"));
            freq.setEnabled(false);

            yes.setChecked(userWeeklyCondition.getProteinInUrine().equals("Yes"));
            yes.setEnabled(false);
            no.setChecked(userWeeklyCondition.getProteinInUrine().equals("No"));
            no.setEnabled(false);

            negative.setChecked(userWeeklyCondition.getRhType().equals("Negative"));
            negative.setEnabled(false);
            positive.setChecked(userWeeklyCondition.getRhType().equals("Positive"));
            positive.setEnabled(false);

            colord.setChecked(userWeeklyCondition.getVaginalDischarge().equals("Colored"));
            colord.setEnabled(false);
            uncolord.setChecked(userWeeklyCondition.getVaginalDischarge().equals("Normal"));
            uncolord.setEnabled(false);

            highPleeding.setChecked(userWeeklyCondition.getBleeding().equals("High"));
            highPleeding.setEnabled(false);
            mediumPleeding.setChecked(userWeeklyCondition.getBleeding().equals("Medium"));
            mediumPleeding.setEnabled(false);
            lowPleeding.setChecked(userWeeklyCondition.getBleeding().equals("Low"));
            lowPleeding.setEnabled(false);
            noPleeding.setChecked(userWeeklyCondition.getBleeding().equals("No"));
            noPleeding.setEnabled(false);

            questions_apply.setVisibility(View.GONE);
            diseas_result.setText("For Week " + userWeeklyCondition.getWeekNum() + " : " + userWeeklyCondition.getDisease());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
