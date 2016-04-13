/*
 * Copyright (C) 2016 Peter Gregus for GravityBox Project (C3C076@xda)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ceco.gm2.gravitybox.shortcuts;

import java.util.ArrayList;
import java.util.List;

import com.ceco.gm2.gravitybox.ConnectivityServiceWrapper;
import com.ceco.gm2.gravitybox.R;
import com.ceco.gm2.gravitybox.adapters.IIconListAdapterItem;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class BluetoothShortcut extends AMultiShortcut {
    protected static final String ACTION =  ConnectivityServiceWrapper.ACTION_TOGGLE_BLUETOOTH;

    public BluetoothShortcut(Context context) {
        super(context);
    }

    @Override
    public String getText() {
        return mContext.getString(R.string.shortcut_bluetooth);
    }

    @Override
    public Drawable getIconLeft() {
        return mResources.getDrawable(R.drawable.shortcut_bluetooth);
    }

    @Override
    protected String getAction() {
        return ACTION;
    }

    @Override
    protected String getShortcutName() {
        return getText();
    }

    @Override
    protected List<IIconListAdapterItem> getShortcutList() {
        final List<IIconListAdapterItem> list = new ArrayList<>();
        list.add(new ShortcutItem(mContext, R.string.shortcut_bluetooth,
                R.drawable.shortcut_bluetooth, null));
        list.add(new ShortcutItem(mContext, R.string.bluetooth_on,
                R.drawable.shortcut_bluetooth_enable, new ExtraDelegate() {
                @Override
                public void addExtraTo(Intent intent) {
                    intent.putExtra(EXTRA_ENABLE, true);
                }
        }));
        list.add(new ShortcutItem(mContext, R.string.bluetooth_off,
                R.drawable.shortcut_bluetooth_disable, new ExtraDelegate() {
                @Override
                public void addExtraTo(Intent intent) {
                    intent.putExtra(EXTRA_ENABLE, false);
                }
        }));

        return list;
    }

    public static void launchAction(final Context context, Intent intent) {
        Intent launchIntent = new Intent(ACTION);
        if (intent.hasExtra(EXTRA_ENABLE)) {
            launchIntent.putExtra(EXTRA_ENABLE, intent.getBooleanExtra(EXTRA_ENABLE, false));
        }
        context.sendBroadcast(launchIntent);
    }
}
