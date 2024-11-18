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
import com.example.tangkelek.adapter.UserAdapter;
import com.example.tangkelek.databinding.FragmentUserAdminBinding;
import com.example.tangkelek.model.UserModel;
import com.example.tangkelek.util.ApiServer;
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

public class UserAdminFragment extends Fragment implements DialogAddUserActivity.OnUserAddedListener, DialogUpdateUserActivity.OnUserUpdatedListener {
    private FragmentUserAdminBinding binding;
    private List<UserModel> userModelList;
    private UserAdapter adapter;
    private AlertDialog dialog;
    private static final String URL_USER = ApiServer.site_url_admin + "getUser.php";
    private static final String URL_SEARCH_USER = ApiServer.site_url_admin + "searchUser.php";
    private String selectedCategory = "";
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserAdminBinding.inflate(inflater, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        userModelList = new ArrayList<>();
        dialog = new SpotsDialog.Builder().setContext(requireContext()).setMessage("Loading ......").setCancelable(false).build();
        mContext = requireContext();

        dataUser();

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });

        binding.btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
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
        DialogAddUserActivity dialog = new DialogAddUserActivity();
        dialog.setOnUserAddedListener(this);
        dialog.showNow(getActivity().getSupportFragmentManager(), "add_konsumen");
    }

    private void showFilterDialog() {
        final String[] categories = {"Manajer", "Pengawas"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Pilih Role");
        builder.setItems(categories, (dialog, which) -> {
            selectedCategory = categories[which];
            filterDataByCategory(selectedCategory);
        });

        builder.show();
    }

    private void filterDataByCategory(String category) {
        dialog.show();
        AndroidNetworking.post(ApiServer.site_url_admin + "filterUser.php")
                .addBodyParameter("role", category)
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
                                userModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject userObject = array.getJSONObject(i);
                                    UserModel menu = gson.fromJson(userObject.toString(), UserModel.class);
                                    userModelList.add(menu);
                                }
                                adapter = new UserAdapter(requireContext(), userModelList);
                                binding.recyclerView.setAdapter(adapter);
                            } else {
                                userModelList.clear();
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

    private void searchData(String keyword) {
        dialog.dismiss();
        AndroidNetworking.post(URL_SEARCH_USER)
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
                                userModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject userObject = array.getJSONObject(i);
                                    UserModel user = gson.fromJson(userObject.toString(), UserModel.class);
                                    userModelList.add(user);
                                }
                                adapter = new UserAdapter(requireContext(), userModelList);
                                binding.recyclerView.setAdapter(adapter);
                            } else {
                                userModelList.clear();
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

    private void dataUser() {
        dialog.show();
        AndroidNetworking.get(URL_USER)
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
                                userModelList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject beritaObject = array.getJSONObject(i);
                                    UserModel isi = gson.fromJson(beritaObject + "", UserModel.class);
                                    userModelList.add(isi);
                                }
                                UserAdapter adapter = new UserAdapter(mContext, userModelList);
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
    public void onUserAdded(String message) {
        showSuccessNotification(message);
    }

    @Override
    public void onUserError(String errorMessage) {
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
                        dataUser();
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
                        dataUser();
                        super.onDismissClicked(dialog);
                    }
                });
    }
}