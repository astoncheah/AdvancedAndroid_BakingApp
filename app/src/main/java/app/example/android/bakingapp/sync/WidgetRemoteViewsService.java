package app.example.android.bakingapp.sync;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by cheah on 1/6/17.
 */
public class WidgetRemoteViewsService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(getApplicationContext(),intent);
    }
}
