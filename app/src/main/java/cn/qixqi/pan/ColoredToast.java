package cn.qixqi.pan;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.IntDef;

public class ColoredToast extends Toast {

    @IntDef(value = {
            LENGTH_SHORT,
            LENGTH_LONG
    })
    @interface Duration{}

    public ColoredToast(Context context){
        super(context);
    }



}
