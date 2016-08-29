package user.test.ex.diceraider0;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by INC-B-05 on 2016-04-22.
 */
public abstract class WRHandler<T> extends Handler {

    WeakReference<T> wref;

    public WRHandler(T reference) {
        wref = new WeakReference<>(reference);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        T ref = (wref != null) ?
                wref.get() : null;

        if (ref == null) {
            //메시지 넘기지 않고 종료
            return;
        }

        handleMessage(ref, msg);
    }

    protected abstract void handleMessage(T ref, Message msg);
}
