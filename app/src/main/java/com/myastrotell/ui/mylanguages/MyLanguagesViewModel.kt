package com.myastrotell.ui.mylanguages

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myastrotell.base.BaseViewModel
import com.myastrotell.data.ApiRequestCodes
import com.myastrotell.data.database.entities.SelectedLanguagesEntity
import com.myastrotell.pojo.response.KeyDataResponse
import kotlinx.coroutines.launch
import java.util.ArrayList


class MyLanguagesViewModel : BaseViewModel() {
    private val mRepo = MyLanguageRepo()

    val selectedLanguagesLiveData by lazy { MutableLiveData<List<SelectedLanguagesEntity>>() }

    val languageListLiveData by lazy { MutableLiveData<List<KeyDataResponse>>() }


    fun getSelectedLanguages() {
        viewModelScope.launch {
            selectedLanguagesLiveData.value = mRepo.getSelectedLanguages()
        }
    }


    fun getLanguageList() {
        if (navigator?.isNetworkAvailable() == true) {
            navigator?.showProgressBar()

            viewModelScope.launch {
                mRepo.getLanguageList(
                    ApiRequestCodes.GET_KEY_VALUE,
                    languageListLiveData,
                    errorLiveData
                )
            }

        } else {
            navigator?.showNoNetworkError()
        }
    }


    fun updatedSelectedLanguage(mLanguageList: ArrayList<KeyDataResponse>) {
        viewModelScope.launch {
            mRepo.clearLanguages()

            mLanguageList.forEach { item ->

                if(item.isSelected){
                    val entity = SelectedLanguagesEntity()
                    entity.id = item.id
                    entity.key = item.key
                    entity.value = item.value

                    mRepo.addLanguage(entity)
                }

            }
        }
    }

}