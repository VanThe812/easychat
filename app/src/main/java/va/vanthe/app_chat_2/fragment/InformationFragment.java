package va.vanthe.app_chat_2.fragment;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.LayoutFragmentFriendBinding;
import va.vanthe.app_chat_2.databinding.LayoutFragmentInformationBinding;

public class InformationFragment extends Fragment {

    private LayoutFragmentInformationBinding binding;

    public static InformationFragment newInstance() {
        return new InformationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = LayoutFragmentInformationBinding.inflate(inflater, container, false);

//        binding.inputDateOfBirth.setOnClickListener(view -> {
//            showDatePickerDialog();
//        });
        binding.inputDateOfBirth.setOnFocusChangeListener((view, b) -> {
            if (b) {
                showDatePickerDialog();
                binding.inputDateOfBirth.clearFocus();
            }
        });
        return binding.getRoot();
    }

        public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }
}