package com.sdei.farmx.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sdei.farmx.R;
import com.sdei.farmx.activity.MainActivity;
import com.sdei.farmx.adapter.NothingSelectedSpinnerAdapter;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.callback.NetworkErrorCallback;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.callback.RecyclerMoreActionCallback;
import com.sdei.farmx.databinding.FragmentAddEquipmentBinding;
import com.sdei.farmx.dataobject.Category;
import com.sdei.farmx.dataobject.District;
import com.sdei.farmx.dataobject.Equipment;
import com.sdei.farmx.dataobject.ImagePath;
import com.sdei.farmx.dataobject.ItemDateObject;
import com.sdei.farmx.dataobject.ManufacturerListingResponse;
import com.sdei.farmx.dataobject.SingleSelectionItem;
import com.sdei.farmx.dataobject.State;
import com.sdei.farmx.dataobject.UploadImageResponse;
import com.sdei.farmx.db.DBSource;
import com.sdei.farmx.dialog.SingleSelectionDialog;
import com.sdei.farmx.helper.AppInstance;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppLogger;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static com.sdei.farmx.apimanager.ApiManager.callService;

public class EquipmentAddFragment
        extends EquipmentFragment
        implements RecyclerMoreActionCallback, View.OnClickListener, ApiServiceCallback, NetworkErrorCallback {

    private FragmentAddEquipmentBinding binding;
    private LinearLayout availabilityDateLl;
    private TextInputLayout nameTextInputLayout;

    private ScrollView scrollView;

    private EditText nameEt;
    private EditText quantityEt;
    private EditText priceEt;
    private EditText addressEt;
    private EditText cityEt;
    private EditText pincodeEt;
    private EditText timePeriodEt;

    private TextView availabilityDateTv;

    private Spinner categorySpinner;
    private Spinner varietySpinner;
    private Spinner stateSpinner;
    private Spinner districtSpinner;
    private Spinner timePeriodUnitSpinner;
    private Spinner manufacturerSpinner;
    private Spinner modelYearSpinner;
    private Spinner priceUnitSpinner;

    private RadioGroup paymentModeRg;
    private RadioGroup rentSellRg;

    private SingleSelectionDialog sDialog = null;

    private String actionType;

    private boolean manuallySelectedSpinnerValue = false;
    private boolean manuallySelectedState = false;
    private boolean manuallySelectedCategory = false;

    private Equipment editEquipmentObject;

    private ArrayList<String> categoriesList;
    private ArrayList<String> varietiesList;
    private ArrayList<String> stateList;
    private ArrayList<String> manufacturerList;
    private ArrayList<String> districtList;
    private ArrayList<Category> categories;
    private ArrayList<State> statesList;
    private ArrayList<String> modelYearList;

    private String timePeriods[];
    private String[] paths;

    private int uploadingImageIndex = -1;
    private int maxPastYears = 50;

    private Equipment equipmentObject;
    private ManufacturerListingResponse manufacturerResponse;

    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    /**
     * Called when a fragment is first attached to its context
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    /**
     * Called to have the fragment instantiate its user interface view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_add_equipment, container, false);
        setBindingData(binding);
        initViews(binding);
        return binding.getRoot();
    }

    /**
     * Called when the fragment's activity has been created and this fragment's view hierarchy instantiated
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        changeAppHeader(this, getString(R.string.add_equipment));
        getArgs();
        bindData();
        setViewListeners();
    }

    private void getArgs() {

        Bundle arguments = getArguments();

        if (arguments != null) {

            actionType = arguments.getString("actionType");

            if (!TextUtils.isEmpty(actionType) && actionType.equals("edit"))
                editEquipmentObject = (Equipment) arguments.getSerializable("object");

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.submit_tv:
                AppUtils.hideKeyboard((Activity) activityContext);
                validateParams();
                break;

            case R.id.upload_image_tv:
                openAddImagesSourcesDialog();
                break;

            case R.id.availability_date_ll:

                if (equipmentObject.getRentSell().equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.RENT)) {

                    openDatePickerDialog();

                }

                break;

        }

    }

    private void openDatePickerDialog() {

        Calendar calender = Calendar.getInstance();

        DatePickerDialog dialog
                = new DatePickerDialog(activityContext, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view,
                                  int year,
                                  int monthOfYear,
                                  int dayOfMonth) {

                ItemDateObject object = equipmentObject.getItemDateObj();
                object.setDay(String.valueOf(dayOfMonth));
                object.setMonth(String.valueOf(monthOfYear + 1));
                object.setYear(String.valueOf(year));
                object.setFormatted(object.getDay()
                        + "/" + object.getMonth()
                        + "/" + object.getYear());
                object.setMomentObj(
                        AppUtils.getFormattedDate(object.getFormatted(),
                                AppConstants.DATE_FORMAT.FORMAT_3));

                equipmentObject.setAvailableFrom(object);
                equipmentObject.setItemDateObj(object);

                availabilityDateTv.setTextColor(ContextCompat.getColor(activityContext, R.color.text_black));

            }

        }, calender.get(Calendar.YEAR),
                calender.get(Calendar.MONTH),
                calender.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        if (equipmentObject.getAvailableFrom() != null
                && equipmentObject.getItemDateObj() != null
                && !TextUtils.isEmpty(equipmentObject.getItemDateObj().getYear())
                && !TextUtils.isEmpty(equipmentObject.getItemDateObj().getMonth())
                && !TextUtils.isEmpty(equipmentObject.getItemDateObj().getDay())) {

            dialog.updateDate(Integer.parseInt(equipmentObject.getItemDateObj().getYear()),
                    Integer.parseInt(equipmentObject.getItemDateObj().getMonth()) - 1,
                    Integer.parseInt(equipmentObject.getItemDateObj().getDay()));

        }

        dialog.show();

    }

    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (!AppUtils.isSessionExpired(activityContext, response)) {

            if (response.isSuccess() != null
                    && response.isSuccess()) {

                if (apiIndex == AppConstants.API_INDEX.STATE) {

                    statesList = AppUtils.getParsedDataArrayFromResponse(response, State.class);
                    setSpinnerStatesListing(true);
                    DBSource.getInstance(activityContext).saveStates(statesList);
                    getCategoryListing(AppConstants.CATEGORY_TYPE.EQUIPMENTS, EquipmentAddFragment.this);

                } else if (apiIndex == AppConstants.API_INDEX.CATEGORIES) {

                    categories = AppUtils.getParsedDataArrayFromResponse(response, Category.class);
                    addCategoryItems();
                    getManufacturerListing();

                } else if (apiIndex == AppConstants.API_INDEX.ADD_EQUIPMENT
                        || apiIndex == AppConstants.API_INDEX.UPDATE_EQUIPMENT) {

                    String message =
                            apiIndex == AppConstants.API_INDEX.UPDATE_EQUIPMENT ? getString(R.string.equipment_updated_successfully) : getString(R.string.equipment_added_successfully);
                    Toast.makeText(activityContext, message, Toast.LENGTH_SHORT).show();

                    //finishFragment();

                    AppUtils.clearFragmentStack(getFragmentManager());
                    changeAppHeader(
                            AppConstants.FragmentConstant.fragment, null);

//                    if (apiIndex == AppConstants.API_INDEX.ADD_EQUIPMENT)
//                        openMyEquipmentsPage();
//                    else {
//                        refreshItemListing(false);
                    AppUtils.hideProgressDialog();
                    //   }

                } else if (apiIndex == AppConstants.API_INDEX.UPLOAD_IMAGE) {

                    if (response.getData() != null) {

                        UploadImageResponse res = AppUtils.getParsedData(response.getData(), UploadImageResponse.class);
                        if (res != null) {

                            ArrayList<String> images = equipmentObject.getImages();
                            ArrayList<ImagePath> imagePath = equipmentObject.getImagesPath();

                            if (uploadingImageIndex < 0) {
                                images.add(res.getFullPath());

                                ImagePath path = new ImagePath();
                                path.setType(AppConstants.IMAGES.EQUIPMENTS);
                                path.setPath(res.getFullPath());
                                imagePath.add(path);

                            } else {

                                images.add(uploadingImageIndex - 1, res.getFullPath());

                                ImagePath path = new ImagePath();
                                path.setType(AppConstants.IMAGES.EQUIPMENTS);
                                path.setPath(res.getFullPath());
                                imagePath.add(uploadingImageIndex - 1, path);

                            }

                            equipmentObject.setImages(images);
                            equipmentObject.setImagesPath(imagePath);

                        }

                        if (!uploadImageFileToServer()) {
                            AppUtils.hideProgressDialog();
                        }

                    }

                } else if (apiIndex == AppConstants.API_INDEX.MANUFACTURER) {

                    manufacturerResponse
                            = AppUtils.getParsedData(response.getData(), ManufacturerListingResponse.class);
                    getManufacturerParsedList();
                    prepopulateDataForUpdate();
                    AppUtils.hideProgressDialog();

                }

            } else if (response.getError() != null
                    && !TextUtils.isEmpty(response.getError().getMessage())) {

                Toast.makeText(activityContext, response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                AppUtils.hideProgressDialog();

            }

        } else {

            AppUtils.hideProgressDialog();

        }

    }

    private void openMyEquipmentsPage() {

        if (AppConstants.FragmentConstant.fragment instanceof EquipmentMyEquipmentsFragment) {

            refreshItemListing(true);

        } else {

            MainActivity activity = (MainActivity) activityContext;
            activity.openMyEquipmentsFragment(activityContext);

        }

    }

    private void refreshItemListing(boolean fetchDataFromServer) {

        try {
            EquipmentMyEquipmentsFragment fragment
                    = (EquipmentMyEquipmentsFragment) AppConstants.FragmentConstant.fragment;
            fragment.refreshItemsList(fetchDataFromServer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onException(int apiIndex, Throwable t) {

        Fragment fragment = null;

        if (apiIndex != AppConstants.API_INDEX.ADD_EQUIPMENT
                && apiIndex != AppConstants.API_INDEX.UPDATE_EQUIPMENT
                && apiIndex != AppConstants.API_INDEX.UPLOAD_IMAGE) {
            fragment = EquipmentAddFragment.this;
        }

        if (showErrorToast(fragment, t.getMessage()))
            Toast.makeText(activityContext, t.getMessage(), Toast.LENGTH_SHORT).show();

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onError(int apiIndex, String message) {
        Toast.makeText(activityContext, "" + message, Toast.LENGTH_SHORT).show();
        AppUtils.hideProgressDialog();
    }

    private void setBindingData(FragmentAddEquipmentBinding binding) {

        binding.setHandler(this);

        timePeriods = getResources().getStringArray(R.array.equipment_availability_period_units);

        initializeObjectForBinding();
        binding.setData(equipmentObject);

        binding.setTimePeriodArray(new ArrayList<>(Arrays.asList(timePeriods)));

        categoriesList = new ArrayList<>();
        binding.setCategories(categoriesList);

        manufacturerList = new ArrayList<>();
        binding.setManufacturerList(manufacturerList);

        modelYearList = new ArrayList<>(generateModelYearList());
        binding.setYears(modelYearList);

        varietiesList = new ArrayList<>();
        binding.setVarieties(varietiesList);

        stateList = new ArrayList<>();
        binding.setStates(stateList);

        districtList = new ArrayList<>();
        binding.setDistricts(districtList);

    }

    private ArrayList<String> generateModelYearList() {

        ArrayList<String> yearList = new ArrayList<>();

        int year = Calendar.getInstance().get(Calendar.YEAR);

        for (int i = 0; i < maxPastYears; i++) {

            yearList.add(String.valueOf(year - i));

        }

        return yearList;

    }

    private void initializeObjectForBinding() {

        if (AppInstance.appUser == null) {
            AppUtils.initUserObject(activityContext);
        }

        equipmentObject = new Equipment();
        equipmentObject.setUser(AppInstance.appUser.getId());
        equipmentObject.setRentSell(AppConstants.API_STRING_CONSTANTS.RENT);
        equipmentObject.setItemDateObj(new ItemDateObject());
        equipmentObject.setAvailableFrom(equipmentObject.getItemDateObj());
        equipmentObject.setAvalibilityperiodUnits(timePeriods[0]);
        equipmentObject.setPaymentMethod(AppConstants.API_STRING_CONSTANTS.COD);
        equipmentObject.setPriceUnit(timePeriods[0]);
        equipmentObject.setImages(new ArrayList<String>());
        equipmentObject.setImagesPath(new ArrayList<ImagePath>());

    }

    private void initViews(FragmentAddEquipmentBinding binding) {

        scrollView = binding.scrollView;

        nameTextInputLayout = binding.nameTil;

        availabilityDateTv = binding.availabilityDateTv;
        availabilityDateLl = binding.availabilityDateLl;

        nameEt = binding.nameEt;
        quantityEt = binding.quantityEt;
        priceEt = binding.priceEt;
        addressEt = binding.addressEt;
        cityEt = binding.cityEt;
        pincodeEt = binding.pincodeEt;
        timePeriodEt = binding.timePeriodEt;

        categorySpinner = binding.categorySpinner;
        varietySpinner = binding.varietySpinner;
        stateSpinner = binding.stateSpinner;
        districtSpinner = binding.districtSpinner;
        timePeriodUnitSpinner = binding.timePeriodSpinner;
        manufacturerSpinner = binding.manufacturerSpinner;
        modelYearSpinner = binding.modelYearSpinner;
        priceUnitSpinner = binding.priceUnitSpinner;

        paymentModeRg = binding.paymentRg;
        rentSellRg = binding.rentRg;

    }

    public void bindData() {

        varietiesList.add("");
        districtList.add("");
        statesList = DBSource.getInstance(activityContext).getStates();

        if (AppUtils.isNetworkAvailable(activityContext, EquipmentAddFragment.this)) {

            if (statesList == null || statesList.size() == 0) {

                getStateListing(EquipmentAddFragment.this);

            } else {

                setSpinnerStatesListing(false);
                getCategoryListing(AppConstants.CATEGORY_TYPE.EQUIPMENTS, EquipmentAddFragment.this);

            }

        }

    }

    private void setViewListeners() {

        AppUtils.addTextChangedListener(nameEt);
        AppUtils.addTextChangedListener(quantityEt);
        AppUtils.addTextChangedListener(priceEt);
        AppUtils.addTextChangedListener(addressEt);
        AppUtils.addTextChangedListener(cityEt);
        AppUtils.addTextChangedListener(pincodeEt);
        AppUtils.addTextChangedListener(timePeriodEt);

        manufacturerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                position = position - 1;

                if (manuallySelectedSpinnerValue
                        && position >= 0
                        && position < manufacturerResponse.getManufacturer().size()) {

                    manuallySelectedSpinnerValue = false;
                    equipmentObject.setCompanyManufacturer(manufacturerResponse.getManufacturer().get(position).getId());

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        priceUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (manuallySelectedSpinnerValue
                        && position >= 0
                        && position < timePeriods.length) {

                    manuallySelectedSpinnerValue = false;
                    equipmentObject.setPriceUnit(timePeriods[position]);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        modelYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                position = position - 1;

                if (manuallySelectedSpinnerValue
                        && position >= 0
                        && position < modelYearList.size()) {

                    manuallySelectedSpinnerValue = false;
                    equipmentObject.setModelyear(modelYearList.get(position));

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                position = position - 1;

                if (manuallySelectedCategory
                        && position >= 0
                        && position < categoriesList.size()) {

                    manuallySelectedCategory = false;
                    varietiesList.clear();
                    varietySpinner.setSelection(0);
                    equipmentObject.setVariety(null);
                    getVarietyArrayList(categoriesList.get(position));
                    equipmentObject.setCategory(categories.get(position).getId());

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        varietySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                position = position - 1;
                if (manuallySelectedSpinnerValue
                        && position >= 0
                        && position < varietiesList.size()) {
                    manuallySelectedSpinnerValue = false;
                    equipmentObject.setVariety(varietiesList.get(position));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                position = position - 1;

                if (manuallySelectedState
                        && position >= 0 && position < stateList.size()) {

                    manuallySelectedState = false;
                    districtList.clear();
                    districtSpinner.setSelection(0);
                    equipmentObject.setDistrict(null);

                    getDistrictArrayList(stateList.get(position));
                    equipmentObject.setState(stateList.get(position));

                    if (!TextUtils.isEmpty(equipmentObject.getDistrict())) {

                        for (int i = 0; i < districtList.size(); i++) {

                            if (equipmentObject.getDistrict().equals(districtList.get(i))) {
                                districtSpinner.setSelection(i + 1);
                                break;
                            }

                        }

                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                position = position - 1;
                if (manuallySelectedSpinnerValue
                        && position >= 0
                        && position < districtList.size()) {
                    manuallySelectedSpinnerValue = false;
                    equipmentObject.setDistrict(districtList.get(position));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        timePeriodUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (manuallySelectedSpinnerValue
                        && timePeriods != null
                        && position >= 0
                        && position < timePeriods.length) {
                    manuallySelectedSpinnerValue = false;
                    equipmentObject.setAvalibilityperiodUnits(timePeriods[position]);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        varietySpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP
                        && varietiesList.size() == 1
                        && varietiesList.get(0).equals("")) {

                    Toast.makeText(activityContext,
                            getString(R.string.please_select_category),
                            Toast.LENGTH_SHORT).show();
                    return true;

                } else {

                    manuallySelectedSpinnerValue = true;

                }

                return false;

            }
        });

        setOnTouchListener(modelYearSpinner);
        setOnTouchListener(manufacturerSpinner);
        setOnTouchListener(categorySpinner);
        setOnTouchListener(stateSpinner);
        setOnTouchListener(timePeriodUnitSpinner);

        districtSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        && districtList.size() == 1
                        && districtList.get(0).equals("")) {
                    Toast.makeText(activityContext, getString(R.string.please_select_state), Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    manuallySelectedSpinnerValue = true;
                }
                return false;
            }
        });

        stateSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                manuallySelectedState = true;
                return false;
            }
        });

        categorySpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                manuallySelectedCategory = true;
                return false;
            }
        });

        paymentModeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == R.id.cod_rb) {
                    equipmentObject.setPaymentMethod(AppConstants.API_STRING_CONSTANTS.COD);
                } else if (checkedId == R.id.cheque_rb) {
                    equipmentObject.setPaymentMethod(AppConstants.API_STRING_CONSTANTS.CHEQUE);
                } else {
                    equipmentObject.setPaymentMethod(AppConstants.API_STRING_CONSTANTS.NET_BANKING);
                }

            }
        });

        rentSellRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.rent_rb) {
                    equipmentObject.setRentSell(AppConstants.API_STRING_CONSTANTS.RENT);
                } else {
                    equipmentObject.setRentSell(AppConstants.API_STRING_CONSTANTS.SELL);
                }
            }
        });

    }

    private void setOnTouchListener(final Spinner spinner) {

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                manuallySelectedSpinnerValue = true;
                return false;

            }
        });

    }

    private void prepopulateDataForUpdate() {

        if (!TextUtils.isEmpty(actionType) && actionType.equals("edit")) {

            nameTextInputLayout.setHint(getString(R.string.equipment_name));
            equipmentObject = editEquipmentObject;
            setImagesPath();
            equipmentObject.setUser(AppInstance.appUser.getId());

            if (equipmentObject.getCategoryObj() != null) {
                equipmentObject.setCategory(equipmentObject.getCategoryObj().getId());
                getVarietyArrayList(equipmentObject.getCategoryObj().getName());
            } else {
                equipmentObject.setItemDateObj(new ItemDateObject());
                equipmentObject.setAvailableFrom(equipmentObject.getItemDateObj());
            }

            binding.setData(equipmentObject);

        } else {

            equipmentObject.setState(AppInstance.appUser.getState());
            equipmentObject.setDistrict(AppInstance.appUser.getDistrict());
            equipmentObject.setCity(AppInstance.appUser.getCity());
            equipmentObject.setAddress(AppInstance.appUser.getAddress());
            equipmentObject.setPincode(AppInstance.appUser.getPincode());

        }

        getDistrictArrayList(equipmentObject.getState());

    }

    private void setImagesPath() {

        if (equipmentObject.getImages() != null && equipmentObject.getImages().size() > 0) {

            ArrayList<ImagePath> imagesPath = equipmentObject.getImagesPath();
            if (imagesPath == null) {
                imagesPath = new ArrayList<>();
            }
            imagesPath.clear();

            for (int i = 0; i < equipmentObject.getImages().size(); i++) {

                ImagePath path = new ImagePath();
                path.setType(AppConstants.IMAGES.EQUIPMENTS);
                path.setPath(equipmentObject.getImages().get(i));
                imagesPath.add(path);

            }

            equipmentObject.setImagesPath(imagesPath);

        } else {

            equipmentObject.setImagesPath(new ArrayList<ImagePath>());

        }

    }

    private void setSpinnerStatesListing(boolean refreshAdapter) {

        if (statesList != null && statesList.size() > 0) {

            stateList.clear();

            for (int i = 0; i < statesList.size(); i++) {
                stateList.add(statesList.get(i).getStateName());
            }

            if (refreshAdapter) {
                notifySpinnerAdapter(stateSpinner);
            }


        }

    }

    private void getManufacturerListing() {

        if (AppUtils.isNetworkAvailable(activityContext, EquipmentAddFragment.this)) {

            callService(activityContext,
                    AppConstants.API_INDEX.MANUFACTURER,
                    EquipmentAddFragment.this,
                    true,
                    null,
                    null);

        }


    }

    private void getDistrictArrayList(String stateName) {

        if (statesList != null && statesList.size() > 0) {

            for (int i = 0; i < statesList.size(); i++) {

                if (stateName != null && stateName.equalsIgnoreCase(statesList.get(i).getStateName())) {

                    ArrayList<District> dList = statesList.get(i).getDistricts();
                    districtList.clear();

                    if (dList != null && dList.size() > 0) {

                        for (int j = 0; j < dList.size(); j++) {
                            districtList.add(dList.get(j).getDistrictName());
                        }

                    }

                }
            }

            if (districtList.size() > 0) {
                notifySpinnerAdapter(districtSpinner);
            }

        }

    }

    private void getVarietyArrayList(String categoryName) {

        if (categories != null && categories.size() > 0) {

            varietiesList.clear();

            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getName().equals(categoryName)
                        && categories.get(i).getVariety() != null) {
                    varietiesList.addAll(categories.get(i).getVariety());
                }
            }

            notifySpinnerAdapter(varietySpinner);

            if (varietiesList != null) {

                for (int j = 0; j < varietiesList.size(); j++) {

                    if (!TextUtils.isEmpty(equipmentObject.getVariety())
                            && equipmentObject.getVariety().equals(varietiesList.get(j))) {
                        varietySpinner.setSelection(j + 1);
                    }

                }

            }

        }

    }

    private void notifySpinnerAdapter(Spinner spinner) {

        NothingSelectedSpinnerAdapter adapter
                = (NothingSelectedSpinnerAdapter) spinner.getAdapter();

        if (adapter != null) {
            ArrayAdapter<String> sAdapter = adapter.getAdapter();
            sAdapter.notifyDataSetChanged();
        }

    }

    private void validateParams() {

        if (TextUtils.isEmpty(equipmentObject.getName())
                || equipmentObject.getName().isEmpty()) {

            AppUtils.showTextInputError(
                    (TextInputLayout) nameEt.getParentForAccessibility(),
                    getString(R.string.error_enter_equipment_name));
            nameEt.requestFocus();
            scrollView.scrollTo(0, nameEt.getTop());

        } else if (equipmentObject.getRentSell().equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.RENT)
                && (equipmentObject.getItemDateObj() == null || TextUtils.isEmpty(equipmentObject.getItemDateObj().getFormatted()))) {

            availabilityDateTv.setText(getString(R.string.error_select_date));
            availabilityDateTv.setTextColor(Color.RED);
            scrollView.scrollTo(0, nameEt.getTop());

        } else if (equipmentObject.getRentSell().equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.RENT)
                && (
                TextUtils.isEmpty(equipmentObject.getAvalibilityperiod())
                        || equipmentObject.getAvalibilityperiod().isEmpty()
                        || Double.parseDouble(equipmentObject.getAvalibilityperiod()) == 0)
                ) {

            AppUtils.showTextInputError(
                    (TextInputLayout) timePeriodEt.getParentForAccessibility(),
                    getString(R.string.error_enter_time_period));
            equipmentObject.setAvalibilityperiod("");
            timePeriodEt.requestFocus();
            scrollView.scrollTo(0, timePeriodEt.getTop());

        } else if (equipmentObject.getCategory() == null
                || TextUtils.isEmpty(equipmentObject.getCategory().toString())) {

            AppUtils.showSpinnerErrorMessage(categorySpinner,
                    getContext().getString(R.string.error_select_category));
            scrollView.scrollTo(0, timePeriodEt.getTop());

        } else if (equipmentObject.getCompanyManufacturer() == null
                || TextUtils.isEmpty(equipmentObject.getCompanyManufacturer().toString())) {

            AppUtils.showSpinnerErrorMessage(manufacturerSpinner,
                    getContext().getString(R.string.error_select_manufacturer));
            scrollView.scrollTo(0, categorySpinner.getTop());

        } else if (TextUtils.isEmpty(equipmentObject.getRate())
                || equipmentObject.getRate().isEmpty()
                || Double.parseDouble(equipmentObject.getRate()) == 0) {

            AppUtils.showTextInputError(
                    (TextInputLayout) priceEt.getParentForAccessibility(),
                    getString(R.string.error_enter_price));
            priceEt.requestFocus();
            equipmentObject.setRate("");
            scrollView.scrollTo(0, priceEt.getTop());

        } else if (TextUtils.isEmpty(equipmentObject.getQuantity())
                || equipmentObject.getQuantity().isEmpty()
                || Double.parseDouble(equipmentObject.getQuantity()) == 0) {

            AppUtils.showTextInputError(
                    (TextInputLayout) quantityEt.getParentForAccessibility(),
                    getString(R.string.error_enter_quantity));
            equipmentObject.setQuantity("");
            quantityEt.requestFocus();
            scrollView.scrollTo(0, quantityEt.getTop());

        } else if (TextUtils.isEmpty(equipmentObject.getAddress())
                || equipmentObject.getAddress().isEmpty()) {

            AppUtils.showTextInputError(
                    (TextInputLayout) addressEt.getParentForAccessibility(),
                    getString(R.string.error_enter_address));
            addressEt.requestFocus();
            scrollView.scrollTo(0, addressEt.getTop());

        } else if (TextUtils.isEmpty(equipmentObject.getCity())
                || equipmentObject.getCity().isEmpty()) {

            AppUtils.showTextInputError(
                    (TextInputLayout) cityEt.getParentForAccessibility(),
                    getString(R.string.error_enter_city));
            cityEt.requestFocus();
            scrollView.scrollTo(0, cityEt.getTop());

        } else if (TextUtils.isEmpty(equipmentObject.getState())) {

            AppUtils.showSpinnerErrorMessage(stateSpinner,
                    getContext().getString(R.string.error_select_state));
            scrollView.scrollTo(0, stateSpinner.getTop());

        } else if (TextUtils.isEmpty(equipmentObject.getDistrict())) {

            AppUtils.showSpinnerErrorMessage(districtSpinner,
                    getContext().getString(R.string.error_select_district));
            scrollView.scrollTo(0, districtSpinner.getTop());

        } else if (TextUtils.isEmpty(equipmentObject.getPincode())
                || equipmentObject.getPincode().isEmpty()
                || Double.parseDouble(equipmentObject.getPincode()) == 0) {

            AppUtils.showTextInputError(
                    (TextInputLayout) pincodeEt.getParentForAccessibility(),
                    getString(R.string.error_enter_pincode));
            equipmentObject.setPincode("");
            pincodeEt.requestFocus();

            pincodeEt.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            scrollView.scrollTo(0, pincodeEt.getTop());

        } else if (equipmentObject.getPincode().length() != 6) {

            AppUtils.showTextInputError(
                    (TextInputLayout) pincodeEt.getParentForAccessibility(),
                    getString(R.string.error_valid_pincode));
            pincodeEt.requestFocus();

            pincodeEt.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            scrollView.scrollTo(0, pincodeEt.getTop());

        }

//        else if (
//                equipmentObject.getSupplyAbility().equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.YES)
//                        && TextUtils.isEmpty(cropObject.getSupplyArea())) {
//
//            AppUtils.showSpinnerErrorMessage(supplyAreaSpinner,
//                    getContext().getString(R.string.error_select_supply_area));
//            scrollView.scrollTo(0, supplyAreaSpinner.getTop());
//
//        }
//        else if (
//                equipmentObject.getSupplyAbility().equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.YES)
//                        && (TextUtils.isEmpty(equipmentObject.getSupplyRange())
//                        || equipmentObject.getSupplyRange().isEmpty())) {
//
//            AppUtils.showTextInputError(
//                    (TextInputLayout) distanceEt.getParentForAccessibility(),
//                    getString(R.string.error_enter_distance));
//            distanceEt.requestFocus();
//            scrollView.scrollTo(0, distanceEt.getTop());
//
//        }
        else {

//            if (TextUtils.isEmpty(equipmentObject.getAvailablePeriod())
//                    || equipmentObject.getAvailablePeriod().isEmpty()) {
//                equipmentObject.setAvailablePeriod(null);
//            }
            publishItem();

        }

    }

    private void publishItem() {

        if (AppUtils.isNetworkAvailable(activityContext, true, 101)) {

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            try {
                String json = ow.writeValueAsString(equipmentObject);
                AppLogger.log("EquipmentAddFragment", "" + json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            int apiIndex;

            if (!TextUtils.isEmpty(actionType) && actionType.equals("edit")) {

                apiIndex = AppConstants.API_INDEX.UPDATE_EQUIPMENT;

            } else {

                apiIndex = AppConstants.API_INDEX.ADD_EQUIPMENT;

            }

            callService(activityContext,
                    apiIndex,
                    EquipmentAddFragment.this,
                    true,
                    equipmentObject,
                    getUserAccessToken());

        }

    }

    /**
     * opens the Image sources dialog from which user either select Camera or Gallery option.
     */
    private void openAddImagesSourcesDialog() {

        if (equipmentObject.getImages().size() < AppConstants.CustomGallery.MAXIMUM_IMAGES) {

            ArrayList<SingleSelectionItem> items = new ArrayList<>();
            items.add(AppUtils.getSingleSelectionItem(0, "", getString(R.string.camera), 0));
            items.add(AppUtils.getSingleSelectionItem(1, "", getString(R.string.gallery), 0));

            if (sDialog == null) {

                sDialog = new SingleSelectionDialog(activityContext,
                        items,
                        new RecyclerCallback() {
                            @Override
                            public void onItemClick(int position) {
                                performImagesAction(position);
                                sDialog.dismiss();
                            }

                            @Override
                            public void onChildItemClick(int parentIndex, int childIndex) {

                            }

                        });

                sDialog.setHeader(getString(R.string.add_image));

                sDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        sDialog = null;
                    }
                });

                sDialog.show();
            }

        } else {

            Toast.makeText(activityContext,
                    getString(R.string.you_can_select_maximum_images),
                    Toast.LENGTH_SHORT).show();

        }

    }

    /**
     * Perform the action requested from Image sources dialog
     *
     * @param action either 0 or 1. 0 for camera and 1 for gallery.
     */
    private void performImagesAction(int action) {

        switch (action) {

            case 0:
                openCameraWithPermissionsCheck();
                break;

            case 1:
                openPhoneGallery();
                break;

        }

    }

    /**
     * Open up the Gallery app after checking the required permissions.
     * If the permissions were not given yet by the user then it will request the
     * user to give permission for read from External Storage.
     */
    private void openPhoneGallery() {

        String externalStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE;

        boolean hasReadExternalStoragePermission = AppUtils.hasPermission(activityContext, externalStoragePermission);
        boolean requestReadExternalStorageRationale
                = ActivityCompat.shouldShowRequestPermissionRationale((Activity) activityContext, externalStoragePermission);

        if (hasReadExternalStoragePermission) {

            openGallery();

        } else if (requestReadExternalStorageRationale) {

            checkPermissions(activityContext,
                    AppConstants.AppPermission.ACTION_OPEN_GALLERY, externalStoragePermission);

        } else {

            MainActivity activity = (MainActivity) activityContext;
            activity.requestPermissionRationale(false, false, getString(android.R.string.ok), externalStoragePermission);

        }

    }

    /**
     * open up the Gallery App by using the Activity reference
     */
    private void openGallery() {

        int remainingImages
                = AppConstants.CustomGallery.MAXIMUM_IMAGES - equipmentObject.getImages().size();

        MainActivity activity = (MainActivity) activityContext;
        activity.openCustomGallery(remainingImages);

    }

    /**
     * Open up the camera app after checking the required permissions.
     * If the permissions were not given yet by the user then it will request the
     * user to give permission for open Camera and to write into the External Storage.
     */
    private void openCameraWithPermissionsCheck() {

        String cameraPermission = Manifest.permission.CAMERA;
        String externalStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        boolean hasCameraPermission = AppUtils.hasPermission(activityContext, cameraPermission);
        boolean hasWriteStoragePermission = AppUtils.hasPermission(activityContext, externalStoragePermission);

        boolean requestCameraPermissionRationale
                = ActivityCompat.shouldShowRequestPermissionRationale((Activity) activityContext, cameraPermission);
        boolean requestStoragePermissionRationale
                = ActivityCompat.shouldShowRequestPermissionRationale((Activity) activityContext, externalStoragePermission);

        if (hasCameraPermission
                && hasWriteStoragePermission) {

            openCamera();

        } else if ((!hasCameraPermission && !requestCameraPermissionRationale)
                && (!hasWriteStoragePermission && !requestStoragePermissionRationale)) {

            checkPermissions(activityContext,
                    AppConstants.AppPermission.ACTION_OPEN_CAMERA,
                    PERMISSIONS);

        } else if (!hasCameraPermission
                && !requestCameraPermissionRationale) {

            checkPermissions(activityContext,
                    AppConstants.AppPermission.ACTION_OPEN_CAMERA, cameraPermission);

        } else if (!hasWriteStoragePermission
                && !requestStoragePermissionRationale) {

            checkPermissions(activityContext,
                    AppConstants.AppPermission.ACTION_OPEN_CAMERA, externalStoragePermission);

        } else {

            MainActivity activity = (MainActivity) activityContext;
            activity.requestPermissionRationale(false, false, getString(android.R.string.ok), PERMISSIONS);

        }

    }

    /**
     * Add the path of the last clicked camera image to images arraylist
     *
     * @param path path of the camera image
     */
    public void afterCameraClick(String path) {

        if (equipmentObject.getImages().size() < AppConstants.CustomGallery.MAXIMUM_IMAGES) {

            ImagePath imagePath = new ImagePath();
            imagePath.setPath(path);
            imagePath.setType(AppConstants.IMAGES.EQUIPMENTS);

            publishImage(imagePath);

        }

    }

    /**
     * In this we got selected images path from Gallery
     *
     * @param paths images gallery path
     */
    public void fetchedImagesFromGallery(String[] paths) {

        if (equipmentObject.getImages().size() < AppConstants.CustomGallery.MAXIMUM_IMAGES) {

            int remainingImages
                    = AppConstants.CustomGallery.MAXIMUM_IMAGES - equipmentObject.getImages().size();

            if (remainingImages > paths.length) {
                this.paths = new String[paths.length];
            } else {
                this.paths = new String[remainingImages];
            }

            if (remainingImages > paths.length) {
                remainingImages = paths.length;
            }

            System.arraycopy(paths, 0, this.paths, 0, remainingImages);
            uploadingImageIndex = 0;
            uploadImageFileToServer();

        }

    }

    private boolean uploadImageFileToServer() {

        if (paths != null
                && Arrays.asList(paths).size() > uploadingImageIndex
                && uploadingImageIndex >= 0) {

            ImagePath path = new ImagePath();
            path.setPath(paths[uploadingImageIndex]);
            path.setType(AppConstants.IMAGES.EQUIPMENTS);
            publishImage(path);
            uploadingImageIndex = uploadingImageIndex + 1;
            return true;

        } else {

            uploadingImageIndex = -1;
            return false;

        }

    }

    private void publishImage(ImagePath path) {

        if (AppUtils.isNetworkAvailable(activityContext, true, 101)) {

            callService(
                    activityContext,
                    AppConstants.API_INDEX.UPLOAD_IMAGE,
                    EquipmentAddFragment.this,
                    true,
                    path,
                    getUserAccessToken());

        }

    }

    private void addCategoryItems() {

        if (categories != null && categories.size() > 0) {

            for (int i = 0; i < categories.size(); i++) {

                categoriesList.add(categories.get(i).getName());

            }

            notifySpinnerAdapter(categorySpinner);

        }

    }

    private void getManufacturerParsedList() {

        if (manufacturerResponse != null
                && manufacturerResponse.getManufacturer() != null
                && manufacturerResponse.getManufacturer().size() > 0) {

            manufacturerList.clear();

            for (int i = 0; i < manufacturerResponse.getManufacturer().size(); i++) {

                manufacturerList.add(manufacturerResponse.getManufacturer().get(i).getName());

            }

        }

        notifySpinnerAdapter(manufacturerSpinner);

    }

    @Override
    public void onNetworkYesClicked() {
        finishFragment();
    }

    @Override
    public void onItemClick(int position) {

        ArrayList<String> images = equipmentObject.getImages();
        ArrayList<ImagePath> imagesPath = equipmentObject.getImagesPath();

        if (images != null
                && images.size() > 0) {

            images.remove(position);
            equipmentObject.setImages(images);

            imagesPath.remove(position);
            equipmentObject.setImagesPath(imagesPath);

        }

    }

    @Override
    public void onChildItemClick(int parentIndex, int childIndex) {

    }

    @Override
    public void itemAction(String type, int position) {

    }
}
