package uab.cs422.projectinlook.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uab.cs422.projectinlook.databinding.BottomSheetDeletionBinding

class DeletionBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetDeletionBinding
    var neutralAction: () -> Unit = {}
    var deleteAction: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetDeletionBinding.inflate(layoutInflater)
        binding.btnCancel.setOnClickListener {
            neutralAction()
        }
        binding.btnDelete.setOnClickListener {
            deleteAction()
        }
        
        return binding.root
    }

    companion object {
        const val TAG = "DeletionBottomSheet"
    }
}