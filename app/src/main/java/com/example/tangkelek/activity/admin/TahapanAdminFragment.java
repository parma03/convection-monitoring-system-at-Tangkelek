package com.example.tangkelek.activity.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.tangkelek.R;
import com.example.tangkelek.adapter.AdminTahapanAdapter;
import com.example.tangkelek.adapter.UserAdapter;
import com.example.tangkelek.databinding.FragmentTahapanAdminBinding;
import com.example.tangkelek.databinding.FragmentUserAdminBinding;
import com.example.tangkelek.model.TahapanModel;
import com.example.tangkelek.model.UserModel;
import com.example.tangkelek.util.ApiServer;
import com.google.gson.Gson;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class TahapanAdminFragment extends Fragment implements DialogAddTahapanActivity.OnTahapanAddedListener, DialogUpdateTahapanActivity.OnTahapanUpdatedListener {
    private FragmentTahapanAdminBinding binding;
    private List<TahapanModel> tahapanModelList;
    private AdminTahapanAdapter adapter;
    private AlertDialog dialog;
    private static final String URL_TAHAPAN = ApiServer.site_url_admin + "getTahapan.php";
    private static final String URL_SEARCH_TAHAPAN = ApiServer.site_url_admin + "searchTahapan.php";
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTahapanAdminBinding.inflate(inflater, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        tahapanModelList = new ArrayList<>();
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setMessage("Loading ......").setCancelable(false).build();
        mContext = requireContext();

        dataTahapan();

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });

        binding.kolomSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                searchData(keyword);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return binding.getRoot();
    }

    private void showAddDialog() {
        DialogAddTahapanActivity dialog = new DialogAddTahapanActivity();
        dialog.setOnTahapanAddedListener(this);
        dialog.showNow(getActivity().getSupportFragmentManager(), "add_tahapan");
    }

    private void searchData(String keyword) {
        dialog.dismiss();
        AndroidNetworking.post(URL_SEARCH_TAHAPAN)
                .addBodyParameter("keyword", keyword)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();
                            int code = response.getInt("code");
                            Log.d("response", "response::" + response);
                            if (code == 1) {
                                JSONArray array = response.getJSONArray("data");
                                tahapanModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject tahapanObject = array.getJSONObject(i);
                                    TahapanModel tahapan = gson.fromJson(tahapanObject.toString(), TahapanModel.class);
                                    tahapanModelList.add(tahapan);
                                }
                                adapter = new AdminTahapanAdapter(requireContext(), tahapanModelList);
                                binding.recyclerView.setAdapter(adapter);
                            } else {
                                tahapanModelList.clear();
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("catch", "gambarModel::" + e.toString());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Log.e("error", "onError: " + anError.getErrorBody());
                    }
                });
    }

    private void dataTahapan() {
        dialog.show();
        AndroidNetworking.get(URL_TAHAPAN)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();
                            Log.d("response", "response::" + response);
                            if (response.getString("code").equalsIgnoreCase("1")) {
                                JSONArray array = response.getJSONArray("data");
                                tahapanModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject beritaObject = array.getJSONObject(i);
                                    TahapanModel isi = gson.fromJson(beritaObject + "", TahapanModel.class);
                                    tahapanModelList.add(isi);
                                }
                                AdminTahapanAdapter adapter = new AdminTahapanAdapter(mContext, tahapanModelList);
                                binding.recyclerView.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("catch", "kantinmodel::" + e.toString());
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        dialog.dismiss();
                        Log.e("error", "onError: " + anError.getErrorBody());
                    }
                });
    }

    @Override
    public void onTahapanAdded(String message) {
        showSuccessNotification(message);
    }

    @Override
    public void onTahapanError(String errorMessage) {
        showErrorNotification(errorMessage);
    }

    private void showSuccessNotification(String message) {
        PopupDialog.getInstance(mContext)
                .setStyle(Styles.SUCCESS)
                .setHeading("BERHASIL !!!")
                .setDescription(message)
                .setCancelable(false)
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        dataTahapan();
                        super.onDismissClicked(dialog);
                    }
                });
    }

    private void showErrorNotification(String message) {
        PopupDialog.getInstance(mContext)
                .setStyle(Styles.FAILED)
                .setHeading("GAGAL !!!")
                .setDescription(message)
                .setCancelable(false)
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        dataTahapan();
                        super.onDismissClicked(dialog);
                    }
                });
    }
}