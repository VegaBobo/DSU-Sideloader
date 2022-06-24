package vegabobo.dsusideloader.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

object Toggle {
    const val USERDATA_TOGGLE = 0
    const val IMGSIZE_TOGGLE = 1
}

class HomeViewModel : ViewModel() {

    fun onClickSelectFile() {
        TODO("Not yet implemented")
    }

    val _userdataSizeToggle = MutableLiveData(false)
    val userdataToggle: LiveData<Boolean> = _userdataSizeToggle

    val _imageSizeToggle = MutableLiveData(false)
    val imageSizeToggle: LiveData<Boolean> = _imageSizeToggle

    fun onTouchToggle(toggle: Int) {
        toggle(
            when (toggle) {
                Toggle.USERDATA_TOGGLE -> _userdataSizeToggle
                Toggle.IMGSIZE_TOGGLE -> _imageSizeToggle
                else -> MutableLiveData(false)
            }
        )
    }

    private fun toggle(m: MutableLiveData<Boolean>) {
        m.value = m.value!!.not()
    }

}