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
import com.sdei.farmx.apimanager.ApiManager;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.callback.NetworkErrorCallback;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.callback.RecyclerMoreActionCallback;
import com.sdei.farmx.databinding.FragmentAddCropBinding;
import com.sdei.farmx.dataobject.Category;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.District;
import com.sdei.farmx.dataobject.ImagePath;
import com.sdei.farmx.dataobject.ItemDateObject;
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

public class CropAddFragment
        extends CropFragment
        implements View.OnClickListener, ApiServiceCallback, NetworkErrorCallback {

    private FragmentAddCropBinding binding;
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
    private EditText distanceEt;

    private TextView availabilityDateTv;

    private Spinner categorySpinner;
    private Spinner varietySpinner;
    private Spinner stateSpinner;
    private Spinner districtSpinner;
    public Spinner quantityUnitSpinner;
    private Spinner timePeriodUnitSpinner;
    private Spinner supplyAreaSpinner;

    private RadioGroup qualityRg;
    private RadioGroup supplyAvailabilityRg;
    private RadioGroup paymentModeRg;

    private SingleSelectionDialog sDialog = null;

    private boolean editCrop;

    private boolean manuallySelectedSpinnerValue = false;
    private boolean manuallySelectedState = false;
    private boolean manuallySelectedCategory = false;

    private Crop editCropObject;

    private ArrayList<String> categoriesList;
    private ArrayList<String> varietiesList;
    private ArrayList<String> stateList;
    private ArrayList<String> districtList;
    private ArrayList<Category> categories;
    private ArrayList<State> statesList;

    private String weightUnits[];
    private String timePeriods[];
    private String supplyAreas[];
    private String[] paths;

    private int uploadingImageIndex = -1;

    private Crop cropObject;

    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    RecyclerMoreActionCallback itemClickListener = new RecyclerMoreActionCallback() {
        @Override
        public void itemAction(String type, int position) {

        }

        @Override
        public void onItemClick(int position) {

            ArrayList<String> images = cropObject.getImages();
            ArrayList<ImagePath> imagesPath = cropObject.getImagesPath();

            if (images != null
                    && images.size() > 0) {

                images.remove(position);
                cropObject.setImages(images);
                imagesPath.remove(position);
                cropObject.setImagesPath(imagesPath);

            }

        }

        @Override
        public void onChildItemClick(int parentIndex, int childIndex) {

        }
    };

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
                = DataBindingUtil.inflate(inflater, R.layout.fragment_add_crop, container, false);
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
        changeAppHeader(this, getString(R.string.add_crop));
        getArgs();
        bindData();
        setViewListeners();
    }

    private void getArgs() {

        Bundle arguments = getArguments();

        if (arguments != null) {

            editCrop = arguments.getBoolean("editCrop");

            if (editCrop)
                editCropObject = (Crop) arguments.getSerializable("object");

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.publish_tv:
                AppUtils.hideKeyboard((Activity) activityContext);
                validateParams();
                break;

            case R.id.upload_image_tv:
                openAddImagesSourcesDialog();
                break;

            case R.id.availability_date_ll:

                Calendar calender = Calendar.getInstance();

                DatePickerDialog dialog
                        = new DatePickerDialog(activityContext, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view,
                                          int year,
                                          int monthOfYear,
                                          int dayOfMonth) {

                        ItemDateObject object = cropObject.getAvailableFromObj();
                        object.setDay(String.valueOf(dayOfMonth));
                        object.setMonth(String.valueOf(monthOfYear + 1));
                        object.setYear(String.valueOf(year));
                        object.setFormatted(object.getDay()
                                + "/" + object.getMonth()
                                + "/" + object.getYear());
                        object.setMomentObj(
                                AppUtils.getFormattedDate(object.getFormatted(),
                                        AppConstants.DATE_FORMAT.FORMAT_3));

                        cropObject.setAvailableFrom(object);
                        cropObject.setAvailableFromObj(object);

                        availabilityDateTv.setTextColor(ContextCompat.getColor(activityContext, R.color.text_black));

                    }

                }, calender.get(Calendar.YEAR),
                        calender.get(Calendar.MONTH),
                        calender.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                if (cropObject.getAvailableFrom() != null
                        && !TextUtils.isEmpty(cropObject.getAvailableFromObj().getYear())
                        && !TextUtils.isEmpty(cropObject.getAvailableFromObj().getMonth())
                        && !TextUtils.isEmpty(cropObject.getAvailableFromObj().getDay())) {

                    dialog.updateDate(Integer.parseInt(cropObject.getAvailableFromObj().getYear()),
                            Integer.parseInt(cropObject.getAvailableFromObj().getMonth()) - 1,
                            Integer.parseInt(cropObject.getAvailableFromObj().getDay()));

                }

                dialog.show();

                break;

        }

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
                    getCategoryListing(AppConstants.CATEGORY_TYPE.CROPS, CropAddFragment.this);

                } else if (apiIndex == AppConstants.API_INDEX.CATEGORIES) {

                    categories = AppUtils.getParsedDataArrayFromResponse(response, Category.class);
                    addCategoryItems();
                    prepopulateDataForEditCrop();
                    AppUtils.hideProgressDialog();

                } else if (apiIndex == AppConstants.API_INDEX.ADD_CROP
                        || apiIndex == AppConstants.API_INDEX.UPDATE_CROP) {

                    String message =
                            apiIndex == AppConstants.API_INDEX.UPDATE_CROP ? getString(R.string.crop_updated_successfully) : getString(R.string.crop_added_successfully);
                    Toast.makeText(activityContext, message, Toast.LENGTH_SHORT).show();

                    Crop newCrop = null;

                    if (apiIndex == AppConstants.API_INDEX.UPDATE_CROP) {

                        ArrayList<Crop> newCropList = AppUtils.getParsedDataArrayFromResponse(response, Crop.class);

                        if (newCropList != null && newCropList.size() > 0) {
                            newCrop = newCropList.get(0);
                        }

                    } else {

                        newCrop = AppUtils.getParsedData(response.getData(), Crop.class);

                    }

                    if (newCrop != null)
                        cropObject.setEndDate(newCrop.getEndDate());

                    finishFragment();

                    if (apiIndex == AppConstants.API_INDEX.ADD_CROP)
                        openMyCropsPage();
                    else
                        AppUtils.hideProgressDialog();

                } else if (apiIndex == AppConstants.API_INDEX.UPLOAD_IMAGE) {

                    if (response.getData() != null) {

                        UploadImageResponse res = AppUtils.getParsedData(response.getData(), UploadImageResponse.class);

                        if (res != null) {

                            ArrayList<String> images = cropObject.getImages();
                            ArrayList<ImagePath> imagePath = cropObject.getImagesPath();

                            if (uploadingImageIndex < 0) {
                                images.add(res.getFullPath());

                                ImagePath path = new ImagePath();
                                path.setType(AppConstants.IMAGES.CROPS);
                                path.setPath(res.getFullPath());
                                imagePath.add(path);

                            } else {

                                images.add(uploadingImageIndex - 1, res.getFullPath());

                                ImagePath path = new ImagePath();
                                path.setType(AppConstants.IMAGES.CROPS);
                                path.setPath(res.getFullPath());
                                imagePath.add(uploadingImageIndex - 1, path);

                            }

                            cropObject.setImages(images);
                            cropObject.setImagesPath(imagePath);

                        }

                        if (!uploadImageFileToServer()) {
                            AppUtils.hideProgressDialog();
                        }

                    }

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

    @Override
    public void onException(int apiIndex, Throwable t) {

        Fragment fragment = null;

        if (apiIndex != AppConstants.API_INDEX.ADD_CROP
                && apiIndex != AppConstants.API_INDEX.UPDATE_CROP
                && apiIndex != AppConstants.API_INDEX.UPLOAD_IMAGE) {
            fragment = CropAddFragment.this;
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

    private void setBindingData(FragmentAddCropBinding binding) {

        binding.setHandler(CropAddFragment.this);
        binding.setItemClickListener(itemClickListener);

        weightUnits = getResources().getStringArray(R.array.weight_units);
        timePeriods = getResources().getStringArray(R.array.time_period_array);

        initializeObjectForBinding();
        binding.setData(cropObject);

        binding.setWeightUnits(new ArrayList<>(Arrays.asList(weightUnits)));
        binding.setTimePeriodArray(new ArrayList<>(Arrays.asList(timePeriods)));

        supplyAreas = getResources().getStringArray(R.array.supply_areas_array);
        binding.setSupplyAreas(new ArrayList<>(Arrays.asList(supplyAreas)));

        categoriesList = new ArrayList<>();
        binding.setCategories(categoriesList);

        varietiesList = new ArrayList<>();
        binding.setVarieties(varietiesList);

        stateList = new ArrayList<>();
        binding.setStates(stateList);

        districtList = new ArrayList<>();
        binding.setDistricts(districtList);

    }

    private void initializeObjectForBinding() {

        if (AppInstance.appUser == null) {
            AppUtils.initUserObject(activityContext);
        }

        cropObject = new Crop();
        cropObject.setAvailableFromObj(new ItemDateObject());
        cropObject.setSeller(AppInstance.appUser.getId());
        cropObject.setAvailableUnit(timePeriods[0]);
        cropObject.setQuantityUnit(weightUnits[0]);
        cropObject.setSupplyAbility(AppConstants.API_STRING_CONSTANTS.NO);
        cropObject.setPaymentPreference(AppConstants.API_STRING_CONSTANTS.COD);
        cropObject.setImages(new ArrayList<String>());
        cropObject.setImagesPath(new ArrayList<ImagePath>());

    }

    private void initViews(FragmentAddCropBinding binding) {

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
        distanceEt = binding.distanceEt;

        categorySpinner = binding.categorySpinner;
        varietySpinner = binding.varietySpinner;
        stateSpinner = binding.stateSpinner;
        districtSpinner = binding.districtSpinner;
        quantityUnitSpinner = binding.quantityUnitSpinner;
        timePeriodUnitSpinner = binding.timePeriodSpinner;
        supplyAreaSpinner = binding.supplyAreaSpinner;

        qualityRg = binding.qualityRg;
        supplyAvailabilityRg = binding.supplyAvailabilityRg;
        paymentModeRg = binding.paymentRg;

    }

    public void bindData() {

        varietiesList.add("");
        districtList.add("");
        statesList = DBSource.getInstance(activityContext).getStates();

        if (AppUtils.isNetworkAvailable(activityContext, CropAddFragment.this)) {

            if (statesList == null || statesList.size() == 0) {

                getStateListing(CropAddFragment.this);

            } else {

                setSpinnerStatesListing(false);
                getCategoryListing(AppConstants.CATEGORY_TYPE.CROPS, CropAddFragment.this);

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
        AppUtils.addTextChangedListener(distanceEt);

        nameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    nameTextInputLayout.setHint(getString(R.string.crop_name));
                } else {
                    nameTextInputLayout.setHint(getString(R.string.crop_name_with_example));
                }
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
                    cropObject.setVariety(null);
                    getVarietyArrayList(categoriesList.get(position));
                    cropObject.setCategory(categories.get(position).getId());

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
                    cropObject.setVariety(varietiesList.get(position));
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
                    cropObject.setDistrict(null);

                    getDistrictArrayList(stateList.get(position));
                    cropObject.setState(stateList.get(position));

                    if (!TextUtils.isEmpty(cropObject.getDistrict())) {

                        for (int i = 0; i < districtList.size(); i++) {

                            if (cropObject.getDistrict().equals(districtList.get(i))) {
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
                    cropObject.setDistrict(districtList.get(position));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        quantityUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (manuallySelectedSpinnerValue
                        && weightUnits != null
                        && position >= 0 && position < weightUnits.length) {
                    manuallySelectedSpinnerValue = false;
                    cropObject.setQuantityUnit(weightUnits[position]);
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
                    cropObject.setAvailableUnit(timePeriods[position]);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        supplyAreaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                position = position - 1;

                if (manuallySelectedSpinnerValue
                        && position >= 0
                        && position < supplyAreas.length) {

                    manuallySelectedSpinnerValue = false;
                    cropObject.setSupplyArea(supplyAreas[position]);

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

        setOnTouchListener(quantityUnitSpinner);
        setOnTouchListener(categorySpinner);
        setOnTouchListener(stateSpinner);
        setOnTouchListener(timePeriodUnitSpinner);
        setOnTouchListener(supplyAreaSpinner);

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

        qualityRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId) {

                    case R.id.quality_a_plus_rb:
                        cropObject.setGrade(getString(R.string.quality_a_plus));
                        break;

                    case R.id.quality_a_rb:
                        cropObject.setGrade(getString(R.string.quality_a));
                        break;

                    case R.id.quality_b_rb:
                        cropObject.setGrade(getString(R.string.quality_b));
                        break;

                    case R.id.quality_c_rb:
                        cropObject.setGrade(getString(R.string.quality_c));
                        break;

                    case R.id.quality_d_rb:
                        cropObject.setGrade(getString(R.string.quality_d));
                        break;

                }

            }
        });

        supplyAvailabilityRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == R.id.yes_radio) {
                    cropObject.setSupplyAbility(AppConstants.API_STRING_CONSTANTS.YES);
                } else {
                    cropObject.setSupplyAbility(AppConstants.API_STRING_CONSTANTS.NO);
                }

            }
        });

        paymentModeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == R.id.cod_rb) {
                    cropObject.setPaymentPreference(AppConstants.API_STRING_CONSTANTS.COD);
                } else if (checkedId == R.id.cheque_rb) {
                    cropObject.setPaymentPreference(AppConstants.API_STRING_CONSTANTS.CHEQUE);
                } else {
                    cropObject.setPaymentPreference(AppConstants.API_STRING_CONSTANTS.NET_BANKING);
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

    private void prepopulateDataForEditCrop() {

        if (editCrop) {

            try {

                nameTextInputLayout.setHint(getString(R.string.crop_name));
                cropObject = editCropObject;
                cropObject.setSeller(cropObject.getUser().getId());
                cropObject.setCategory(cropObject.getCategoryObj().getId());
                setImagesPath();
                binding.setData(cropObject);

                int index = getSelectedCategoryIndex();
                if (index >= 0) {
                    getVarietyArrayList(categoriesList.get(index));
                    cropObject.setCategory(categories.get(index).getId());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            cropObject.setState(AppInstance.appUser.getState());
            cropObject.setDistrict(AppInstance.appUser.getDistrict());
            cropObject.setCity(AppInstance.appUser.getCity());
            cropObject.setAddress(AppInstance.appUser.getAddress());
            cropObject.setPincode(AppInstance.appUser.getPincode());

        }

        getDistrictArrayList(cropObject.getState());

    }

    private void setImagesPath() {

        if (cropObject.getImages() != null && cropObject.getImages().size() > 0) {

            ArrayList<ImagePath> imagesPath = cropObject.getImagesPath();

            if (imagesPath == null) {
                imagesPath = new ArrayList<>();
            }

            imagesPath.clear();

            for (int i = 0; i < cropObject.getImages().size(); i++) {

                ImagePath path = new ImagePath();
                path.setType(AppConstants.IMAGES.CROPS);
                path.setPath(cropObject.getImages().get(i));
                imagesPath.add(path);

            }

            cropObject.setImagesPath(imagesPath);

        } else {

            cropObject.setImagesPath(new ArrayList<ImagePath>());

        }

    }

    private int getSelectedCategoryIndex() {
        if (categories != null && categories.size() > 0) {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId().equals(cropObject.getCategory())) {
                    categorySpinner.setSelection(i + 1);
                    return i;
                }
            }
        }
        return -1;
    }

    private void openMyCropsPage() {

        if (AppConstants.FragmentConstant.fragment instanceof CropMyCropsFragment) {

            CropMyCropsFragment fragment = (CropMyCropsFragment) AppConstants.FragmentConstant.fragment;
            fragment.refreshItemsList();

        } else {

            MainActivity activity = (MainActivity) activityContext;
            activity.openMyCropsPage(activity);

        }

    }

    private void setSpinnerStatesListing(boolean refreshAdapter) {

        if (statesList != null && statesList.size() > 0) {

            stateList.clear();

            for (int i = 0; i < statesList.size(); i++) {
                stateList.add(statesList.get(i).getStateName());
            }

            if (refreshAdapter) {
                NothingSelectedSpinnerAdapter adapter
                        = (NothingSelectedSpinnerAdapter) stateSpinner.getAdapter();
                ArrayAdapter<String> sAdapter = adapter.getAdapter();
                sAdapter.notifyDataSetChanged();
            }

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

                    if (!TextUtils.isEmpty(cropObject.getVariety())
                            && cropObject.getVariety().equals(varietiesList.get(j))) {
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

        if (TextUtils.isEmpty(cropObject.getName())
                || cropObject.getName().isEmpty()) {

            AppUtils.showTextInputError(
                    (TextInputLayout) nameEt.getParentForAccessibility(),
                    getString(R.string.error_enter_crop_name));
            nameEt.requestFocus();
            scrollView.scrollTo(0, nameEt.getTop());

        } else if (cropObject.getCategory() == null
                || TextUtils.isEmpty(cropObject.getCategory().toString())) {

            AppUtils.showSpinnerErrorMessage(categorySpinner,
                    getContext().getString(R.string.error_select_category));
            scrollView.scrollTo(0, categorySpinner.getTop());

        } else if (TextUtils.isEmpty(cropObject.getQuantity())
                || cropObject.getQuantity().isEmpty()) {

            AppUtils.showTextInputError(
                    (TextInputLayout) quantityEt.getParentForAccessibility(),
                    getString(R.string.error_enter_quantity));
            quantityEt.requestFocus();
            scrollView.scrollTo(0, quantityEt.getTop());

        } else if (TextUtils.isEmpty(cropObject.getPrice())
                || cropObject.getPrice().isEmpty()) {

            AppUtils.showTextInputError(
                    (TextInputLayout) priceEt.getParentForAccessibility(),
                    getString(R.string.error_enter_price));
            priceEt.requestFocus();
            scrollView.scrollTo(0, priceEt.getTop());

        } else if (TextUtils.isEmpty(cropObject.getAddress())
                || cropObject.getAddress().isEmpty()) {

            AppUtils.showTextInputError(
                    (TextInputLayout) addressEt.getParentForAccessibility(),
                    getString(R.string.error_enter_address));
            addressEt.requestFocus();
            scrollView.scrollTo(0, addressEt.getTop());

        } else if (TextUtils.isEmpty(cropObject.getCity())
                || cropObject.getCity().isEmpty()) {

            AppUtils.showTextInputError(
                    (TextInputLayout) cityEt.getParentForAccessibility(),
                    getString(R.string.error_enter_city));
            cityEt.requestFocus();
            scrollView.scrollTo(0, cityEt.getTop());

        } else if (TextUtils.isEmpty(cropObject.getState())) {

            AppUtils.showSpinnerErrorMessage(stateSpinner,
                    getContext().getString(R.string.error_select_state));
            scrollView.scrollTo(0, stateSpinner.getTop());

        } else if (TextUtils.isEmpty(cropObject.getDistrict())) {

            AppUtils.showSpinnerErrorMessage(districtSpinner,
                    getContext().getString(R.string.error_select_district));
            scrollView.scrollTo(0, districtSpinner.getTop());

        } else if (TextUtils.isEmpty(cropObject.getPincode())
                || cropObject.getPincode().isEmpty()) {

            AppUtils.showTextInputError(
                    (TextInputLayout) pincodeEt.getParentForAccessibility(),
                    getString(R.string.error_enter_pincode));
            pincodeEt.requestFocus();
            scrollView.scrollTo(0, pincodeEt.getTop());

        } else if (cropObject.getPincode().length() != 6) {

            AppUtils.showTextInputError(
                    (TextInputLayout) pincodeEt.getParentForAccessibility(),
                    getString(R.string.error_valid_pincode));
            pincodeEt.requestFocus();
            scrollView.scrollTo(0, pincodeEt.getTop());

        } else if (TextUtils.isEmpty(cropObject.getAvailableFromObj().getFormatted())) {

            availabilityDateTv.setText(getString(R.string.error_select_date));
            availabilityDateTv.setTextColor(Color.RED);
            scrollView.scrollTo(0, availabilityDateLl.getTop());

        }
//        else if (TextUtils.isEmpty(cropObject.getAvailablePeriod())
//                || cropObject.getAvailablePeriod().isEmpty()) {
//
//            AppUtils.showTextInputError(
//                    (TextInputLayout) timePeriodEt.getParentForAccessibility(),
//                    getString(R.string.error_enter_time_period));
//            timePeriodEt.requestFocus();
//            scrollView.scrollTo(0, timePeriodEt.getTop());
//
//        }
        else if (
                cropObject.getSupplyAbility().equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.YES)
                        && TextUtils.isEmpty(cropObject.getSupplyArea())) {

            AppUtils.showSpinnerErrorMessage(supplyAreaSpinner,
                    getContext().getString(R.string.error_select_supply_area));
            scrollView.scrollTo(0, supplyAreaSpinner.getTop());

        } else if (
                cropObject.getSupplyAbility().equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.YES)
                        && (TextUtils.isEmpty(cropObject.getSupplyRange())
                        || cropObject.getSupplyRange().isEmpty())) {

            AppUtils.showTextInputError(
                    (TextInputLayout) distanceEt.getParentForAccessibility(),
                    getString(R.string.error_enter_distance));
            distanceEt.requestFocus();
            scrollView.scrollTo(0, distanceEt.getTop());

        } else {

            if (TextUtils.isEmpty(cropObject.getAvailablePeriod())
                    || cropObject.getAvailablePeriod().isEmpty()) {
                cropObject.setAvailablePeriod(null);
            }
            publishItem();

        }

    }

    private void publishItem() {

        if (AppUtils.isNetworkAvailable(activityContext, true, 101)) {

            if (cropObject.getSupplyAbility().equals(AppConstants.API_STRING_CONSTANTS.NO)) {
                cropObject.setSupplyArea("");
                cropObject.setSupplyRange("0");
            }

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            try {
                String json = ow.writeValueAsString(cropObject);
                AppLogger.log("CropAddFragment", "" + json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            int apiIndex;

            if (editCrop) {

                apiIndex = AppConstants.API_INDEX.UPDATE_CROP;

            } else {

                apiIndex = AppConstants.API_INDEX.ADD_CROP;

            }

            ApiManager.callService(activityContext,
                    apiIndex,
                    CropAddFragment.this,
                    true,
                    cropObject,
                    getUserAccessToken());

        }

    }

    /**
     * opens the Image sources dialog from which user either select Camera or Gallery option.
     */
    private void openAddImagesSourcesDialog() {

        if (cropObject.getImages().size() < AppConstants.CustomGallery.MAXIMUM_IMAGES) {

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
                = AppConstants.CustomGallery.MAXIMUM_IMAGES - cropObject.getImages().size();

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

        if (cropObject.getImages().size() < AppConstants.CustomGallery.MAXIMUM_IMAGES) {

            ImagePath imagePath = new ImagePath();
            imagePath.setPath(path);
            imagePath.setType(AppConstants.IMAGES.CROPS);

            publishImage(imagePath);

        }

    }

    /**
     * In this we got selected images path from Gallery
     *
     * @param paths images gallery path
     */
    public void fetchedImagesFromGallery(String[] paths) {

        if (cropObject.getImages().size() < AppConstants.CustomGallery.MAXIMUM_IMAGES) {

            int remainingImages
                    = AppConstants.CustomGallery.MAXIMUM_IMAGES - cropObject.getImages().size();

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
            path.setType(AppConstants.IMAGES.CROPS);
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
            ApiManager.callService(
                    activityContext,
                    AppConstants.API_INDEX.UPLOAD_IMAGE,
                    CropAddFragment.this,
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

            NothingSelectedSpinnerAdapter adapter
                    = (NothingSelectedSpinnerAdapter) categorySpinner.getAdapter();
            if (adapter != null) {
                ArrayAdapter<String> sAdapter = adapter.getAdapter();
                sAdapter.notifyDataSetChanged();
            }

        }

    }

    @Override
    public void onNetworkYesClicked() {
        finishFragment();
    }

}
