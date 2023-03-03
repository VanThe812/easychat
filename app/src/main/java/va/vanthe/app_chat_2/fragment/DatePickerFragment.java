package va.vanthe.app_chat_2.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import va.vanthe.app_chat_2.R;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Lấy ngày hiện tại để đặt làm ngày mặc định
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Tạo một đối tượng DatePickerDialog và trả về nó
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int day) {
        // Khi người dùng chọn ngày, hiển thị nó trên EditText
        android.widget.EditText dateEditText = getActivity().findViewById(R.id.inputDateOfBirth);
        String sday, smonth;
        month = month + 1;
        if (day<10)
            sday = "0"+day;
        else
            sday = ""+day;

        if (month<10)
            smonth = "0"+month;
        else
            smonth = ""+month;

        dateEditText.setText(sday + "/" + smonth+ "/" + year);
    }
}





