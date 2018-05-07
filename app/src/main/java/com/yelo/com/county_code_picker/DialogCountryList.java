package com.yelo.com.county_code_picker;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.yelo.com.R;
import com.yelo.com.utility.ClickListener;
import com.yelo.com.utility.CommonClass;

import java.util.ArrayList;

/**
 * <h>DialogCountryList</h>
 * <p>
 *     In this class we used to open simple dialog pop-up to select
 *     your own country code.
 * </p>
 * @since 4/12/2017
 */
public class DialogCountryList
{
    private static final String TAG = DialogCountryList.class.getSimpleName();
    private Activity mActivity;
    private ArrayList<Country> arrayListCountry;
    private ArrayList<Country> arrayListFilter;
    private Dialog countryPickerDialog;
    private RecyclerView rV_countryList;
    private CountryPickerRvAdap countryPickerRvAdap;

    public DialogCountryList(Activity mActivity, ArrayList<Country> arrayListCountry) {
        this.mActivity = mActivity;
        this.arrayListCountry = arrayListCountry;
        arrayListFilter=new ArrayList<>();
    }

    /**
     * <h>showCountryCodePicker</h>
     * <p>
     *     In this method we used to set all country iso code and number on dialog pop-up.
     * </p>
     * @param listener The callback variable of the SetCountryCodeListener interface
     */
    public void showCountryCodePicker(final SetCountryCodeListener listener)
    {
        countryPickerDialog = new Dialog(mActivity);
        countryPickerDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        countryPickerDialog.setContentView(R.layout.dialog_show_country_list);
        countryPickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        countryPickerDialog.getWindow().setLayout((int)(CommonClass.getDeviceWidth(mActivity)*0.9),(int)(CommonClass.getDeviceHeight(mActivity)*0.9));

        rV_countryList= (RecyclerView) countryPickerDialog.findViewById(R.id.rV_countryList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(mActivity);
        rV_countryList.setLayoutManager(layoutManager);

        //setRvAdapter(arrayListCountry,listener);
        countryPickerRvAdap=new CountryPickerRvAdap(arrayListCountry,listener,countryPickerDialog);
        rV_countryList.setAdapter(countryPickerRvAdap);
        countryPickerRvAdap.notifyDataSetChanged();

        // Search
        final EditText eT_searchCode= (EditText) countryPickerDialog.findViewById(R.id.eT_searchCode);
        eT_searchCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String searchedText=eT_searchCode.getText().toString();
                countryPickerRvAdap.getFilter().filter(searchedText);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        countryPickerDialog.show();
    }
}
