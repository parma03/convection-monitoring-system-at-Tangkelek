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
import com.example.tangkelek.adapter.AdminProdukusiAdapter;
import com.example.tangkelek.adapter.UserAdapter;
import com.example.tangkelek.databinding.FragmentProduksiAdminBinding;
import com.example.tangkelek.databinding.FragmentUserAdminBinding;
import com.example.tangkelek.model.KonveksiModel;
import com.example.tangkelek.model.UserModel;
import com.example.tangkelek.util.ApiServer;
import com.example.tangkelek.util.PrefManager;
import com.google.gson.Gson;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ProduksiAdminFragment extends Fragment implements DialogAddProduksiActivity.OnProduksiAddedListener{
    private FragmentProduksiAdminBinding binding;
    private List<KonveksiModel> konveksiModelList;
    private AdminProdukusiAdapter adapter;
    private static final String URL_PRODUKSI = ApiServer.site_url_admin + "getProduksi.php";
    private static final String URL_SEARCH_PRODUKSI = ApiServer.site_url_admin + "searchProduksi.php";
    private Context mContext;
    private AlertDialog dialog;
    PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProduksiAdminBinding.inflate(inflater, container, false);
        mContext = requireContext();
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setMessage("Loading ......").setCancelable(false).build();
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        konveksiModelList = new ArrayList<>();
        prefManager = new PrefManager(getContext());

        dataProduksi();

        switch (prefManager.getTipe()){
            case "admin":{
                binding.btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAddDialog();
                    }
                });
                break;
            }
            case "manajer":{
                binding.btnAdd.setVisibility(View.GONE);
                break;
            }
        }

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
        DialogAddProduksiActivity dialog = new DialogAddProduksiActivity();
        dialog.setOnProduksiAddedListener(this);
        dialog.showNow(getActivity().getSupportFragmentManager(), "add_produksi");
    }

    private void searchData(String keyword) {
        dialog.dismiss();
        AndroidNetworking.post(URL_SEARCH_PRODUKSI)
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
                                konveksiModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject konveksiObject = array.getJSONObject(i);
                                    KonveksiModel konveksi = gson.fromJson(konveksiObject.toString(), KonveksiModel.class);
                                    konveksiModelList.add(konveksi);
                                }
                                adapter = new AdminProdukusiAdapter(requireContext(), konveksiModelList);
                                binding.recyclerView.setAdapter(adapter);
                            } else {
                                konveksiModelList.clear();
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

    private void dataProduksi() {
        dialog.show();
        AndroidNetworking.get(URL_PRODUKSI)
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
                                konveksiModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject konveksiObject = array.getJSONObject(i);
                                    KonveksiModel konveksi = gson.fromJson(konveksiObject.toString(), KonveksiModel.class);
                                    konveksiModelList.add(konveksi);
                                }
                                AdminProdukusiAdapter adapter = new AdminProdukusiAdapter(mContext, konveksiModelList);
                                binding.recyclerView.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("catch", "produksimodel::" + e.toString());
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
    public void onProduksiAdded(String message) {
        showSuccessNotification(message);
    }

    @Override
    public void onProduksiError(String errorMessage) {
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
                        dataProduksi();
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
                        dataProduksi();
                        super.onDismissClicked(dialog);
                    }
                });
    }
}